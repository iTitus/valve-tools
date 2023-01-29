package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class VpkFileEntry extends VpkEntry {

    private static final short TERMINATOR = -1;
    private static final short EXTERNAL_ARCHIVE_MARKER = Short.MAX_VALUE;

    private final int crc;
    private final short preloadBytes;
    private final short archiveIndex;
    private final int entryOffset;
    private final int entryLength;
    private final ByteBuffer preload;

    private VpkFileEntry(VpkDirEntry parent, String name, int crc, short preloadBytes, short archiveIndex, int entryOffset, int entryLength, ByteBuffer preload) {
        super(parent, name);
        this.crc = crc;
        this.preloadBytes = preloadBytes;
        this.archiveIndex = archiveIndex;
        this.entryOffset = entryOffset;
        this.entryLength = entryLength;
        this.preload = preload;
    }

    static VpkFileEntry load(VpkDirEntry parent, String path, DataReader r) throws IOException {
        int crc = r.readUInt();
        short preloadBytes = r.readUShort();
        short archiveIndex = r.readUShort();
        int entryOffset = r.readUInt();
        int entryLength = r.readUInt();

        short terminator = r.readUShort();
        if (terminator != TERMINATOR) {
            throw new VpkException("expected terminator");
        }

        ByteBuffer preload;
        var preloadBytesUnsigned = Short.toUnsignedInt(preloadBytes);
        if (preloadBytesUnsigned > 0) {
            preload = r.readByteBuffer(preloadBytesUnsigned).asReadOnlyBuffer();
        } else {
            preload = null;
        }

        return new VpkFileEntry(parent, path, crc, preloadBytes, archiveIndex, entryOffset, entryLength, preload);
    }

    @Override
    public boolean isRegularFile() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long size() {
        return Short.toUnsignedLong(preloadBytes) + Integer.toUnsignedLong(entryLength);
    }

    public int getCrc() {
        return crc;
    }

    public int getPreloadBytes() {
        return Short.toUnsignedInt(preloadBytes);
    }

    public short getArchiveIndex() {
        return archiveIndex;
    }

    public boolean hasExternalArchiveIndex() {
        return archiveIndex != EXTERNAL_ARCHIVE_MARKER;
    }

    public long getEntryOffset() {
        return Integer.toUnsignedLong(entryOffset);
    }

    public long getEntryLength() {
        return Integer.toUnsignedLong(entryLength);
    }

    public ByteBuffer getPreload() {
        return preload != null ? preload.asReadOnlyBuffer() : null;
    }
}
