package io.github.ititus.valve_tools.steam_web_api.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public final class EnumByOrdinal implements TypeAdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Object[] enumConstants = type.getRawType().getEnumConstants();
        if (enumConstants == null) {
            return null;
        }

        return new TypeAdapter<>() {

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                out.value(Integer.toUnsignedString(((Enum<?>) value).ordinal()));
            }

            @Override
            public T read(JsonReader in) throws IOException {
                return (T) enumConstants[Integer.parseUnsignedInt(in.nextString())];
            }
        };
    }
}
