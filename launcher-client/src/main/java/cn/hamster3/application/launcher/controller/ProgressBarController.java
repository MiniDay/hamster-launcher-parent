package cn.hamster3.application.launcher.controller;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProgressBarController {
    public Label name;
    public ProgressBar progress;

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setProgress(double progress) {
        this.progress.setProgress(progress);
    }
}
