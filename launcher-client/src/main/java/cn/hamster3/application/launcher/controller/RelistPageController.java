package cn.hamster3.application.launcher.controller;

import cn.hamster3.application.launcher.entity.auth.AccountProfile;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class RelistPageController {
    public VBox relistPane;
    public TextField accountField;
    public PasswordField passwordField;

    private LaunchOptions options;
    private AccountProfile profile;
    private CompletableFuture<Void> future;

    public RelistPageController() {
    }

    public void init(LaunchOptions options, AccountProfile profile) {
        this.options = options;
        this.profile = profile;

        accountField.setText(profile.getAccount());
        future = new CompletableFuture<>();
    }

    public void onConfirm() {
        try {
            JsonObject responseParams = profile.getType().postLogin(
                    accountField.getText(),
                    passwordField.getText(),
                    false
            );
            System.out.println("重新登录成功.");
            String accessToken = responseParams.get("accessToken").getAsString();
            String clientToken = responseParams.get("clientToken").getAsString();
            profile.setAccessToken(accessToken);
            profile.setClientToken(clientToken);
//            if (responseParams.has("selectedProfile")) {
//                JsonObject selectedProfile = responseParams.getAsJsonObject("selectedProfile");
//                String id = selectedProfile.get("id").getAsString();
//                String name = selectedProfile.get("name").getAsString();
//                if (!profile.getPlayerUUID().equals(id) || !profile.getPlayerName().equals(name)) {
//                    Alert alert = new Alert(Alert.AlertType.ERROR);
//                    alert.setTitle("角色失效");
//                    alert.setHeaderText("该角色已不可用");
//                    alert.setContentText("账户角色可能已被删除!");
//                    alert.showAndWait();
//                    options.removeProfile(profile);
//                }
//                future.completeExceptionally(new Exception("账户角色已不可用!"));
//                relistPane.getScene().getWindow().hide();
//                return;
//            }
            for (JsonElement element : responseParams.getAsJsonArray("availableProfiles")) {
                JsonObject selectedProfile = element.getAsJsonObject();
                String id = selectedProfile.get("id").getAsString();
                String name = selectedProfile.get("name").getAsString();
                if (profile.getPlayerUUID().equals(id) && profile.getPlayerName().equals(name)) {
                    System.out.println("选取角色: " + name + "(" + id + ")");
                    profile.getType().postRefresh(profile, false);
                    future.complete(null);
                    relistPane.getScene().getWindow().hide();
                    return;
                }
                System.out.println("跳过角色: " + name + "(" + id + ")");
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("角色失效");
            alert.setHeaderText("该角色已不可用");
            alert.setContentText("账户角色可能已被删除!");
            alert.showAndWait();
            options.removeProfile(profile);
            future.completeExceptionally(new Exception("账户角色已不可用!"));
            relistPane.getScene().getWindow().hide();
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
    }

    public CompletableFuture<Void> getFuture() {
        return future;
    }
}
