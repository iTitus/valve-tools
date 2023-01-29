package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VpkArchiveMd5Entry {

    static final int SIZE = 28;

    private final int archiveIndex;
    private final int offset;
    private final int length;
    private final ByteBuffer checksum;

    private VpkArchiveMd5Entry(int archiveIndex, int offset, int length, ByteBuffer checksum) {
        this.archiveIndex = archiveIndex;
        this.offset = offset;
        this.length = length;
        this.checksum = checksum;
    }

    static VpkArchiveMd5Entry load(DataReader r) throws IOException {
        var archiveIndex = r.readUInt();
        var offset = r.readUInt();
        var length = r.readUInt();
        var checksum = r.readByteBuffer(16).asReadOnlyBuffer();
        return new VpkArchiveMd5Entry(archiveIndex, offset, length, checksum);
    }

    public long getArchiveIndex() {
        return Integer.toUnsignedLong(archiveIndex);
    }

    public long getOffset() {
        return Integer.toUnsignedLong(offset);
    }

    public long getLength() {
        return Integer.toUnsignedLong(length);
    }

    public ByteBuffer getChecksum() {
        return checksum;
    }
}
