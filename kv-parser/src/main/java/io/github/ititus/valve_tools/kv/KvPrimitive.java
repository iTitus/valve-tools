package io.github.ititus.valve_tools.kv;

import java.util.Objects;

public final class KvPrimitive extends KvBase {

    private final String value;

    KvPrimitive(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public Object getValue() {
        return value;
    }

    @Override
    public KvPrimitive asPrimitive() {
        return this;
    }

    public String asString() {
        return value;
    }

    public int asUInt() {
        return Integer.parseUnsignedInt(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
