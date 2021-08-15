package cn.hamster3.application.launcher.controller;

import cn.hamster3.application.launcher.Bootstrap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import cn.hamster3.application.launcher.constant.AuthenticationType;
import cn.hamster3.application.launcher.entity.auth.AccountProfile;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddAccountPageController implements Initializable {
    public VBox addAccountPane;

    public TextField accountField;
    public PasswordField passwordField;
    public ChoiceBox<AuthenticationType> choiceBox;

    private double dragX;
    private double dragY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (AuthenticationType value : AuthenticationType.values()) {
            choiceBox.getItems().add(value);
        }
        choiceBox.getSelectionModel().select(0);

    }

    public void onCancel() {
        addAccountPane.getScene().getWindow().hide();
    }

    public void onLogin() {
        AuthenticationType type = choiceBox.getSelectionModel().getSelectedItem();
        String accountString = accountField.getText();
        try {
            JsonObject object = type.postLogin(accountString, passwordField.getText(), false);
            String accessToken = object.get("accessToken").getAsString();
            String clientToken = object.get("clientToken").getAsString();

            if (object.has("selectedProfile")) {
                JsonObject profileObject = object.getAsJsonObject("selectedProfile");
                String playerName = profileObject.get("name").getAsString();
                String uuid = profileObject.get("id").getAsString();
                AccountProfile account = new AccountProfile(type, accountString, playerName, uuid, accessToken, clientToken);
                LaunchOptions options = LaunchOptions.getInstance();
                options.addProfile(account);
                options.save();
                addAccountPane.getScene().getWindow().hide();
                return;
            }

            ArrayList<AccountProfile> accountProfiles = new ArrayList<>();
            for (JsonElement element : object.getAsJsonArray("availableProfiles")) {
                JsonObject profileObject = element.getAsJsonObject();
                String playerName = profileObject.get("name").getAsString();
                String uuid = profileObject.get("id").getAsString();
                accountProfiles.add(new AccountProfile(
                        type, accountString, playerName, uuid, accessToken, clientToken
                ));
            }
            if (accountProfiles.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("未找到角色");
                alert.setHeaderText("未找到游戏角色！");
                alert.setContentText("你需要先添加一个游戏角色才能继续操作！");
                alert.showAndWait();
                addAccountPane.getScene().getWindow().hide();
                return;
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Bootstrap.class.getResource("/fxml/ProfileSelectPage.fxml"));
            Scene scene = new Scene(loader.load());
            Stage addAccountStage = new Stage(StageStyle.UNDECORATED);
            addAccountStage.setScene(scene);
            addAccountStage.initOwner(addAccountPane.getScene().getWindow());
            addAccountStage.initModality(Modality.WINDOW_MODAL);
            addAccountStage.show();
            ProfileSelectPageController addAccountPage = loader.getController();
            addAccountPage.setParentStage(addAccountPane.getScene().getWindow());
            addAccountPage.setAccountProfiles(accountProfiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onMousePressed(MouseEvent event) {
        Window window = addAccountPane.getScene().getWindow();
        dragX = window.getX() - event.getScreenX();
        dragY = window.getY() - event.getScreenY();
    }

    @FXML
    public void onMouseDragged(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        Window window = addAccountPane.getScene().getWindow();
        window.setX(event.getScreenX() + dragX);
        window.setY(event.getScreenY() + dragY);
    }

    @FXML
    public void onTouchMoved(TouchEvent event) {
        Window window = addAccountPane.getScene().getWindow();
        window.setX(event.getTouchPoint().getScreenX() + dragX);
        window.setY(event.getTouchPoint().getScreenY() + dragY);
    }

    @FXML
    public void onMouseReleased() {
        dragX = -1;
        dragY = -1;
    }
}
