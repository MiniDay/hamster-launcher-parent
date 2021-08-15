package cn.hamster3.application.launcher.entity.library;

import com.google.gson.JsonObject;

public class Artifact {
    private final String path;
    private final String url;
    private final String sha1;
    private final long size;

    public Artifact(JsonObject object) {
        path = object.get("path").getAsString();
        url = object.get("url").getAsString();
        sha1 = object.get("sha1").getAsString();
        size = object.get("size").getAsLong();
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public String getSha1() {
        return sha1;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "path='" + path + '\'' +
                ", url='" + url + '\'' +
                ", sha1='" + sha1 + '\'' +
                ", size=" + size +
                '}';
    }
}
