package io.github.ititus.valve_tools.steam_web_api.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public final class UnsignedInt extends TypeAdapter<Integer> {

    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        out.value(Integer.toUnsignedString(value));
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
        return Integer.parseUnsignedInt(in.nextString());
    }
}
