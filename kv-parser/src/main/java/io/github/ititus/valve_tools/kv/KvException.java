package io.github.ititus.valve_tools.kv;

import java.io.IOException;

public class KvException extends IOException {

    public KvException() {
        super();
    }

    public KvException(String message) {
        super(message);
    }


    public KvException(String message, Throwable cause) {
        super(message, cause);
    }


    public KvException(Throwable cause) {
        super(cause);
    }
}
