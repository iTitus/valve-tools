package io.github.ititus.valve_tools.steam_web_api.exception;

import io.github.ititus.valve_tools.steam_web_api.common.Result;

public class ResultException extends SteamWebApiException {

    public ResultException(Result result) {
        super(result != null ? result + " (" + result.ordinal() + ") - " + result.getDescription() : "null status");
    }

    public ResultException(String message) {
        super(message);
    }

    public ResultException(Throwable cause) {
        super(cause);
    }
}
