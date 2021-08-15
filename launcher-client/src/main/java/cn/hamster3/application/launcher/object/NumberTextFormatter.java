package cn.hamster3.application.launcher.object;

import javafx.scene.control.TextFormatter;

public class NumberTextFormatter extends TextFormatter<TextFormatter.Change> {
    public NumberTextFormatter() {
        super(change -> {
            String text = change.getText();
            if (text.length() > 4) {
                return null;
            }
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        });
    }
}
