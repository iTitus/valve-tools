package io.github.ititus.valve_tools.steam_web_api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public interface Parameters {

    static void writeUnsignedArray(Map<String, String> params, String countName, String arrayName, int... arr) {
        if (countName != null) {
            params.put(countName, Integer.toUnsignedString(arr.length));
        }
        for (int i = 0; i < arr.length; i++) {
            params.put(arrayName + "[" + i + "]", Integer.toUnsignedString(arr[i]));
        }
    }

    static void writeUnsignedArray(Map<String, String> params, String countName, String arrayName, long... arr) {
        if (countName != null) {
            params.put(countName, Integer.toUnsignedString(arr.length));
        }
        for (int i = 0; i < arr.length; i++) {
            params.put(arrayName + "[" + i + "]", Long.toUnsignedString(arr[i]));
        }
    }

    static void writeArray(Map<String, String> params, String countName, String arrayName, String... arr) {
        if (countName != null) {
            params.put(countName, Integer.toUnsignedString(arr.length));
        }
        for (int i = 0; i < arr.length; i++) {
            params.put(arrayName + "[" + i + "]", Objects.requireNonNull(arr[i]));
        }
    }

    static <T> void writeOptional(Map<String, String> params, String key, T value) {
        writeOptional(params, key, value, Object::toString);
    }

    static <T> void writeOptional(Map<String, String> params, String key, T value, Function<? super T, ? extends String> toString) {
        if (key != null && value != null) {
            params.put(key, toString.apply(value));
        }
    }

    default JsonElement toJson() {
        var json = new JsonObject();
        populateJson(json);
        return json;
    }

    default void populateJson(JsonObject json) {
        throw new UnsupportedOperationException();
    }

    default Map<String, String> toMap() {
        Map<String, String> params = new LinkedHashMap<>();
        populateParams(params);
        return params;
    }

    default void populateParams(Map<String, String> params) {
        throw new UnsupportedOperationException();
    }
}
