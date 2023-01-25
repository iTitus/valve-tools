package io.github.ititus.valve_tools.steam_web_api.json;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.ititus.commons.math.number.JavaMath;

import java.io.IOException;
import java.time.Instant;

public final class UnixTime {

    private UnixTime() {
    }

    public static final class Seconds extends TypeAdapter<Instant> {

        @Override
        public void write(JsonWriter out, Instant value) throws IOException {
            var epochSecond = value.getEpochSecond();
            if (epochSecond < 0 || epochSecond > JavaMath.UNSIGNED_INT_MAX_VALUE) {
                throw new JsonSyntaxException("out of bounds for uint32 timestamp");
            }

            out.value(epochSecond);
        }

        @Override
        public Instant read(JsonReader in) throws IOException {
            return Instant.ofEpochSecond(Integer.parseUnsignedInt(in.nextString()));
        }
    }
}
