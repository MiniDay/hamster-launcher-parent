package cn.hamster3.application.launcher.constant;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum SystemType {
    WINDOWS("windows"),
    LINUX("linux"),
    OSX("osx"),
    OTHER("other");

    private static final SystemType systemType;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            systemType = WINDOWS;
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            systemType = OSX;
        } else if (osName.contains("nux")) {
            systemType = LINUX;
        } else {
            systemType = OTHER;
        }
    }

    private final String simpleName;

    SystemType(String simpleName) {
        this.simpleName = simpleName;
    }

    public static SystemType getSystemType() {
        return systemType;
    }

    public static Path getWorkingDirectory(String folder) {
        String home = System.getProperty("user.home", ".");
        switch (systemType) {
            case LINUX:
                return Paths.get(home, "." + folder);
            case WINDOWS:
                String appdata = System.getenv("APPDATA");
                return Paths.get(appdata == null ? home : appdata, "." + folder);
            case OSX:
                return Paths.get(home, "Library", "Application Support", folder);
            default:
                return Paths.get(home, folder);
        }
    }

    public String getSimpleName() {
        return simpleName;
    }
}
