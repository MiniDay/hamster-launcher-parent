package cn.hamster3.application.launcher.controller;

import cn.hamster3.application.launcher.Bootstrap;
import cn.hamster3.application.launcher.entity.auth.AccountProfile;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;
import cn.hamster3.application.launcher.util.LaunchUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Bootstrap.class.getResource("/fxml/ProgressBar.fxml"));
            loader.load();
            Scene scene = new Scene(loader.load());
            Stage configStage = new Stage(StageStyle.UNDECORATED);
            configStage.setScene(scene);
            configStage.initOwner(Bootstrap.getStage());
            configStage.initModality(Modality.WINDOW_MODAL);
            configStage.show();

            LaunchUtils.launchGame(this, loader.getController()).whenComplete(
                    (unused, e) -> {
                        if (e != null) {
                            System.out.println("启动失败!");
                            e.printStackTrace();
                        } else {
                            System.out.println("启动成功!");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void showRelistPage(LaunchOptions options, AccountProfile profile) {
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
