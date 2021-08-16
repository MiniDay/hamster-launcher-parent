package cn.hamster3.application.launcher.util;

import cn.hamster3.application.launcher.Bootstrap;
import cn.hamster3.application.launcher.constant.AuthenticationType;
import cn.hamster3.application.launcher.controller.ProgressBarController;
import cn.hamster3.application.launcher.controller.ProgressBarListController;
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
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public abstract class LaunchUtils {

    @SuppressWarnings("SpellCheckingInspection")
    public static CompletableFuture<Void> launchGame(
            SidebarPageController pageController,
            ProgressBarListController progressBarList
    ) throws Exception {
        long start = System.currentTimeMillis();
        LaunchOptions options = LaunchOptions.getInstance();

        AccountProfile selectedProfile = options.getSelectedProfile();
        if (selectedProfile == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("请登录账号");
            alert.setHeaderText("请登录账号");
            alert.setContentText("你必须先登录账号才能进入游戏!");
            alert.show();
            long end = System.currentTimeMillis();
            System.out.println("启动操作中断，耗时: " + (end - start) + " ms");
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.complete(null);
            return future;
        }
        ArrayList<CompletableFuture<?>> futures = new ArrayList<>();

        AuthenticationType type = selectedProfile.getType();
        if (type != AuthenticationType.OFFICIAL) {
            ProgressBarController downloadAuthLibProgress = progressBarList.createProgressBar("安装 authlib-injector...");
            futures.add(
                    downloadAuthlibInjector()
                            .whenComplete((file, throwable) -> downloadAuthLibProgress.setProgress(1))
            );
        }

        ProgressBarController validateProgress = progressBarList.createProgressBar("验证账户");
        futures.add(
                type.postValidate(selectedProfile)
                        .thenAccept(success -> {
                            if (success) {
                                return;
                            }
                            System.out.println("账户验证失败.");
                            JsonObject object;
                            try {
                                object = type.postRefresh(selectedProfile, false);
                            } catch (IOException e) {
                                e.printStackTrace();
                                pageController.showRelistPage(options, selectedProfile);
                                return;
                            }
                            String accessToken = object.get("accessToken").getAsString();
                            String clientToken = object.get("clientToken").getAsString();
                            selectedProfile.setAccessToken(accessToken);
                            selectedProfile.setClientToken(clientToken);
                            System.out.println("令牌刷新成功.");
                            options.save();
                        })
                        .whenComplete((file, throwable) -> validateProgress.setProgress(1))
        );

        File minecraftFolder = LauncherUtils.getMinecraftFolder();
        System.out.println(minecraftFolder.getAbsolutePath());

        LaunchData launchData = new LaunchData(minecraftFolder, "五彩方块1.17.1", options);
        launchData.generatorNativeLibrary();

        StringArray launchScript = LauncherUtils.getLaunchScript(options, launchData);
        System.out.println(launchScript);
        System.out.println();

        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(launchData.getVersionFolder());
        builder.environment().put("APPDATA", launchData.getMinecraftFolder().getParentFile().getAbsolutePath());
        builder.command(launchScript.getStrings());
        new StreamRedirectThread(builder.start()).start();

        Bootstrap.getStage().hide();

        long end = System.currentTimeMillis();
        System.out.println("启动执行完成，耗时: " + (end - start) + " ms");
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static CompletableFuture<File> downloadAuthlibInjector() {
        CompletableFuture<File> future = new CompletableFuture<>();
        File file = new File(LauncherUtils.getLauncherDirectory(), "authlib-injector-1.1.38.jar");
        if (file.exists()) {
            if (EncryptionUtils.verificationFileSHA256(file, "c79416acc317eaade53307342d894ed6cf787c754282ae9de79e37ca28253941")) {

                future.complete(file);
                return future;
            }
        }
        ThreadUtils.exec(() -> {
            try {
                URL url = new URL("https://bmclapi2.bangbang93.com/mirrors/authlib-injector/artifact/38/authlib-injector-1.1.38.jar");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                int code = connection.getResponseCode();
                System.out.println("获取 authlib-injector 服务器返回状态码: " + code);
                Files.copy(
                        connection.getInputStream(),
                        file.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
                future.complete(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return future;
    }

}
