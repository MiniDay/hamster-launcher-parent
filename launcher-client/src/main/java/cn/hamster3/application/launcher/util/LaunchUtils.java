package cn.hamster3.application.launcher.util;

import cn.hamster3.application.launcher.constant.AuthenticationType;
import cn.hamster3.application.launcher.controller.ProgressBarController;
import cn.hamster3.application.launcher.controller.ProgressListController;
import cn.hamster3.application.launcher.controller.SidebarPageController;
import cn.hamster3.application.launcher.entity.LaunchData;
import cn.hamster3.application.launcher.entity.auth.AccountProfile;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;
import cn.hamster3.application.launcher.object.StringArray;
import cn.hamster3.application.launcher.thread.StreamRedirectThread;
import com.google.gson.JsonObject;
import javafx.scene.control.Alert;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public abstract class LaunchUtils {
    /**
     * 启动游戏
     *
     * @param pageController  -
     * @param progressBarList -
     * @return -
     */
    @SuppressWarnings("SpellCheckingInspection")
    public static CompletableFuture<Void> launchGame(
            SidebarPageController pageController,
            ProgressListController progressBarList,
            ExecutorService executorService
    ) {
        long start = System.currentTimeMillis();

        LaunchOptions options = LaunchOptions.getInstance();
        AccountProfile selectedProfile = options.getSelectedProfile();
        if (selectedProfile == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("请登录账号");
            alert.setHeaderText("请登录账号");
            alert.setContentText("你必须先登录账号才能进入游戏!");
            alert.show();
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.complete(null);
            return future;
        }

        CompletableFuture<?> launchCheckFuture = new CompletableFuture<>();

        AuthenticationType type = selectedProfile.getType();
        CompletableFuture<File> authlibFuture;
        // 验证外置登录库文件完整性
        if (type != AuthenticationType.OFFICIAL) {
            ProgressBarController downloadAuthLibProgress = progressBarList.createProgressBar("安装 authlib-injector...");
            authlibFuture = downloadAuthlibInjector(executorService);
            launchCheckFuture = authlibFuture
                    .whenComplete((file, throwable) -> downloadAuthLibProgress.setProgress(1));
        } else {
            authlibFuture = null;
        }

        // 验证账户令牌
        ProgressBarController validateProgress = progressBarList.createProgressBar("验证账户令牌");
        launchCheckFuture = CompletableFuture.allOf(
                launchCheckFuture,
                validateProfile(pageController, options, executorService)
                        .whenComplete((a, e) -> validateProgress.setProgress(1))
        );

        CompletableFuture<Void> launchFuture = new CompletableFuture<>();
        launchCheckFuture.whenComplete((o, throwable) -> {
            if (throwable != null) {
                launchFuture.completeExceptionally(throwable);
                return;
            }
            try {
                File minecraftFolder = LauncherUtils.getMinecraftFolder();
//                minecraftFolder = new File("C:\\Users\\MiniDay\\Desktop\\五彩方块\\.minecraft");
                System.out.println("游戏文件夹: " + minecraftFolder);

                LaunchData launchData = new LaunchData(minecraftFolder, "五彩方块1.17.1").generatorNativeLibrary(options);

                // 生成启动脚本
                StringArray launchScript = LauncherUtils.getLaunchScript(launchData, options);
                if (authlibFuture != null) {
                    String authlibPath = authlibFuture.join().getAbsolutePath().replace("\\", "/");
                    launchScript.add(1, String.format("-javaagent:%s=%s", authlibPath, type.getApiUrl()));
                }
                System.out.println("启动脚本: " + launchScript);

                // 启动游戏
                ProcessBuilder proccess = new ProcessBuilder();
                proccess.directory(launchData.getVersionFolder());
                proccess.environment().put("APPDATA", launchData.getMinecraftFolder().getParentFile().getAbsolutePath());
                proccess.command(launchScript.getStrings());
                new StreamRedirectThread(proccess.start(), launchFuture).start();

                long end = System.currentTimeMillis();
                System.out.println("启动检测执行完成，耗时: " + (end - start) + " ms");

                ProgressBarController controller = progressBarList.createProgressBar("等待游戏启动.");
                launchFuture.whenComplete((unused, throwable1) -> {
                    controller.setProgress(1);
                    controller.setName("游戏已启动!");
                });
            } catch (IOException e) {
                launchFuture.completeExceptionally(e);
            }
        });
        return launchFuture;
    }

    /**
     * 下载 authlib-injector
     *
     * @return -
     */
    @SuppressWarnings("SpellCheckingInspection")
    public static CompletableFuture<File> downloadAuthlibInjector(ExecutorService executorService) {
        CompletableFuture<File> future = new CompletableFuture<>();
        File file = new File(LauncherUtils.getLauncherDirectory(), "authlib-injector-1.1.38.jar");
        if (file.exists()) {
            if (EncryptionUtils.sha256(file, "c79416acc317eaade53307342d894ed6cf787c754282ae9de79e37ca28253941")) {
                System.out.println("authlib-injector 校验成功!");
                future.complete(file);
                return future;
            }
            System.out.println("authlib-injector 校验失败, 重新下载!");
        }
        executorService.execute(() -> {
            try {
                URL url = new URL("https://bmclapi2.bangbang93.com/mirrors/authlib-injector/artifact/38/authlib-injector-1.1.38.jar");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                int code = connection.getResponseCode();
                System.out.println("获取 authlib-injector 服务器返回状态码: " + code);
                Files.copy(
                        connection.getInputStream(),
                        file.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
                future.complete(file);
            } catch (Exception e) {
                future.completeExceptionally(e);
                e.printStackTrace();
            }
        });
        return future;
    }

    /**
     * 验证账户令牌
     *
     * @return -
     */
    public static CompletableFuture<Void> validateProfile(
            SidebarPageController pageController,
            LaunchOptions options,
            ExecutorService executorService) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        AccountProfile selectedProfile = options.getSelectedProfile();
        AuthenticationType type = selectedProfile.getType();

        executorService.execute(() -> {
            System.out.println("开始验证令牌.");
            try {
                if (type.postValidate(selectedProfile)) {
                    // 如果令牌验证成功则直接完成
                    System.out.println("令牌验证成功!");
                    future.complete(null);
                    return;
                }
            } catch (IOException e) {
                future.completeExceptionally(e);
                return;
            }
            System.out.println("令牌验证失败.");
            // 刷新令牌
            System.out.println("尝试刷新令牌.");
            try {
                JsonObject object = type.postRefresh(selectedProfile, false);
                String accessToken = object.get("accessToken").getAsString();
                String clientToken = object.get("clientToken").getAsString();
                selectedProfile.setAccessToken(accessToken);
                selectedProfile.setClientToken(clientToken);
                System.out.println("令牌刷新成功.");
                options.save();
                // 如果令牌刷新成功则成功
                future.complete(null);
            } catch (IOException e) {
                System.out.println("令牌刷新失败.");
                System.out.println("弹出重新登录界面.");
                pageController.showRelistPage(options, selectedProfile)
                        .whenComplete(
                                (unused, throwable) -> {
                                    if (throwable != null) {
                                        future.completeExceptionally(throwable);
                                    } else {
                                        System.out.println("重新登录成功.");
                                        options.save();
                                        future.complete(unused);
                                    }
                                }
                        );
            }
        });

        return future;
    }

}
