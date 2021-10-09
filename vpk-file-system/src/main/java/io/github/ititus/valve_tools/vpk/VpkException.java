package io.github.ititus.valve_tools.vpk;

import java.io.IOException;

public class VpkException extends IOException {

    public VpkException() {
        super();
    }

    public VpkException(String message) {
        super(message);
    }

    public VpkException(String message, Throwable cause) {
        super(message, cause);
    }

    public VpkException(Throwable cause) {
        super(cause);
    }
}
