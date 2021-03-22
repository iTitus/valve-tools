package io.github.ititus.valve_tools.steam_web_api.exception;

public class HttpIOException extends SteamWebApiException {

    public HttpIOException(Throwable cause) {
        super(cause);
    }

    public HttpIOException(String message) {
        super(message);
    }
}
