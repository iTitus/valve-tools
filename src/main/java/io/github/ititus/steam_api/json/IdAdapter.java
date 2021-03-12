package io.github.ititus.steam_api.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.function.IntFunction;

public abstract class IdAdapter<T extends IdAdapter.HasId> extends TypeAdapter<T> {

    private final IntFunction<T> factory;

    protected IdAdapter(IntFunction<T> factory) {
        this.factory = factory;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        out.value(value.getId());
    }

    @Override
    public T read(JsonReader in) throws IOException {
        return factory.apply(in.nextInt());
    }

    public interface HasId {

        int getId();

    }
}
