package io.github.ititus.valve_tools.steam_web_api.exception;

import io.github.ititus.io.HttpStatus;

public class HttpStatusException extends HttpIOException {

    public HttpStatusException(HttpStatus status, String body) {
        super("HTTP Status: " + status + " (" + status.code() + ") | " + body);
    }
}
