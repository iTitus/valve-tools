package io.github.ititus.steam_api;

import com.google.gson.*;
import io.github.ititus.steam_api.exception.ResultException;
import io.github.ititus.steam_api.exception.SteamWebApiException;

import java.util.ArrayList;
import java.util.List;

public abstract class JsonMethod<T extends Interface, R> extends Method<T, R> {

    protected static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .create();

    protected JsonMethod(T apiInterface, String name, int version, SteamWebApi.HttpMethod httpMethod) {
        super(apiInterface, name, version, httpMethod, SteamWebApi.Format.JSON);
    }

    protected static <S> List<S> extractArrayElements(JsonObject json, String arrayKey, int expectedArrayLength,
                                                      Class<S> resultClass) throws SteamWebApiException {
        JsonObject obj = json.getAsJsonObject().get("response").getAsJsonObject();
        Result result = Result.findById(obj.get("result").getAsInt());
        int resultCount = obj.get("resultcount").getAsInt();
        if (result != Result.OK) {
            throw new ResultException(result);
        } else if (resultCount != expectedArrayLength) {
            throw new ResultException("Unexpected number of results");
        }

        JsonArray arr = obj.getAsJsonArray(arrayKey);
        if (arr.size() != expectedArrayLength) {
            throw new ResultException("Unexpected array length");
        }

        List<S> results = new ArrayList<>(expectedArrayLength);
        for (int i = 0; i < expectedArrayLength; i++) {
            S resultObj = GSON.fromJson(arr.get(i), resultClass);
            if (resultObj instanceof AbstractResult) {
                Result resultEnum = ((AbstractResult) resultObj).getResult();
                if (resultEnum != Result.OK) {
                    throw new ResultException(resultEnum);
                }
            }

            results.add(resultObj);
        }

        return results;
    }

    @Override
    protected R parse(String data) throws SteamWebApiException {
        return parseJson(JsonParser.parseString(data).getAsJsonObject());
    }

    protected abstract R parseJson(JsonObject json) throws SteamWebApiException;

}
