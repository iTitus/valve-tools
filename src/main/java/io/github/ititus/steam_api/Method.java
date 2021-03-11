package io.github.ititus.steam_api;

import io.github.ititus.steam_api.exception.ResultException;
import io.github.ititus.steam_api.exception.SteamWebApiException;

import java.util.Objects;

public abstract class Method<T extends Interface, R> implements Parameters {

    private final T apiInterface;
    private final String name;
    private final int version;
    private final SteamWebApi.HttpMethod httpMethod;
    private final SteamWebApi.Format format;

    protected Method(T apiInterface, String name, int version, SteamWebApi.HttpMethod httpMethod,
                     SteamWebApi.Format format) {
        this.apiInterface = Objects.requireNonNull(apiInterface);
        this.name = Objects.requireNonNull(name);
        this.version = version;
        this.httpMethod = Objects.requireNonNull(httpMethod);
        this.format = Objects.requireNonNull(format);
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

    public SteamWebApi.HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public SteamWebApi.Format getFormat() {
        return format;
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
            return parse(apiInterface.request(httpMethod, getUrl(), format, this));
        } catch (SteamWebApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ResultException(e);
        }
    }
}
