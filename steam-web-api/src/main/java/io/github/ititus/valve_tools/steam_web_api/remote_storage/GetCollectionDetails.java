package io.github.ititus.valve_tools.steam_web_api.remote_storage;

import com.google.gson.JsonObject;
import io.github.ititus.valve_tools.steam_web_api.ApiMethod;
import io.github.ititus.valve_tools.steam_web_api.JsonMethod;
import io.github.ititus.valve_tools.steam_web_api.exception.ParameterException;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;

import java.util.List;
import java.util.Map;

import static io.github.ititus.valve_tools.steam_web_api.Parameters.writeArray;

public class GetCollectionDetails extends JsonMethod<RemoteStorage, List<CollectionDetails>> {

    private final long[] collectionIds;

    GetCollectionDetails(RemoteStorage apiInterface, long... collectionIds) throws SteamWebApiException {
        super(apiInterface, "GetCollectionDetails", 1, ApiMethod.POST);
        if (collectionIds.length == 0) {
            throw new ParameterException("Expected non-empty ids");
        }

        this.collectionIds = collectionIds;
    }

    @Override
    public void populateParams(Map<String, String> params) {
        writeArray(params, "collectioncount", "publishedfileids", collectionIds);
    }

    @Override
    protected List<CollectionDetails> parseJson(JsonObject json) throws SteamWebApiException {
        return extractArrayElements(json, "collectiondetails", collectionIds.length, CollectionDetails.class);
    }
}
