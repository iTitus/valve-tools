package io.github.ititus.valve_tools.kv;

import java.util.Objects;

public final class KvPrimitive extends KvBase {

    private final Object value;

    KvPrimitive(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Objects.toString(value);
    }
}
