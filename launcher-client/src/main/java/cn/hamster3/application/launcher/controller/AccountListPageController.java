package cn.hamster3.application.launcher.controller;

import cn.hamster3.application.launcher.Bootstrap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import cn.hamster3.application.launcher.entity.auth.AccountProfile;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AccountListPageController implements Initializable {
    public VBox accountListPane;

    private double dragX;
    private double dragY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    private void init() {
        accountListPane.getChildren().clear();
        LaunchOptions options = LaunchOptions.getInstance();
        for (AccountProfile profile : options.getProfiles()) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Bootstrap.class.getResource("/fxml/Account.fxml"));
                accountListPane.getChildren().add(loader.load());
                AccountController controller = loader.getController();
                controller.setAccount(profile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onAddAccount() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Bootstrap.class.getResource("/fxml/AddAccountPage.fxml"));
            Scene scene = new Scene(loader.load());
            Stage addAccountStage = new Stage(StageStyle.UNDECORATED);
            addAccountStage.setScene(scene);
            addAccountStage.initOwner(accountListPane.getScene().getWindow());
            addAccountStage.initModality(Modality.WINDOW_MODAL);
            addAccountStage.show();
            addAccountStage.setOnHidden(event -> {
                init();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickClose() {
        accountListPane.getScene().getWindow().hide();
    }

    @FXML
    public void onMousePressed(MouseEvent event) {
        Window window = accountListPane.getScene().getWindow();
        dragX = window.getX() - event.getScreenX();
        dragY = window.getY() - event.getScreenY();
    }

    @FXML
    public void onMouseDragged(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        Window window = accountListPane.getScene().getWindow();
        window.setX(event.getScreenX() + dragX);
        window.setY(event.getScreenY() + dragY);
    }

    @FXML
    public void onTouchMoved(TouchEvent event) {
        Window window = accountListPane.getScene().getWindow();
        window.setX(event.getTouchPoint().getScreenX() + dragX);
        window.setY(event.getTouchPoint().getScreenY() + dragY);
    }

    @FXML
    public void onMouseReleased() {
        dragX = -1;
        dragY = -1;
    }
}
