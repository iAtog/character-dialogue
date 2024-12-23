package me.iatog.characterdialogue.dialogs;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodConfiguration {

    private final Pattern pattern;
    private final Map<String, Object> objects;
    private final String argument;

    public MethodConfiguration(@NotNull String argument, @NotNull String config) {
        this.objects = new HashMap<>();
        this.pattern = Pattern.compile("(\\w+)=[\"']([^\"']*)[\"']\\s*,?\\s*|(\\w+)=([^,\\s}]+)");
        this.argument = argument;

        this.init(config);
    }

    public String getArgument() {
        return argument;
    }

    public void set(@NotNull String key, @NotNull Object value) {
        this.objects.put(key, value);
    }

    public Object get(String key) {
        return objects.get(key);
    }

    public Object get(String key, Object def) {
        return !contains(key) ? def : get(key);
    }

    public String getString(String key) {
        Object value = get(key);

        if (value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }

    public String getString(String key, String def) {
        return !contains(key) ? def : getString(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) get(key);
    }

    public boolean getBoolean(String key, boolean def) {
        return !contains(key) ? def : getBoolean(key);
    }

    public int getInteger(String key) {
        Object value = get(key);
        return switch (value) {
            case null -> 0;
            case Float v -> v.intValue();
            case String s -> Integer.parseInt(s);
            default -> (int) value;
        };
    }

    public int getInteger(String key, int def) {
        return !contains(key) ? def : getInteger(key);
    }

    public float getFloat(String key) {
        Object value = get(key);
        return switch (value) {
            case null -> 0f;
            case Integer i -> i.floatValue();
            case String s -> Float.parseFloat(s);
            default -> (float) value;
        };
    }

    public float getFloat(String key, float def) {
        return !contains(key) ? def : getFloat(key);
    }

    public boolean contains(String key) {
        return objects.containsKey(key);
    }

    private void init(String input) {
        if (input.isEmpty()) {
            return;
        }

        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                objects.put(matcher.group(1), matcher.group(2));
            } else {
                objects.put(matcher.group(3), this.convertValue(matcher.group(4)));
            }
        }
    }

    public Map<String, Object> map() {
        return Collections.unmodifiableMap(objects);
    }

    private Object convertValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value.toLowerCase());
        } else if (value.matches("-?\\d+(\\.\\d+)?")) {
            try {
                if (value.contains(".")) {
                    return Float.parseFloat(value);
                } else {
                    return Integer.parseInt(value);
                }
            } catch (NumberFormatException exception) {
                return value;
            }
        } else {
            return value;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        objects.forEach((key, value) -> {
            if (value instanceof String) {
                stringBuilder.append(key).append("='").append(value).append("'");
            } else {
                stringBuilder.append(key).append("=").append(value);
            }
            stringBuilder.append(",");
        });

        if (stringBuilder.toString().endsWith(",")) {
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        }

        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
