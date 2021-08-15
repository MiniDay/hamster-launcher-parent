package cn.hamster3.application.launcher.entity.rule;

import com.google.gson.JsonObject;

public class RuleOS {
    private String name;
    private String version;
    private String arch;

    public RuleOS(JsonObject object) {
        if (object.has("name")) {
            name = object.get("name").getAsString();
        }
        if (object.has("version")) {
            version = object.get("name").getAsString();
        }
        if (object.has("arch")) {
            arch = object.get("arch").getAsString();
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getArch() {
        return arch;
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean match() {
        if (name != null) {
            if (!System.getProperty("os.name").toLowerCase().contains(name)) {
                return false;
            }
        }
        if (version != null) {
            if (!System.getProperty("os.version").matches(version)) {
                return false;
            }
        }
        if (arch != null) {
            if (!System.getProperty("os.arch").equals("arch")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "RuleOS{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", arch='" + arch + '\'' +
                '}';
    }
}
