package io.github.ititus.valve_tools.steam_web_api.exception;

public class HttpStatusException extends HttpIOException {

    public HttpStatusException(int status, String body) {
        super("HTTP Status: " + status + " | " + body);
    }
}
