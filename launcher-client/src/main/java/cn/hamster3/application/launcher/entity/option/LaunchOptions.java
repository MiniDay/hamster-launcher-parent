package cn.hamster3.application.launcher.entity.option;

import cn.hamster3.application.launcher.object.StringArray;
import cn.hamster3.application.launcher.entity.auth.AccountProfile;
import cn.hamster3.application.launcher.util.LauncherUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 启动选项
 */
public class LaunchOptions {
    private static final LaunchOptions INSTANCE = loadLaunchOptions();
    /**
     * 账户列表
     */
    private final ArrayList<AccountProfile> profiles;
    /**
     * 启动器配置
     */
    private final HashMap<String, String> configs;
    /**
     * 游戏选项
     */
    private final HashMap<String, String> options;
    /**
     * 游戏参数
     */
    private final HashMap<String, String> arguments;
    /**
     * 已选择的账号
     */
    private int selectedProfile;

    public LaunchOptions() {
        profiles = new ArrayList<>();
        configs = new HashMap<>();
        options = new HashMap<>();
        arguments = new HashMap<>();

        setHasCustomResolution(true);
        setResolutionWidth("1280");
        setResolutionHeight("720");
    }

    public static LaunchOptions getInstance() {
        return INSTANCE;
    }

    public static LaunchOptions loadLaunchOptions() {
        File configFile = new File(LauncherUtils.getLauncherDirectory(), "config.json");
        if (configFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
                StringBuilder builder = new StringBuilder();
                while (reader.ready()) {
                    builder.append(reader.readLine()).append('\n');
                }
                reader.close();
                return LauncherUtils.gson.fromJson(builder.toString(), LaunchOptions.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LaunchOptions options = new LaunchOptions();
        options.save();
        return options;
    }

    public void save() {
        save(new File(LauncherUtils.getLauncherDirectory(), "config.json"));
    }

    public void save(File file) {
        String s = LauncherUtils.gson.toJson(this);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            writer.write(s);
            writer.close();
            System.out.println("配置文件已保存至: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void replaceArguments(StringArray script) {
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            script.replace("${" + entry.getKey() + "}", entry.getValue());
        }
    }

    public AccountProfile getSelectedProfile() {
        if (selectedProfile < 0 || selectedProfile >= profiles.size()) {
            return null;
        }
        return profiles.get(selectedProfile);
    }

    public void setSelectedProfile(AccountProfile profile) {
        this.selectedProfile = profiles.indexOf(profile);
    }

    public void addProfile(AccountProfile profile) {
        if (profiles.contains(profile)) {
            return;
        }
        profiles.add(profile);
        selectedProfile = profiles.indexOf(profile);
    }

    public void removeProfile(AccountProfile profile) {
        if (profile.equals(getSelectedProfile())) {
            if (profiles.size() > 0) {
                selectedProfile = 0;
            } else {
                selectedProfile = -1;
            }
        }
        profiles.remove(profile);
    }

    public ArrayList<AccountProfile> getProfiles() {
        return profiles;
    }

    @Nullable
    public String getOption(String key) {
        return options.get(key);
    }

    public boolean matchFeatures(HashMap<String, String> features) {
        for (Map.Entry<String, String> entry : features.entrySet()) {
            if (!options.containsKey(entry.getKey())) {
                return false;
            }
            if (!options.get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    public String getResolutionWidth() {
        return arguments.get("resolution_width");
    }

    public void setResolutionWidth(String resolutionWidth) {
        arguments.put("resolution_width", resolutionWidth);
    }

    public String getResolutionHeight() {
        return arguments.get("resolution_height");
    }

    public void setResolutionHeight(String resolutionHeight) {
        arguments.put("resolution_height", resolutionHeight);
    }

    public boolean getHasCustomResolution() {
        return Boolean.parseBoolean(options.getOrDefault("has_custom_resolution", "true"));
    }

    public void setHasCustomResolution(boolean hasCustomResolution) {
        options.put("has_custom_resolution", String.valueOf(hasCustomResolution));
    }

    public String getJavaPath() {
        return configs.getOrDefault("java_path", LauncherUtils.getJavaHomePath());
    }

    public void setJavaPath(String path) {
        configs.put("java_path", path);
    }

    public String getMinMemory() {
        return configs.getOrDefault("min_memory", "128");
    }

    public void setMinMemory(String minMemory) {
        configs.put("min_memory", minMemory);
    }

    public String getMaxMemory() {
        return configs.getOrDefault("max_memory", "2048");
    }

    public void setMaxMemory(String minMemory) {
        configs.put("max_memory", minMemory);
    }

    public String getCustomJvmArguments() {
        return configs.get("custom_jvm_arguments");
    }

    public void setCustomJvmArguments(String customJvmArguments) {
        configs.put("custom_jvm_arguments", customJvmArguments);
    }

    @Override
    public String toString() {
        return "LaunchOptions{" +
                "options=" + options +
                ", arguments=" + arguments +
                '}';
    }
}
