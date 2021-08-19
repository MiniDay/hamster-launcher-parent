package cn.hamster3.application.launcher.controller;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class ProgressBarController {
    public Label name;
    public ProgressIndicator progress;

    public void setName(String text) {
        Platform.runLater(() -> name.setText(text));
    }

    public void setProgress(double value) {
        Platform.runLater(() -> progress.setProgress(value));
    }
}
