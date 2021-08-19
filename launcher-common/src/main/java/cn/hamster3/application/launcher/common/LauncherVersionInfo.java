package cn.hamster3.application.launcher.common;

public class LauncherVersionInfo {
    public String version;
    public String info;
    public String url;

    public LauncherVersionInfo(String version, String info, String url) {
        this.version = version;
        this.info = info;
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public String getInfo() {
        return info;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "LauncherVersionInfo{" +
                "version='" + version + '\'' +
                ", info='" + info + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
