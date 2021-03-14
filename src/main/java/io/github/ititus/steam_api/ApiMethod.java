package io.github.ititus.steam_api;

import io.github.ititus.util.HttpUtil;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public enum ApiMethod {

    GET((url, params) -> HttpRequest.newBuilder()
            .uri(URI.create(url + HttpUtil.buildQueryString(params)))
            .GET()
    ),
    POST((url, params) -> HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpUtil.ofFormData(params))
    );

    private final BiFunction<String, Map<String, String>, HttpRequest.Builder> buildRequestFunction;

    ApiMethod(BiFunction<String, Map<String, String>, HttpRequest.Builder> buildRequestFunction) {
        this.buildRequestFunction = Objects.requireNonNull(buildRequestFunction);
    }

    public HttpRequest.Builder buildRequest(String url, Map<String, String> params) {
        return buildRequestFunction.apply(url, params);
    }
}
