package cn.hamster3.application.launcher.controller;

import cn.hamster3.application.launcher.Bootstrap;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ProgressBarListController {
    public VBox progressBarList;

    public ProgressBarController createProgressBar(String name) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Bootstrap.class.getResource("/fxml/ProgressBar.fxml"));
        try {
            progressBarList.getChildren().add(0, loader.load());
            ProgressBarController controller = loader.getController();
            controller.setName(name);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
