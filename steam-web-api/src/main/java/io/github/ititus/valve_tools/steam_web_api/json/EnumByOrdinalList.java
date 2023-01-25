package io.github.ititus.valve_tools.steam_web_api.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

public final class EnumByOrdinalList implements TypeAdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        var paramType = (ParameterizedType) type.getType();
        Class<?> clazz = (Class<?>) paramType.getRawType();
        if (!clazz.isAssignableFrom(ArrayList.class) || !Iterable.class.isAssignableFrom(clazz)) {
            return null;
        }

        Object[] enumConstants = ((Class<?>) paramType.getActualTypeArguments()[0]).getEnumConstants();
        if (enumConstants == null) {
            return null;
        }

        return new TypeAdapter<>() {

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                out.beginArray();
                for (var obj : (Iterable<?>) value) {
                    out.value(Integer.toUnsignedString(((Enum<?>) obj).ordinal()));
                }

                out.endArray();
            }

            @Override
            public T read(JsonReader in) throws IOException {
                ArrayList<Object> list = new ArrayList<>();
                in.beginArray();
                while (in.peek() != JsonToken.END_ARRAY) {
                    list.add(enumConstants[Integer.parseUnsignedInt(in.nextString())]);
                }

                in.endArray();
                return (T) list;
            }
        };
    }
}
