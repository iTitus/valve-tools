package io.github.ititus.valve_tools.kv;

public abstract sealed class KvBase permits KvPrimitive, KeyValues {

    public KeyValues asKeyValues() {
        throw new UnsupportedOperationException();
    }

    public KvPrimitive asPrimitive() {
        throw new UnsupportedOperationException();
    }
}
