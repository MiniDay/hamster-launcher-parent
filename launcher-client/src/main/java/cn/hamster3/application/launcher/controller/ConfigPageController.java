package cn.hamster3.application.launcher.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;
import cn.hamster3.application.launcher.object.NumberTextFormatter;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigPageController implements Initializable {
    public VBox configPane;

    public TextField javaPathField;
    public TextField customJvmArgumentsField;
    public TextField minMemoryField;
    public TextField maxMemoryField;

    public CheckBox customScreenSizeCheckBox;
    public TextField widthField;
    public TextField heightField;

    private double dragX;
    private double dragY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LaunchOptions options = LaunchOptions.getInstance();

        minMemoryField.setTextFormatter(new NumberTextFormatter());
        maxMemoryField.setTextFormatter(new NumberTextFormatter());
        widthField.setTextFormatter(new NumberTextFormatter());
        heightField.setTextFormatter(new NumberTextFormatter());

        javaPathField.setText(options.getJavaPath());
        if (options.getCustomJvmArguments() != null) {
            customJvmArgumentsField.setText(options.getCustomJvmArguments());
        }

        minMemoryField.setText(options.getMinMemory());
        maxMemoryField.setText(options.getMaxMemory());

        customScreenSizeCheckBox.setSelected(options.getHasCustomResolution());
        if (options.getResolutionWidth() != null) {
            widthField.setText(options.getResolutionWidth());
        }
        if (options.getResolutionHeight() != null) {
            heightField.setText(options.getResolutionHeight());
        }
    }

    public void onCancel() {
        configPane.getScene().getWindow().hide();
    }

    public void onSave() {
        LaunchOptions options = LaunchOptions.getInstance();
        File file = new File(javaPathField.getText());
        if (file.exists()) {
            options.setJavaPath(file.getAbsolutePath());
        }

        String minMemory = minMemoryField.getText();
        if (minMemory.length() > 0) {
            options.setMinMemory(minMemory);
        }

        String maxMemory = maxMemoryField.getText();
        if (maxMemory.length() > 0) {
            options.setMaxMemory(maxMemory);
        }

        String width = widthField.getText();
        if (width.length() > 0) {
            options.setResolutionWidth(width);
        }

        String height = heightField.getText();
        if (height.length() > 0) {
            options.setResolutionHeight(height);
        }

        String customJvmArguments = customJvmArgumentsField.getText();
        if (customJvmArguments.trim().length() > 0) {
            options.setCustomJvmArguments(customJvmArguments);
        }

        options.setHasCustomResolution(customScreenSizeCheckBox.isSelected());
        options.save();
        configPane.getScene().getWindow().hide();
    }

    @FXML
    public void onMousePressed(MouseEvent event) {
        Window window = configPane.getScene().getWindow();
        dragX = window.getX() - event.getScreenX();
        dragY = window.getY() - event.getScreenY();
    }

    @FXML
    public void onMouseDragged(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        Window window = configPane.getScene().getWindow();
        window.setX(event.getScreenX() + dragX);
        window.setY(event.getScreenY() + dragY);
    }

    @FXML
    public void onTouchMoved(TouchEvent event) {
        Window window = configPane.getScene().getWindow();
        window.setX(event.getTouchPoint().getScreenX() + dragX);
        window.setY(event.getTouchPoint().getScreenY() + dragY);
    }

    @FXML
    public void onMouseReleased() {
        dragX = -1;
        dragY = -1;
    }
}
