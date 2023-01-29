package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

    default byte[] readByteArray(int size) throws IOException {
        byte[] arr = new byte[size];
        read(ByteBuffer.wrap(arr), size);
        return arr;
    }

    default ByteBuffer readByteBuffer(int size) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        read(buf, size);
        buf.flip();
        return buf;
    }

    default void read(ByteBuffer target, long size) throws IOException {
        for (long i = 0; i < size; i++) {
            target.put(readByte());
        }
    }

    void skip(long n) throws IOException;

}
