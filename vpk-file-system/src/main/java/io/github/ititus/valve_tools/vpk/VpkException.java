package io.github.ititus.valve_tools.vpk;

import java.io.IOException;

public class VpkException extends IOException {

    VpkException() {
        super();
    }

    VpkException(String message) {
        super(message);
    }

    VpkException(String message, Throwable cause) {
        super(message, cause);
    }

    VpkException(Throwable cause) {
        super(cause);
    }
}
