package io.github.ititus.steam_api.exception;

public class SteamWebApiException extends Exception {

    public SteamWebApiException() {
        super();
    }

    public SteamWebApiException(String message) {
        super(message);
    }

    public SteamWebApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public SteamWebApiException(Throwable cause) {
        super(cause);
    }
}
