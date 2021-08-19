package cn.hamster3.application.launcher;

import cn.hamster3.application.launcher.util.LauncherUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Bootstrap extends Application {
    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    @SuppressWarnings({"SpellCheckingInspection", "ConstantConditions"})
    public static void main(String[] args) {
        String launcherVersion = LauncherUtils.getLauncherVersion();
        if (!launcherVersion.equalsIgnoreCase("development_version")) {
            try {
                PrintStream stream = new PrintStream(
                        new FileOutputStream("launcher.log", false), true, "UTF-8"
                );
                System.setOut(stream);
                System.setErr(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("http.agent", "HamsterLauncher/" + launcherVersion);
        System.setProperty("javafx.autoproxy.disable", "true");
        File backgroundFolder = LauncherUtils.getBackgroundDirectory();
        if (backgroundFolder.mkdirs()) {
            System.out.println("创建背景文件夹...");
            try {
                Files.copy(
                        Bootstrap.class.getResourceAsStream("/background.png"),
                        new File(backgroundFolder, "background.png").toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("启动 HamsterLauncher: " + launcherVersion);
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Bootstrap.stage = stage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/MainFrame.fxml"));

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("仓鼠发射器");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image("/images/icon.png"));
        stage.show();
    }
}
