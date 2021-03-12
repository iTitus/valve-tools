package io.github.ititus.steam_api.exception;

public class HttpStatusException extends HttpIOException {

    public HttpStatusException(int status, String body) {
        super("HTTP Status: " + status + " | " + body);
    }
}
