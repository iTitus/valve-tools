package io.github.ititus.valve_tools.steam_web_api;

import com.google.gson.*;
import io.github.ititus.valve_tools.steam_web_api.common.BaseResult;
import io.github.ititus.valve_tools.steam_web_api.common.Result;
import io.github.ititus.valve_tools.steam_web_api.exception.ResultException;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;

import java.util.ArrayList;
import java.util.List;

public abstract class JsonMethod<T extends Interface, R> extends Method<T, R> {

    public static final Gson GSON = new GsonBuilder().create();

    protected JsonMethod(T apiInterface, String name, int version, ApiMethod apiMethod) {
        super(apiInterface, name, version, apiMethod, ResponseFormat.JSON);
    }

    protected static <S extends BaseResult> List<S> extractArrayElements(JsonElement json, String arrayKey, int expectedArrayLength, Class<S> resultClass) throws SteamWebApiException {
        var response = json.getAsJsonObject().getAsJsonObject("response");

        if (response.has("result")) {
            Result result = GSON.fromJson(response, BaseResult.class).getResult();
            if (result != Result.OK) {
                throw new ResultException(result);
            }
        }

        var resultCount = response.get("resultcount");
        if (resultCount != null && resultCount.isJsonPrimitive()) {
            var resultCountAsInt = resultCount.getAsInt();
            if (resultCountAsInt != expectedArrayLength) {
                throw new ResultException("Unexpected number of results");
            }
        }

        var arr = response.getAsJsonArray(arrayKey);
        if (arr == null) {
            throw new ResultException("Missing array");
        } else if (arr.size() != expectedArrayLength) {
            throw new ResultException("Unexpected array length");
        }

        List<S> results = new ArrayList<>(expectedArrayLength);
        for (int i = 0; i < expectedArrayLength; i++) {
            S resultObj = GSON.fromJson(arr.get(i), resultClass);
            Result resultEnum = resultObj.getResult();
            if (resultEnum != Result.OK) {
                throw new ResultException(resultEnum);
            }

            results.add(resultObj);
        }

        return results;
    }

    @Override
    protected R parse(String data) throws SteamWebApiException {
        if (getFormat() != ResponseFormat.JSON) {
            throw new SteamWebApiException(new UnsupportedOperationException("only JSON responses supported"));
        }

        return parseJson(JsonParser.parseString(data).getAsJsonObject());
    }

    protected abstract R parseJson(JsonObject json) throws SteamWebApiException;

}
