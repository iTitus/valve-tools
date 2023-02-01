package io.github.ititus.valve_tools.vpk.internal;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;

public final class IoUtil {

    private IoUtil() {}

    public static String readString(ByteBuffer bb) {
        StringBuilder sb = new StringBuilder();
        byte b;
        while ((b = bb.get()) != 0) {
            if (b < 0) {
                throw new IllegalArgumentException("read non-ascii char");
            }

            sb.append((char) b);
        }

        return sb.toString();
    }

    public static ByteBuffer sliceAdvance(ByteBuffer bb, int size) {
        var oldPos = bb.position();
        bb.position(oldPos + size);
        return bb.slice(oldPos, size).order(ByteOrder.LITTLE_ENDIAN);
    }

    public static ByteBuffer sliceAdvance(SeekableByteChannel ch, int size) throws IOException {
        var bb = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        if (ch.read(bb) != size) {
            throw new EOFException();
        }
        bb.flip();
        return bb;
    }
}
