package io.github.ititus.steam_api.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class UnixTime {

    public static class Seconds extends TypeAdapter<Instant> {

        @Override
        public void write(JsonWriter out, Instant value) throws IOException {
            out.value(value.getEpochSecond());
        }

        @Override
        public Instant read(JsonReader in) throws IOException {
            return Instant.ofEpochSecond(in.nextLong());
        }
    }

    public static class Millis extends TypeAdapter<Instant> {

        @Override
        public void write(JsonWriter out, Instant value) throws IOException {
            out.value(value.toEpochMilli());
        }

        @Override
        public Instant read(JsonReader in) throws IOException {
            return Instant.ofEpochMilli(in.nextLong());
        }
    }
}
