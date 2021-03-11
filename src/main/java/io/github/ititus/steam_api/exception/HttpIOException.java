package io.github.ititus.steam_api.exception;

public class HttpIOException extends SteamWebApiException {

    public HttpIOException(Throwable cause) {
        super(cause);
    }

    public HttpIOException(String message) {
        super(message);
    }
}
