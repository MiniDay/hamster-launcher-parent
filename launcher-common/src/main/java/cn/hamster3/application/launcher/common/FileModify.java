package cn.hamster3.application.launcher.common;

public class FileModify {
    /**
     * 操作类型
     */
    private Type type;
    /**
     * 文件名
     */
    private String name;
    /**
     * 相对于 .minecraft 文件夹的路径
     */
    private String path;

    public FileModify(String path, Type type) {
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    enum Type {
        /**
         * 新增文件
         * <p>
         * 如果已有则替换
         */
        REPLACE,
        /**
         * 删除文件
         */
        DELETE
    }
}
