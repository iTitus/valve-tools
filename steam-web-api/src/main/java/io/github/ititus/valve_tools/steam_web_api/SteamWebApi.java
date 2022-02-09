package io.github.ititus.valve_tools.steam_web_api;

import io.github.ititus.commons.io.HttpStatus;
import io.github.ititus.valve_tools.steam_web_api.exception.HttpIOException;
import io.github.ititus.valve_tools.steam_web_api.exception.HttpStatusException;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.RemoteStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import static java.net.http.HttpResponse.BodyHandlers;

public final class SteamWebApi {

    private final String apiKey;
    private final HttpClient httpClient;

    private SteamWebApi(String apiKey, HttpClient httpClient) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
    }

    public static SteamWebApi create() {
        return builder().build();
    }

    public static SteamWebApi.Builder builder() {
        return new Builder();
    }

    private static String extractBody(HttpResponse<InputStream> response) throws SteamWebApiException {
        List<String> encodings = response.headers().allValues("Content-Encoding");
        if (encodings.size() > 1) {
            throw new HttpIOException("Multiple encoding methods not supported");
        }

        ByteArrayOutputStream os = response.headers().firstValue("Content-Length")
                .map(s -> {
                    try {
                        return new ByteArrayOutputStream(Integer.parseInt(s));
                    } catch (NumberFormatException ignored) {
                        return null;
                    }
                })
                .orElseGet(ByteArrayOutputStream::new);

        if (encodings.isEmpty()) {
            try (InputStream is = response.body()) {
                is.transferTo(os);
            } catch (IOException e) {
                throw new HttpIOException(e);
            }
        } else {
            String encoding = encodings.get(0);
            if ("gzip".equals(encoding) || "x-gzip".equals(encoding)) {
                try (InputStream is = new GZIPInputStream(response.body())) {
                    is.transferTo(os);
                } catch (IOException e) {
                    throw new HttpIOException(e);
                }
            } else if ("deflate".equals(encoding)) {
                try (InputStream is = new ZipInputStream(response.body())) {
                    is.transferTo(os);
                } catch (IOException e) {
                    throw new HttpIOException(e);
                }
            } else {
                throw new HttpIOException("Encoding " + encoding + " is not supported");
            }
        }

        return os.toString(StandardCharsets.UTF_8);
    }

    public String getApiKey() {
        return apiKey;
    }

    public RemoteStorage remoteStorage() {
        return new RemoteStorage(this);
    }

    public String request(ApiMethod apiMethod, String url, ResponseFormat responseFormat, Map<String, String> params) throws SteamWebApiException {
        params = buildParams(responseFormat, params);

        HttpRequest request = apiMethod.buildRequest(url, params)
                .header("Accept-Encoding", "gzip, deflate")
                .build();
        HttpResponse<InputStream> response;
        try {
            response = httpClient.send(request, BodyHandlers.ofInputStream());
        } catch (Exception e) {
            throw new SteamWebApiException("Error while communicating with the Steam Web API", e);
        }

        String body = extractBody(response);
        HttpStatus status = HttpStatus.of(response.statusCode());

        if (!status.isOk()) {
            throw new HttpStatusException(status, body);
        }

        return body;
    }

    private Map<String, String> buildParams(ResponseFormat responseFormat, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }

        if (responseFormat != ResponseFormat.JSON) {
            params.put("format", responseFormat.getName());
        }

        if (apiKey != null) {
            params.put("key", apiKey);
        }

        return params;
    }

    public static final class Builder {

        private String apiKey = null;
        private HttpClient httpClient = null;

        public Builder apiKey(String apiKey) {
            if (this.apiKey != null) {
                throw new IllegalStateException("api key already set");
            } else if (apiKey != null && !apiKey.matches("^[0-9A-F]{32}$")) {
                throw new IllegalArgumentException("invalid api key");
            }

            this.apiKey = apiKey;
            return this;
        }

        public Builder httpClient(HttpClient httpClient) {
            if (this.httpClient != null) {
                throw new IllegalStateException("http client already set");
            }

            this.httpClient = Objects.requireNonNull(httpClient);
            return this;
        }

        public SteamWebApi build() {
            if (httpClient == null) {
                httpClient = HttpClient.newHttpClient();
            }

            return new SteamWebApi(apiKey, httpClient);
        }
    }
}
