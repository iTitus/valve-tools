package io.github.ititus.valve_tools.steam_web_api.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public final class UnsignedLong extends TypeAdapter<Long> {

    @Override
    public void write(JsonWriter out, Long value) throws IOException {
        out.value(Long.toUnsignedString(value));
    }

    @Override
    public Long read(JsonReader in) throws IOException {
        return Long.parseUnsignedLong(in.nextString());
    }
}
