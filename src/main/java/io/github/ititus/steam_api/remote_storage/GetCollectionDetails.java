package io.github.ititus.steam_api.remote_storage;

import com.google.gson.JsonObject;
import io.github.ititus.steam_api.JsonMethod;
import io.github.ititus.steam_api.SteamWebApi;
import io.github.ititus.steam_api.exception.ParameterException;
import io.github.ititus.steam_api.exception.SteamWebApiException;

import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

public class GetCollectionDetails extends JsonMethod<RemoteStorage, List<CollectionDetails>> {

    private final long[] collectionIds;

    GetCollectionDetails(RemoteStorage apiInterface, long... collectionIds) throws SteamWebApiException {
        super(apiInterface, "GetCollectionDetails", 1, SteamWebApi.HttpMethod.POST);
        if (collectionIds.length == 0) {
            throw new ParameterException("Expected non-empty ids");
        }

        this.collectionIds = collectionIds;
    }

    @Override
    public void populateParams(Map<String, String> params) {
        params.put("collectioncount", valueOf(collectionIds.length));
        for (int i = 0; i < collectionIds.length; i++) {
            params.put("publishedfileids[" + i + "]", valueOf(collectionIds[i]));
        }
    }

    @Override
    protected List<CollectionDetails> parseJson(JsonObject json) throws SteamWebApiException {
        return extractArrayElements(json, "collectiondetails", collectionIds.length, CollectionDetails.class);
    }
}
