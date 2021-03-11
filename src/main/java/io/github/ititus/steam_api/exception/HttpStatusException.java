package io.github.ititus.steam_api.exception;

public class HttpStatusException extends SteamWebApiException {

    public HttpStatusException(int status, String body) {
        super("HTTP Status: " + status + " | " + body);
    }
}
