package io.github.ititus.steam_api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

public interface Parameters {

    @SafeVarargs
    static <T> JsonArray toPrimitiveArray(T... arr) {
        JsonArray jsonArray = new JsonArray();
        for (T t : arr) {
            if (t == null) {
                jsonArray.add(JsonNull.INSTANCE);
            } else if (t instanceof Boolean) {
                jsonArray.add((Boolean) t);
            } else if (t instanceof Character) {
                jsonArray.add((Character) t);
            } else if (t instanceof Number) {
                jsonArray.add((Number) t);
            } else if (t instanceof String) {
                jsonArray.add((String) t);
            } else if (t instanceof JsonElement) {
                jsonArray.add((JsonElement) t);
            } else {
                throw new UnsupportedOperationException("cannot add " + t + " to json array");
            }
        }

        return jsonArray;
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
