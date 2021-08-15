package cn.hamster3.application.launcher.entity.library;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Downloads {
    private final Artifact artifact;
    private HashMap<String, Artifact> classifiers;

    public Downloads(JsonObject object) {
        artifact = new Artifact(object.getAsJsonObject("artifact"));

        if (object.has("classifiers")) {
            classifiers = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : object.getAsJsonObject("classifiers").entrySet()) {
                classifiers.put(entry.getKey(), new Artifact(entry.getValue().getAsJsonObject()));
            }
        }
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public HashMap<String, Artifact> getClassifiers() {
        return classifiers;
    }

    @Override
    public String toString() {
        return "Downloads{" +
                "artifact=" + artifact +
                ", classifiers=" + classifiers +
                '}';
    }
}
