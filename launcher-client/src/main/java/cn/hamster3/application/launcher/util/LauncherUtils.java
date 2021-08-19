package cn.hamster3.application.launcher.util;

import cn.hamster3.application.launcher.Bootstrap;
import cn.hamster3.application.launcher.constant.SystemType;
import cn.hamster3.application.launcher.entity.LaunchData;
import cn.hamster3.application.launcher.entity.auth.AccountProfile;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;
import cn.hamster3.application.launcher.entity.rule.Rule;
import cn.hamster3.application.launcher.object.StringArray;
import com.google.gson.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.jar.JarFile;

public class LauncherUtils {
    public static final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();


    /**
     * 获取游戏本体文件夹所在的位置
     * <p>
     * 当 本程序 jar 文件 放置在桌面时
     * <p>
     * 配置文件会放置在 "~/AppData/Roaming/.minecraft" 文件夹
     * <p>
     * 否则会放置在同级文件夹下的 ".minecraft" 文件夹
     *
     * @return 游戏本体文件夹所在的位置
     */
    public static File getMinecraftFolder() {
        String userHome = System.getProperty("user.home");
        File workDirectory = new File(System.getProperty("user.dir"));

        if (workDirectory.equals(new File(System.getProperty("user.home"), "Desktop"))) {
            if (SystemType.getSystemType() == SystemType.WINDOWS) {
                return new File(userHome, "AppData/Roaming/.minecraft");
            } else {
                return new File(userHome, ".minecraft");
            }
        } else {
            return new File(workDirectory, ".minecraft");
        }
    }

    /**
     * 获取启动器文件所在的位置
     * <p>
     * 当 本程序 jar 文件 放置在桌面时
     * <p>
     * 配置文件会放置在 "~/.HamsterLauncher" 文件夹
     * <p>
     * 否则会放置在同级文件夹下
     *
     * @return 配置文件所在的位置
     */
    public static File getLauncherDirectory() {
        File workDirectory = new File(System.getProperty("user.dir"));
        if (workDirectory.getName().equals("Desktop")) {
            return SystemType.getWorkingDirectory("HamsterLauncher").toFile();
        } else {
            return workDirectory;
        }
    }

    public static File getBackgroundDirectory() {
        return new File(getLauncherDirectory(), "backgrounds");
    }

    public static File getLauncherJarFile() throws URISyntaxException {
        return new File(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    }

    public static String getJavaHomePath() {
        return System.getProperty("java.home").replace("\\", "/");
    }

    public static String getLauncherVersion() {
        if (System.getProperties().containsKey("launcher-version")) {
            return System.getProperty("launcher-version");
        }
        try {
            JarFile file = new JarFile(getLauncherJarFile());
            String value = file.getManifest().getMainAttributes().getValue("launcher-version");
            System.setProperty("launcher-version", value);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return "development_version";
        }
    }

    /**
     * 获取启动脚本
     *
     * @param options    启动选项
     * @param launchData 启动数据
     * @return 启动脚本
     */
    public static StringArray getLaunchScript(LaunchData launchData, LaunchOptions options) {
        StringArray jvmArguments = launchData.getReplacedJvmArguments(options)
                .append("-Xmn%sm", options.getMinMemory())
                .append("-Xmx%sm", options.getMaxMemory());

        if (options.getCustomJvmArguments() != null) {
            jvmArguments.append(options.getCustomJvmArguments().split(" "));
        }
        options.replaceArguments(jvmArguments);

        AccountProfile selectedProfile = options.getSelectedProfile();
        StringArray gameArguments = launchData.getReplacedGameArguments(options)
                .replace("${auth_player_name}", selectedProfile.getPlayerName())
                .replace("${auth_uuid}", selectedProfile.getPlayerUUID())
                .replace("${auth_access_token}", selectedProfile.getAccessToken())
                .replace("${user_type}", selectedProfile.getType().name())
                .replace("${version_type}", "\"仓鼠发射器\"")
                .replace("${version_name}", "\"仓鼠发射器\"");
        options.replaceArguments(gameArguments);

        StringArray startScript = new StringArray();
        switch (SystemType.getSystemType()) {
            case WINDOWS: {
                startScript
                        .append("\"%s/bin/java.exe\"", options.getJavaPath())
                        .append(jvmArguments)
                        .append(launchData.getMainClass())
                        .append(gameArguments);
            }
            case LINUX: {
                startScript
                        .append("nohup %s/bin/java", options.getJavaPath())
                        .append(jvmArguments)
                        .append(launchData.getMainClass())
                        .append(gameArguments)
                        .append("&");
            }
            default: {
                startScript
                        .append("%s/bin/javaw", options.getJavaPath())
                        .append(jvmArguments)
                        .append(launchData.getMainClass())
                        .append(gameArguments);
            }
        }
        return startScript;
    }

    /**
     * 从版本配置文件中解析获取游戏（和jvm）启动参数
     * <p>
     * 你应该传入的是 版本 json 配置文件内的 arguments 对象的子对象
     *
     * @param element json元素
     * @param options 启动选项
     * @return 启动参数
     */
    public static StringArray parserVersionJson(JsonElement element, LaunchOptions options) {
        if (element.isJsonArray()) {
            return parserJsonArray(element.getAsJsonArray(), options);
        }
        if (element.isJsonObject()) {
            return parserJsonObject(element.getAsJsonObject(), options);
        }
        return new StringArray().append(element.getAsString());
    }

    private static StringArray parserJsonArray(JsonArray array, LaunchOptions options) {
        StringArray stringArray = new StringArray();
        for (JsonElement element : array) {
            StringArray subArray = parserVersionJson(element, options);
            if (subArray != null) {
                stringArray.append(subArray);
            }
        }
        return stringArray;
    }

    private static StringArray parserJsonObject(JsonObject object, LaunchOptions options) {
        for (JsonElement element : object.getAsJsonArray("rules")) {
            Rule rule = new Rule(element.getAsJsonObject());
            if (!rule.match(options)) {
                return null;
            }
        }
        return parserVersionJson(object.get("value"), options);
    }

    /**
     * 从皮肤数据中截取头像数据
     *
     * @param inputStream 皮肤图像数据
     * @param targetSize  目标图像大小
     * @return 头像数据
     */
    @SuppressWarnings("SpellCheckingInspection")
    public static byte[] getAvatorFromSkin(InputStream inputStream, int targetSize) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        int width = image.getWidth();
        if (width != 32 && width != 64) {
            return null;
        }

        BufferedImage avatorImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        avatorImage.getGraphics().drawImage(
                image.getSubimage(8, 8, 8, 8)
                        .getScaledInstance(targetSize, targetSize, Image.SCALE_FAST),
                0,
                0,
                null
        );
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(avatorImage, "PNG", stream);
        byte[] bytes = stream.toByteArray();
        stream.close();
        return bytes;
    }


}
