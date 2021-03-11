package io.github.ititus.steam_api;

import io.github.ititus.steam_api.exception.HttpIOException;
import io.github.ititus.steam_api.exception.HttpStatusException;
import io.github.ititus.steam_api.exception.SteamWebApiException;
import io.github.ititus.steam_api.remote_storage.RemoteStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import static java.net.http.HttpResponse.BodyHandlers;

public final class SteamWebApi {

    private final String apiKey;

    private final HttpClient httpClient;

    private SteamWebApi(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                //.followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public static SteamWebApi create() {
        return builder().build();
    }

    public static SteamWebApi.Builder builder() {
        return new Builder();
    }

    private static String buildQueryString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        StringBuilder query = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (first) {
                query.append('?');
                first = false;
            } else {
                query.append('&');
            }

            query.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
        }

        return query.toString();
    }

    private static BodyPublisher ofFormData(Map<String, String> data) {
        if (data == null || data.isEmpty()) {
            return BodyPublishers.noBody();
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }

            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return BodyPublishers.ofString(builder.toString());
    }

    public String getApiKey() {
        return apiKey;
    }

    public RemoteStorage remoteStorage() {
        return new RemoteStorage(this);
    }

    public String request(HttpMethod httpMethod, String url, Format format, Map<String, String> params) throws SteamWebApiException {
        params = buildParams(format, params);

        HttpRequest request = httpMethod.buildRequest(url, params)
                .header("Accept-Encoding", "gzip, deflate")
                .build();
        HttpResponse<InputStream> response;
        try {
            response = httpClient.send(request, BodyHandlers.ofInputStream());
        } catch (Exception e) {
            throw new SteamWebApiException("Error while communicating with the Steam Web API", e);
        }

        List<String> encodings = response.headers().allValues("Content-Encoding");
        boolean compressed = !encodings.isEmpty();
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

        int status = response.statusCode();
        String body = os.toString(StandardCharsets.UTF_8);

        if (status != 200) {
            throw new HttpStatusException(status, body);
        }

        return body;
    }

    private Map<String, String> buildParams(Format format, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }

        if (format != Format.JSON) {
            params.put("format", format.getName());
        }

        if (apiKey != null) {
            params.put("key", apiKey);
        }

        return params;
    }

    public enum HttpMethod {

        GET {
            @Override
            public HttpRequest.Builder buildRequest(String url, Map<String, String> params) {
                return HttpRequest.newBuilder()
                        .uri(URI.create(url + buildQueryString(params)))
                        .GET();
            }
        },
        POST {
            @Override
            public HttpRequest.Builder buildRequest(String url, Map<String, String> params) {
                return HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(ofFormData(params));
            }
        };

        public abstract HttpRequest.Builder buildRequest(String url, Map<String, String> params);

    }

    public enum Format {

        JSON("json"),
        XML("xml"),
        VDF("vdf");

        private final String name;

        Format(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static final class Builder {

        private String apiKey = null;

        private SteamWebApi.Builder apiKey(String apiKey) {
            if (apiKey != null && !apiKey.matches("^[0-9A-F]{32}$")) {
                throw new IllegalArgumentException("invalid api key");
            }

            this.apiKey = apiKey;
            return this;
        }

        public SteamWebApi build() {
            return new SteamWebApi(apiKey);
        }
    }
}
