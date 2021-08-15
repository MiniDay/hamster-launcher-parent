package cn.hamster3.application.launcher.controller;

import com.google.gson.JsonObject;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import cn.hamster3.application.launcher.entity.auth.AccountProfile;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;

import java.io.IOException;


public class AddProfileController {
    public ImageView playerIcon;
    public Label playerName;
    public Label type;
    public Label account;

    private AccountProfile profile;

    private Window parentStage;

    public void setParentStage(Window parentStage) {
        this.parentStage = parentStage;
    }

    public void setProfile(AccountProfile profile) {
        this.profile = profile;
        playerName.setText(profile.getPlayerName());
        type.setText(profile.getType().getName());
        account.setText(profile.getAccount());

        new Thread(() -> {
            profile.refreshPlayerIcon();
            playerIcon.setImage(profile.getPlayerIconImage());
        }).start();
    }

    public void onMouseClicked(MouseEvent event) {
        LaunchOptions options = LaunchOptions.getInstance();
        options.addProfile(profile);
        try {
            JsonObject object = profile.getType().postRefresh(profile, false);
            String accessToken = object.get("accessToken").getAsString();
            String clientToken = object.get("clientToken").getAsString();
            profile.setAccessToken(accessToken);
            profile.setClientToken(clientToken);
            System.out.println("令牌刷新成功.");
            System.out.println("accessToken: " + accessToken);
            System.out.println("clientToken: " + clientToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        options.save();

        Node source = (Node) event.getSource();
        source.getScene().getWindow().hide();

        parentStage.hide();
    }

}
