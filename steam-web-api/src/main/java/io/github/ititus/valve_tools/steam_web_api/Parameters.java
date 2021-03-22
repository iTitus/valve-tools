package io.github.ititus.valve_tools.steam_web_api;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.valueOf;

public interface Parameters {

    static void writeArray(Map<String, String> params, String countName, String arrayName, int... arr) {
        params.put(countName, valueOf(arr.length));
        for (int i = 0; i < arr.length; i++) {
            params.put(arrayName + "[" + i + "]", valueOf(arr[i]));
        }
    }

    static void writeArray(Map<String, String> params, String countName, String arrayName, long... arr) {
        params.put(countName, valueOf(arr.length));
        for (int i = 0; i < arr.length; i++) {
            params.put(arrayName + "[" + i + "]", valueOf(arr[i]));
        }
    }

    static void writeArray(Map<String, String> params, String countName, String arrayName, String... arr) {
        params.put(countName, valueOf(arr.length));
        for (int i = 0; i < arr.length; i++) {
            params.put(arrayName + "[" + i + "]", valueOf(arr[i]));
        }
    }

    default JsonObject toJson() {
        JsonObject json = new JsonObject();
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
