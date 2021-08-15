package cn.hamster3.application.launcher.entity.rule;

import cn.hamster3.application.launcher.entity.option.LaunchOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Rule {
    private final Action action;
    private HashMap<String, String> features;
    private RuleOS os;

    public Rule(JsonObject object) {
        action = Action.valueOf(object.get("action").getAsString().toUpperCase());
        if (object.has("os")) {
            os = new RuleOS(object.getAsJsonObject("os"));
        }
        if (object.has("features")) {
            features = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : object.getAsJsonObject("features").entrySet()) {
                features.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
    }

    @SuppressWarnings({"RedundantIfStatement", "BooleanMethodIsAlwaysInverted"})
    public boolean match(LaunchOptions options) {
        boolean flag = switch (action) {
            case ALLOW -> true;
            case DISALLOW -> false;
        };
        if (os != null) {
            if (os.match() != flag) {
                return false;
            }
        }
        if (features != null) {
            if (options.matchFeatures(features) != flag) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "action=" + action +
                ", features=" + features +
                ", os=" + os +
                '}';
    }
}
