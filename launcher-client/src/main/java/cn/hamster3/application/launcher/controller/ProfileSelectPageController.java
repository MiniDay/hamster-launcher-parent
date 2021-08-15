package cn.hamster3.application.launcher.controller;

import cn.hamster3.application.launcher.Bootstrap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import cn.hamster3.application.launcher.entity.auth.AccountProfile;

import java.io.IOException;
import java.util.ArrayList;

public class ProfileSelectPageController {
    public VBox profileListPane;

    private double dragX;
    private double dragY;

    private Window parentStage;

    public void setParentStage(Window parentStage) {
        this.parentStage = parentStage;
    }

    public void setAccountProfiles(ArrayList<AccountProfile> accountProfiles) {
        for (AccountProfile profile : accountProfiles) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Bootstrap.class.getResource("/fxml/AddProfile.fxml"));
                profileListPane.getChildren().add(loader.load());
                AddProfileController controller = loader.getController();
                controller.setParentStage(parentStage);
                controller.setProfile(profile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onClickClose() {
        profileListPane.getScene().getWindow().hide();
    }

    @FXML
    public void onMousePressed(MouseEvent event) {
        Window window = profileListPane.getScene().getWindow();
        dragX = window.getX() - event.getScreenX();
        dragY = window.getY() - event.getScreenY();
    }

    @FXML
    public void onMouseDragged(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        Window window = profileListPane.getScene().getWindow();
        window.setX(event.getScreenX() + dragX);
        window.setY(event.getScreenY() + dragY);
    }

    @FXML
    public void onTouchMoved(TouchEvent event) {
        Window window = profileListPane.getScene().getWindow();
        window.setX(event.getTouchPoint().getScreenX() + dragX);
        window.setY(event.getTouchPoint().getScreenY() + dragY);
    }

    @FXML
    public void onMouseReleased() {
        dragX = -1;
        dragY = -1;
    }

    public void onCancel() {
        profileListPane.getScene().getWindow().hide();
    }
}
