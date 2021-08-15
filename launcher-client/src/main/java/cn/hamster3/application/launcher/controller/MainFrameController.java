package cn.hamster3.application.launcher.controller;

import cn.hamster3.application.launcher.Bootstrap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import cn.hamster3.application.launcher.util.LauncherUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainFrameController implements Initializable {
    @FXML
    public Pane framePane;
    @FXML
    public AnchorPane sidebarPane;
    @FXML
    public AnchorPane mainPane;

    private double dragX;
    private double dragY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Bootstrap.class.getResource("/fxml/SidebarPage.fxml"));
            Node node = loader.load();
            sidebarPane.getChildren().add(node);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File backgroundFolder = LauncherUtils.getBackgroundDirectory();
        File[] files = backgroundFolder.listFiles();
        if (files != null && files.length > 0) {
            File backgroundFile = files[(int) (files.length * Math.random())];
            Platform.runLater(() -> framePane.setBackground(new Background(
                    new BackgroundImage(
                            new Image(backgroundFile.toURI().toString()),
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.DEFAULT,
                            new BackgroundSize(
                                    BackgroundSize.AUTO,
                                    BackgroundSize.AUTO,
                                    true,
                                    true,
                                    true,
                                    true
                            )
                    )
            )));
        }
    }

    @FXML
    public void onMousePressed(MouseEvent event) {
        Stage stage = Bootstrap.getStage();
        dragX = stage.getX() - event.getScreenX();
        dragY = stage.getY() - event.getScreenY();
    }

    @FXML
    public void onMouseDragged(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        Stage stage = Bootstrap.getStage();
        stage.setX(event.getScreenX() + dragX);
        stage.setY(event.getScreenY() + dragY);
    }

    @FXML
    public void onTouchMoved(TouchEvent event) {
        Stage stage = Bootstrap.getStage();
        stage.setX(event.getTouchPoint().getScreenX() + dragX);
        stage.setY(event.getTouchPoint().getScreenY() + dragY);
    }

    @FXML
    public void onMouseReleased() {
        dragX = -1;
        dragY = -1;
    }

    @FXML
    public void onClickClose() {
        Platform.exit();
    }
}
