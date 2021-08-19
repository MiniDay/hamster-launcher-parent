package cn.hamster3.application.launcher.controller;

import cn.hamster3.application.launcher.Bootstrap;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class ProgressListController {
    public VBox progressBarList;

    private ExecutorService service;

    public void init(ExecutorService service) {
        this.service = service;
    }

    public ProgressBarController createProgressBar(String name) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Bootstrap.class.getResource("/fxml/ProgressBar.fxml"));
        try {
            Node node = loader.load();
            Platform.runLater(() -> progressBarList.getChildren().add(0, node));
            ProgressBarController controller = loader.getController();
            controller.setName(name);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onCancel() {
        service.shutdownNow();
        Platform.runLater(() -> progressBarList.getScene().getWindow().hide());
    }
}
