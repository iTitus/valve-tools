package io.github.ititus.valve_tools.steam_web_api.remote_storage;

import com.google.gson.JsonObject;
import io.github.ititus.valve_tools.steam_web_api.ApiMethod;
import io.github.ititus.valve_tools.steam_web_api.JsonMethod;
import io.github.ititus.valve_tools.steam_web_api.exception.ParameterException;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;

import java.util.List;
import java.util.Map;

import static io.github.ititus.valve_tools.steam_web_api.Parameters.writeUnsignedArray;

public final class GetPublishedFileDetails extends JsonMethod<RemoteStorage, List<PublishedFileDetails>> {

    private final long[] publishedFileIds;

    GetPublishedFileDetails(RemoteStorage apiInterface, long... publishedFileIds) throws SteamWebApiException {
        super(apiInterface, "GetPublishedFileDetails", 1, ApiMethod.POST);
        if (publishedFileIds.length == 0) {
            throw new ParameterException("Expected non-empty ids");
        }

        this.publishedFileIds = publishedFileIds;
    }

    @Override
    public void populateParams(Map<String, String> params) {
        writeUnsignedArray(params, "itemcount", "publishedfileids", publishedFileIds);
    }

    @Override
    protected List<PublishedFileDetails> parseJson(JsonObject json) throws SteamWebApiException {
        return extractArrayElements(json, "publishedfiledetails", publishedFileIds.length, PublishedFileDetails.class);
    }
}
