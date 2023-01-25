package io.github.ititus.valve_tools.steam_web_api.remote_storage;

import com.google.gson.JsonObject;
import io.github.ititus.valve_tools.steam_web_api.ApiMethod;
import io.github.ititus.valve_tools.steam_web_api.JsonMethod;
import io.github.ititus.valve_tools.steam_web_api.common.Result;
import io.github.ititus.valve_tools.steam_web_api.exception.ResultException;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;

import java.util.Map;

import static io.github.ititus.valve_tools.steam_web_api.Parameters.writeOptional;

public final class GetUGCFileDetails extends JsonMethod<RemoteStorage, UGCFileDetails> {

    private final Long steamId;
    private final long ugcId;
    private final int appId;

    GetUGCFileDetails(RemoteStorage apiInterface, Long steamId, long ugcId, int appId) throws SteamWebApiException {
        super(apiInterface, "GetUGCFileDetails", 1, ApiMethod.GET);
        this.steamId = steamId;
        this.ugcId = ugcId;
        this.appId = appId;
    }

    @Override
    public void populateParams(Map<String, String> params) {
        writeOptional(params, "steamid", steamId, Long::toUnsignedString);
        params.put("ugcid", Long.toUnsignedString(ugcId));
        params.put("appid", Integer.toUnsignedString(appId));
    }

    @Override
    protected UGCFileDetails parseJson(JsonObject json) throws SteamWebApiException {
        if (json.has("status")) {
            var result = Result.values()[json.getAsJsonObject("status").getAsJsonPrimitive("code").getAsInt()];
            if (result != Result.OK) {
                throw new ResultException(result);
            }
        }

        var data = json.getAsJsonObject("data");
        return GSON.fromJson(data, UGCFileDetails.class);
    }
}
