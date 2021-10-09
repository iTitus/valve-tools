package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.ByteBuffer;

@FunctionalInterface
interface DataReader {

    private static short bytesToShort(byte b1, byte b2) {
        return (short) (Byte.toUnsignedInt(b1) | (Byte.toUnsignedInt(b2) << 8));
    }

    private static int bytesToInt(byte b1, byte b2, byte b3, byte b4) {
        return Byte.toUnsignedInt(b1) | (Byte.toUnsignedInt(b2) << 8) | (Byte.toUnsignedInt(b3) << 16) | (Byte.toUnsignedInt(b4) << 24);
    }

    byte readByte() throws IOException;

    default short readUShort() throws IOException {
        return bytesToShort(readByte(), readByte());
    }

    default int readUInt() throws IOException {
        return bytesToInt(readByte(), readByte(), readByte(), readByte());
    }

    default String readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        byte b;
        while ((b = readByte()) != 0) {
            sb.append((char) Byte.toUnsignedInt(b));
        }

        return sb.toString();
    }

    default void read(ByteBuffer target, int size) throws IOException {
        for (int i = 0; i < size; i++) {
            target.put(readByte());
        }
    }
}
