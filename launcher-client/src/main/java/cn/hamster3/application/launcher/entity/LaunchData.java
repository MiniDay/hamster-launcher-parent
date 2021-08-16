package cn.hamster3.application.launcher.entity;

import cn.hamster3.application.launcher.entity.library.LibraryPath;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cn.hamster3.application.launcher.entity.asset.AssetIndex;
import cn.hamster3.application.launcher.entity.library.LibraryList;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;
import cn.hamster3.application.launcher.object.StringArray;
import cn.hamster3.application.launcher.util.LauncherUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 启动参数
 */
@SuppressWarnings("unused")
public class LaunchData {
    /**
     * 游戏主文件夹，一般是指 .minecraft 文件夹的路径
     */
    private final File minecraftFolder;
    /**
     * 要启动的版本名称，通过这个来寻找
     */
    private final String versionName;
    /**
     * 游戏配置，具体配置项请打开
     * <p>
     * .minecraft/versions/%versionName%/%versionName%.json
     * <p>
     * 查看
     */
    private final LaunchOptions options;

    /**
     * 游戏运行路径，通常是和 versionFolder 相同（各版本独立）
     * <p>
     * 也可以设置成和 minecraftFolder 相同（跨版本通用）
     */
    private final File gameFolder;
    /**
     * 游戏版本文件夹，通过 minecraftFolder 和 versionName 确定
     * <p>
     * .minecraft/versions/%versionName%
     */
    private final File versionFolder;
    /**
     * 游戏依赖文件夹
     * <p>
     * minecraftFolder/libraries
     */
    private final File librariesFolder;
    /**
     * natives 运行库文件夹
     * <p>
     * gameFolder/natives
     */
    private final File nativesFolder;
    /**
     * 游戏本体核心文件，通常是
     * <p>
     * versionFolder/%versionName%.jar
     */
    private final File minecraftJarFile;

    /**
     * 游戏主类文件所在位置
     * <p>
     * 在版本的 json 配置文件中定义
     */
    private final String mainClass;
    /**
     * 游戏版本类型（快照或稳定版）
     * <p>
     * 在版本的 json 配置文件中定义
     */
    private final String versionType;

    /**
     * 游戏版本类型（快照或稳定版）
     * <p>
     * 在版本的 json 配置文件中定义
     */
    private final AssetIndex assetIndex;
    /**
     * 依赖库列表
     * <p>
     * 通过一定格式读取版本的 json 配置文件而生成
     */
    private final LibraryList libraries;

    /**
     * Java 虚拟机参数
     * <p>
     * 通过一定格式读取版本的 json 配置文件而生成
     */
    private final StringArray jvmArguments;
    /**
     * 游戏参数
     * <p>
     * 通过一定格式读取版本的 json 配置文件而生成
     */
    private final StringArray gameArguments;
    /**
     * Java虚拟机要加载的所有类路径
     * <p>
     * 通过一定格式读取版本的 json 配置文件而生成
     */
    private final ArrayList<LibraryPath> libraryPathList;

    /**
     * 实例化启动脚本
     *
     * @param minecraftFolder minecraft 游戏路径。一般是指 .minecraft 文件夹
     * @param versionName     版本名称
     * @throws IOException 读取版本配置和依赖路径时可能会抛出异常
     */
    public LaunchData(File minecraftFolder, String versionName) throws IOException {
        this(minecraftFolder, versionName, LaunchOptions.getInstance());
    }

    /**
     * 实例化启动脚本
     *
     * @param minecraftFolder minecraft 游戏路径。一般是指 .minecraft 文件夹
     * @param versionName     版本名称
     * @param options         启动选项
     * @throws IOException 读取版本配置和依赖路径时可能会抛出异常
     */
    public LaunchData(File minecraftFolder, String versionName, LaunchOptions options) throws IOException {
        this.minecraftFolder = minecraftFolder;
        this.versionName = versionName;
        this.options = options;

        gameFolder = new File(minecraftFolder, "versions/" + versionName);
        versionFolder = new File(minecraftFolder, "versions/" + versionName);
        librariesFolder = new File(minecraftFolder, "libraries");
        nativesFolder = new File(gameFolder, "natives");
        minecraftJarFile = new File(versionFolder, versionName + ".jar");

        File launcherConfigFile = new File(versionFolder, versionName + ".json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(launcherConfigFile), StandardCharsets.UTF_8));
        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
        reader.close();

        mainClass = object.get("mainClass").getAsString();
        versionType = object.get("type").getAsString();

        assetIndex = new AssetIndex(object.getAsJsonObject("assetIndex"));
        libraries = new LibraryList(object.getAsJsonArray("libraries"));

        JsonObject argumentsObject = object.getAsJsonObject("arguments");
        gameArguments = LauncherUtils.parserVersionJson(argumentsObject.get("game"), options);
        jvmArguments = LauncherUtils.parserVersionJson(argumentsObject.get("jvm"), options);
        libraryPathList = libraries.getClassPath(options);
    }

    /**
     * 读取 classPathList 并扫描 natives 依赖
     * <p>
     * 然后找到这些依赖的 native 函数库，并将其解压至依赖库文件位置
     *
     * @throws IOException IO错误
     */
    public void generatorNativeLibrary() throws IOException {
        if (nativesFolder.mkdirs()) {
            System.out.println("创建 natives 文件夹...");
        }
        for (LibraryPath path : libraryPathList) {
            // 不是 native 依赖则跳过
            if (!path.isNative()) {
                continue;
            }
            // 找到这个 jar 文件，并解压其中的 native 函数库
            File nativeLibraryFile = new File(librariesFolder, path.getPath());

            JarFile jarFile = new JarFile(nativeLibraryFile);
            Enumeration<? extends ZipEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                // 跳过非函数库的文件
                if (!entryName.endsWith(".dll") && !entryName.endsWith(".so") && !entryName.endsWith(".dylib")) {
                    continue;
                }
                File copyFile = new File(nativesFolder, entryName);
                if (copyFile.exists()) {
                    System.out.println(copyFile.getAbsolutePath() + " 已存在，跳过该文件。");
                    continue;
                }
                // 复制到对应位置
                Files.copy(
                        jarFile.getInputStream(entry),
                        copyFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
                System.out.println("将 " + nativeLibraryFile.getAbsolutePath() + " 复制到 " + copyFile.getAbsolutePath() + " !");
            }
        }
    }

    /**
     * 获取启动游戏所需要的所有类路径，包括：
     * <p>
     * 1. 依赖库的类路径
     * <p>
     * 2. 游戏本体的类路径
     *
     * @return 启动游戏所需要的所有类路径
     */
    public String getClassPathString() {
        String pathPrefix = librariesFolder.getAbsolutePath() + "\\";
        ArrayList<String> classPath = new ArrayList<>();
        for (LibraryPath path : libraryPathList) {
            if (path.isNative()) {
                continue;
            }
            classPath.add(pathPrefix + path.getPath());
        }
        classPath.add(minecraftJarFile.getAbsolutePath());
        return String.join(";", classPath);
    }

    public StringArray getReplacedGameArguments() {
        return gameArguments
                .replace("${game_directory}", gameFolder.getAbsolutePath())
                .replace("${assets_index_name}", assetIndex.getId())
                .replace("${assets_root}", new File(minecraftFolder, "assets").getAbsolutePath())
                ;
    }

    public StringArray getReplacedJvmArguments() {
        return jvmArguments
                .replace("${natives_directory}", new File(versionFolder, "natives").getAbsolutePath())
                .replace("${launcher_name}", "仓鼠发射器")
                .replace("${launcher_version}", "叁只仓鼠")
                .replace("${classpath}", getClassPathString());
    }

    public File getMinecraftFolder() {
        return minecraftFolder;
    }

    public String getVersionName() {
        return versionName;
    }

    public LaunchOptions getOptions() {
        return options;
    }

    public File getVersionFolder() {
        return versionFolder;
    }

    public File getMinecraftJarFile() {
        return minecraftJarFile;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getVersionType() {
        return versionType;
    }

    public AssetIndex getAssetIndex() {
        return assetIndex;
    }

    public LibraryList getLibraries() {
        return libraries;
    }

    public StringArray getJvmArguments() {
        return jvmArguments;
    }

    public StringArray getGameArguments() {
        return gameArguments;
    }

    public ArrayList<LibraryPath> getClassPathList() {
        return libraryPathList;
    }

    @Override
    public String toString() {
        return "LauncherData{" +
                "minecraftFolder=" + minecraftFolder +
                ", versionName='" + versionName + '\'' +
                ", options=" + options +
                ", gameFolder=" + gameFolder +
                ", versionFolder=" + versionFolder +
                ", librariesFolder=" + librariesFolder +
                ", nativesFolder=" + nativesFolder +
                ", minecraftJarFile=" + minecraftJarFile +
                ", mainClass='" + mainClass + '\'' +
                ", versionType='" + versionType + '\'' +
                ", assetIndex=" + assetIndex +
                ", libraries=" + libraries +
                ", jvmArguments='" + jvmArguments + '\'' +
                ", gameArguments='" + gameArguments + '\'' +
                ", libraryPathList=" + libraryPathList +
                '}';
    }
}
