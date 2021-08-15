package cn.hamster3.application.launcher.entity.library;

public record LibraryPath(String path, boolean isNative) {
    @Override
    public String toString() {
        return "LibraryPath{" +
                "path='" + path + '\'' +
                ", isNative=" + isNative +
                '}';
    }
}
