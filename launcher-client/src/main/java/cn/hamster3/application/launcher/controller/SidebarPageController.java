package cn.hamster3.application.launcher.controller;

import cn.hamster3.application.launcher.Bootstrap;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import cn.hamster3.application.launcher.constant.AuthenticationType;
import cn.hamster3.application.launcher.entity.LaunchData;
import cn.hamster3.application.launcher.entity.auth.AccountProfile;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;
import cn.hamster3.application.launcher.object.StringArray;
import cn.hamster3.application.launcher.thread.StreamRedirectThread;
import cn.hamster3.application.launcher.util.LauncherUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("SpellCheckingInspection")
public class SidebarPageController implements Initializable {
    public ImageView avator;
    public Label playerName;
    public Label profileType;

    private Stage configStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    @SuppressWarnings("ConstantConditions")
    private void init() {
        LaunchOptions options = LaunchOptions.getInstance();
        AccountProfile profile = options.getSelectedProfile();
        if (profile == null) {
            profileType.setText("请添加账号");
            playerName.setText("##########");
            avator.setImage(new Image(Bootstrap.class.getResourceAsStream("/images/minecraft.png")));
            return;
        }
        profileType.setText(profile.getType().getName());
        playerName.setText(profile.getPlayerName());
        new Thread(() -> {
            if (profile.getPlayerIcon() != null) {
                avator.setImage(profile.getPlayerIconImage());
                if (profile.shouldRefreshIcon()) {
                    profile.refreshPlayerIcon();
                }
            } else {
                profile.refreshPlayerIcon();
            }
            avator.setImage(profile.getPlayerIconImage());
        }).start();
    }

    @FXML
    public void startGame() {
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
            return;
        }

        try {
            AuthenticationType type = selectedProfile.getType();
            if (!type.postValidate(selectedProfile)) {
                System.out.println("账户验证失败.");
                JsonObject object;
                try {
                    object = type.postRefresh(selectedProfile, false);
                } catch (IOException e) {
                    e.printStackTrace();
                    showRelistPage(options, selectedProfile);
                    return;
                }
                String accessToken = object.get("accessToken").getAsString();
                String clientToken = object.get("clientToken").getAsString();
                selectedProfile.setAccessToken(accessToken);
                selectedProfile.setClientToken(clientToken);
                System.out.println("令牌刷新成功.");
                System.out.println("accessToken: " + accessToken);
                System.out.println("clientToken: " + clientToken);
                options.save();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        File minecraftFolder = LauncherUtils.getMinecraftFolder();
        System.out.println(minecraftFolder.getAbsolutePath());
        try {
            LaunchData launchData = new LaunchData(minecraftFolder, "1.17", options);
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        System.out.println("启动执行完成，耗时: " + (end - start) + " ms");
    }

    @FXML
    public void showConfig() {
        if (configStage != null) {
            configStage.show();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Bootstrap.class.getResource("/fxml/ConfigPage.fxml"));
            Scene scene = new Scene(loader.load());
            configStage = new Stage(StageStyle.UNDECORATED);
            configStage.setScene(scene);
            configStage.initOwner(Bootstrap.getStage());
            configStage.initModality(Modality.WINDOW_MODAL);
            configStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickAccount() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Bootstrap.class.getResource("/fxml/AccountListPage.fxml"));
            Scene scene = new Scene(loader.load());
            Stage accountListStage = new Stage(StageStyle.UNDECORATED);
            accountListStage.setScene(scene);
            accountListStage.initOwner(Bootstrap.getStage());
            accountListStage.initModality(Modality.WINDOW_MODAL);
            accountListStage.show();
            accountListStage.setOnHidden(event -> init());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showRelistPage(LaunchOptions options, AccountProfile profile) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Bootstrap.class.getResource("/fxml/RelistPage.fxml"));
            Scene scene = new Scene(loader.load());
            Stage accountListStage = new Stage(StageStyle.UNDECORATED);
            accountListStage.setScene(scene);
            accountListStage.initOwner(Bootstrap.getStage());
            accountListStage.initModality(Modality.WINDOW_MODAL);
            accountListStage.show();
            accountListStage.setOnHidden(event -> init());
            RelistPageController controller = loader.getController();
            controller.setProfile(options, profile);

            accountListStage.setOnHidden(event -> {
                options.save();
                init();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
