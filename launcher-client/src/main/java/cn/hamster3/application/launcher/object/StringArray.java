package cn.hamster3.application.launcher.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringArray {
    private final List<String> strings;

    public StringArray() {
        strings = new ArrayList<>();
    }

    public StringArray(String[] strings) {
        this.strings = Arrays.asList(strings);

    }

    public StringArray add(int index, String string) {
        strings.add(index, string);
        return this;
    }

    public StringArray append(String string) {
        strings.add(string);
        return this;
    }

    public StringArray append(String format, Object... args) {
        strings.add(String.format(format, args));
        return this;
    }

    public StringArray append(String[] strings) {
        this.strings.addAll(Arrays.asList(strings));
        return this;
    }

    public StringArray append(StringArray array) {
        strings.addAll(array.strings);
        return this;
    }

    public StringArray replace(String key, String value) {
        for (int i = 0; i < strings.size(); i++) {
            strings.set(i, strings.get(i).replace(key, value));
        }
        return this;
    }

    public List<String> getStrings() {
        return strings;
    }

    public String toString() {
        return String.join(" ", strings);
    }
}
