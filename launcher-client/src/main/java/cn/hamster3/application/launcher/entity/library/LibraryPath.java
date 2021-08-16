package cn.hamster3.application.launcher.entity.library;

public class LibraryPath {
    private final String path;
    private final boolean isNative;

    public LibraryPath(String path, boolean isNative) {
        this.path = path;
        this.isNative = isNative;
    }

    public String getPath() {
        return path;
    }

    public boolean isNative() {
        return isNative;
    }

    @Override
    public String toString() {
        return "LibraryPath{" +
                "path='" + path + '\'' +
                ", isNative=" + isNative +
                '}';
    }
}
