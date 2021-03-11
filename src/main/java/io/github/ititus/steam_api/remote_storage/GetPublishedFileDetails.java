package io.github.ititus.steam_api.remote_storage;

import com.google.gson.JsonObject;
import io.github.ititus.steam_api.JsonMethod;
import io.github.ititus.steam_api.SteamWebApi;
import io.github.ititus.steam_api.exception.ParameterException;
import io.github.ititus.steam_api.exception.SteamWebApiException;

import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

public class GetPublishedFileDetails extends JsonMethod<RemoteStorage, List<PublishedFileDetails>> {

    private final long[] publishedFileIds;

    GetPublishedFileDetails(RemoteStorage apiInterface, long... publishedFileIds) throws SteamWebApiException {
        super(apiInterface, "GetPublishedFileDetails", 1, SteamWebApi.HttpMethod.POST);
        if (publishedFileIds.length == 0) {
            throw new ParameterException("Expected non-empty ids");
        }

        this.publishedFileIds = publishedFileIds;
    }

    @Override
    public void populateParams(Map<String, String> params) {
        params.put("itemcount", valueOf(publishedFileIds.length));
        for (int i = 0; i < publishedFileIds.length; i++) {
            params.put("publishedfileids[" + i + "]", valueOf(publishedFileIds[i]));
        }
    }

    @Override
    protected List<PublishedFileDetails> parseJson(JsonObject json) throws SteamWebApiException {
        return extractArrayElements(json, "publishedfiledetails", publishedFileIds.length, PublishedFileDetails.class);
    }
}
