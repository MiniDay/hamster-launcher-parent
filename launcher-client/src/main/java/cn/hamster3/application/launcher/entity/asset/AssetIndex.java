package cn.hamster3.application.launcher.entity.asset;

import com.google.gson.JsonObject;

public class AssetIndex {
    private final String id;
    private final String sha1;
    private final long size;
    private final long totalSize;
    private final String url;

    public AssetIndex(JsonObject object) {
        id = object.get("id").getAsString();
        sha1 = object.get("sha1").getAsString();
        size = object.get("size").getAsLong();
        totalSize = object.get("totalSize").getAsLong();
        url = object.get("url").getAsString();
    }

    public String getId() {
        return id;
    }

    public String getSha1() {
        return sha1;
    }

    public long getSize() {
        return size;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "AssetIndex{" +
                "id='" + id + '\'' +
                ", sha1='" + sha1 + '\'' +
                ", size=" + size +
                ", totalSize=" + totalSize +
                ", url='" + url + '\'' +
                '}';
    }
}
