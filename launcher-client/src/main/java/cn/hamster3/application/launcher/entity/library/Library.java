package cn.hamster3.application.launcher.entity.library;

import cn.hamster3.application.launcher.constant.SystemType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;
import cn.hamster3.application.launcher.entity.rule.Rule;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Library {
    private final String name;
    private final String group;
    private final String artifact;
    private final String version;

    private Downloads downloads;
    private HashMap<String, String> natives;
    private ArrayList<Rule> rules;

    public Library(JsonObject object) {
        name = object.get("name").getAsString();
        String[] split = name.split(":");
        group = split[0];
        artifact = split[1];
        version = split[2];

        if (object.has("downloads")) {
            downloads = new Downloads(object.getAsJsonObject("downloads"));
        }
        if (object.has("natives")) {
            natives = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : object.getAsJsonObject("natives").entrySet()) {
                natives.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        if (object.has("rules")) {
            rules = new ArrayList<>();
            for (JsonElement element : object.getAsJsonArray("rules")) {
                rules.add(new Rule(element.getAsJsonObject()));
            }
        }
    }

    public void merge(Library otherLibrary) {
        if (downloads == null) {
            downloads = otherLibrary.downloads;
        } else if (downloads.getClassifiers() == null) {
            downloads = otherLibrary.downloads;
        }
        if (natives == null) {
            natives = otherLibrary.natives;
        }
        if (rules == null) {
            rules = otherLibrary.rules;
        }
    }

    public LibraryPath getClassPath() {
        // 文件夹路径
        String path = group.replace(".", "\\") + "\\" + artifact + "\\" + version;
        String fileName = artifact + "-" + version + ".jar";
        return new LibraryPath(path + "\\" + fileName, false);
    }

    @Nullable
    public LibraryPath getNativeClassPath(LaunchOptions options) {
        if (rules != null) {
            for (Rule rule : rules) {
                if (!rule.match(options)) {
                    return null;
                }
            }
        }
        if (natives == null) {
            return null;
        }
        String nativeArtifactID = natives.get(SystemType.getSystemType().getSimpleName());
        if (nativeArtifactID == null) {
            return null;
        }
        if (downloads.getClassifiers() == null) {
            return null;
        }
        Artifact nativeArtifact = downloads.getClassifiers().get(nativeArtifactID);
        if (nativeArtifact == null) {
            return null;
        }
        return new LibraryPath(nativeArtifact.getPath().replace("/", "\\"), true);
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getArtifact() {
        return artifact;
    }

    public String getVersion() {
        return version;
    }

    public Downloads getDownloads() {
        return downloads;
    }

    public HashMap<String, String> getNatives() {
        return natives;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Library )) return false;
        Library library = (Library) o;
        return group.equals(library.group) && artifact.equals(library.artifact) && version.equals(library.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, artifact, version);
    }

    @Override
    public String toString() {
        return "Library{" +
                "group='" + group + '\'' +
                ", artifact='" + artifact + '\'' +
                ", version='" + version + '\'' +
                ", downloads=" + downloads +
                ", natives=" + natives +
                ", rules=" + rules +
                '}';
    }
}
