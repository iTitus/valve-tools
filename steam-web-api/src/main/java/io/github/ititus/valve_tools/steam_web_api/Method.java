package io.github.ititus.valve_tools.steam_web_api;

import io.github.ititus.valve_tools.steam_web_api.exception.ResultException;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;

import java.util.Objects;

public abstract class Method<T extends Interface, R> implements Parameters {

    private final T apiInterface;
    private final String name;
    private final int version;
    private final ApiMethod apiMethod;
    private final ResponseFormat responseFormat;

    protected Method(T apiInterface, String name, int version, ApiMethod apiMethod,
                     ResponseFormat responseFormat) {
        this.apiInterface = Objects.requireNonNull(apiInterface);
        this.name = Objects.requireNonNull(name);
        this.version = version;
        this.apiMethod = Objects.requireNonNull(apiMethod);
        this.responseFormat = Objects.requireNonNull(responseFormat);
    }

    public T getApiInterface() {
        return apiInterface;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public ApiMethod getHttpMethod() {
        return apiMethod;
    }

    public ResponseFormat getFormat() {
        return responseFormat;
    }

    public String getBaseUrl() {
        return "https://api.steampowered.com";
    }

    public String getUrl() {
        return getBaseUrl() + "/" + apiInterface.getName() + "/" + name + "/v" + version + "/";
    }

    protected abstract R parse(String data) throws SteamWebApiException;

    public R request() throws SteamWebApiException {
        try {
            return parse(apiInterface.request(apiMethod, getUrl(), responseFormat, this));
        } catch (SteamWebApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ResultException(e);
        }
    }
}
