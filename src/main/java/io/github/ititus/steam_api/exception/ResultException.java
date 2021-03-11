package io.github.ititus.steam_api.exception;

import io.github.ititus.steam_api.Result;

public class ResultException extends SteamWebApiException {

    public ResultException(Result result) {
        super(result != null ? result + " (" + result.getId() + ") - " + result.getDescription() : "null status");
    }

    public ResultException(String message) {
        super(message);
    }

    public ResultException(Throwable cause) {
        super(cause);
    }
}
