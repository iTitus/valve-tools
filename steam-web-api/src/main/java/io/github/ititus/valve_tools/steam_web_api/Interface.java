package io.github.ititus.valve_tools.steam_web_api;

import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;

import java.util.Map;
import java.util.Objects;

public class Interface {

    private final String name;
    private final boolean service;
    private final SteamWebApi api;

    protected Interface(String name, SteamWebApi api) {
        this.name = Objects.requireNonNull(name);
        this.service = name.endsWith("Service");
        this.api = Objects.requireNonNull(api);
    }

    public String getName() {
        return name;
    }

    public boolean isService() {
        return service;
    }

    public SteamWebApi getApi() {
        return api;
    }

    protected String request(
            ApiMethod apiMethod,
            String url,
            ResponseFormat responseFormat,
            Parameters parameters
    ) throws SteamWebApiException {
        Map<String, String> stringParams;
        if (isService()) {
            stringParams = Map.of("input_json", parameters.toJson().toString());
        } else {
            stringParams = parameters.toMap();
        }

        return api.request(apiMethod, url, responseFormat, stringParams);
    }
}
