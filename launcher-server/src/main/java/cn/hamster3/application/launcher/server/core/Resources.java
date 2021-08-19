package cn.hamster3.application.launcher.server.core;

import cn.hamster3.application.launcher.common.LauncherVersionInfo;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Resources {
    public static final Logger logger = LoggerFactory.getLogger("Resources");
    private static final Resources instance = new Resources();

    private final ArrayList<LauncherVersionInfo> launcherVersionInfoList;
    private JsonArray launcherVersionInfoArray;

    private Resources() {
        launcherVersionInfoList = new ArrayList<>();
    }

    public static Resources getInstance() {
        return instance;
    }


    /**
     * 重载所有启动器版本信息
     */
    public void reloadLauncherInfoList() {
        synchronized (launcherVersionInfoList) {
            launcherVersionInfoList.clear();
            File launchersFolder = new File("launchers");
            if (launchersFolder.mkdir()) {
                logger.info("创建启动器存档文件夹...");
            }
            File[] files = launchersFolder.listFiles();
            if (files == null) {
                logger.warn(launchersFolder.getAbsoluteFile() + " 不是一个文件夹!");
                return;
            }
            for (File subFile : files) {
                LauncherVersionInfo info = generateLauncherVersionInfo(subFile);
                launcherVersionInfoList.add(info);
            }
            launcherVersionInfoList.sort((o1, o2) -> -o1.getVersion().compareTo(o2.getVersion()));
        }
        launcherVersionInfoArray = new JsonArray();
        for (LauncherVersionInfo info : launcherVersionInfoList) {
            launcherVersionInfoArray.add(getLauncherInfoAsJson(info));
        }
        logger.info("重载所有启动器版本信息完成.");
    }

    /**
     * 获取启动器版本信息
     *
     * @param version 版本
     * @return -
     */
    public JsonObject getLauncherInfo(String version) {
        LauncherVersionInfo versionInfo = launcherVersionInfoList.stream()
                .filter(info -> info.getVersion().equalsIgnoreCase(version))
                .findFirst()
                .orElse(null);
        if (versionInfo == null) {
            return null;
        }
        return getLauncherInfoAsJson(versionInfo);
    }

    public JsonObject getLatestLauncherInfo() {
        if (launcherVersionInfoList.size() < 1) {
            return null;
        }
        LauncherVersionInfo info = launcherVersionInfoList.get(0);
        return getLauncherInfoAsJson(info);
    }

    /**
     * 获取启动器版本信息列表
     *
     * @return -
     */
    public JsonArray getLauncherInfoArray() {
        return launcherVersionInfoArray;
    }

    /**
     * 获取启动器文件
     *
     * @param version 版本
     * @return -
     */
    public File getLauncherFile(String version) {
        File launchersFolder = new File("launchers");
        File versionFolder = new File(launchersFolder, version);
        return new File(versionFolder, "HamsterLauncher-" + version + ".jar");
    }


    private LauncherVersionInfo generateLauncherVersionInfo(File folder) {
        File infoFile = new File(folder, "info.md");
        String url = "/download/launcher/" + folder.getName();
        return new LauncherVersionInfo(folder.getName(), readFileAsString(infoFile), url);
    }

    private String readFileAsString(File file) {
        if (!file.exists()) {
            logger.warn("取消读取文件 " + file.getAbsolutePath() + " 文件不存在!");
            return "";
        }
        try {
            StringBuilder builder = new StringBuilder();
            FileInputStream stream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int read = stream.read(bytes);
            while (read > 0) {
                builder.append(new String(bytes, 0, read, StandardCharsets.UTF_8));
                read = stream.read(bytes);
            }
            return builder.toString();
        } catch (IOException e) {
            logger.error("读取文件 " + file.getAbsolutePath() + " 时出现了一个异常: ", e);
        }
        return "";
    }

    private JsonObject getLauncherInfoAsJson(LauncherVersionInfo info) {
        return new JsonObject()
                .put("version", info.getVersion())
                .put("info", info.getInfo())
                .put("url", info.getUrl());
    }

}
