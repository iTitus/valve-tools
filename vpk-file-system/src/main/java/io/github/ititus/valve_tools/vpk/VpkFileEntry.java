package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VpkFileEntry extends VpkEntry {

    private static final short TERMINATOR = -1;

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
        if (preloadBytes > 0) {
            preload = ByteBuffer.allocate(preloadBytes);
            r.read(preload, preloadBytes);
            preload = preload.asReadOnlyBuffer();
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
        return preloadBytes + entryLength;
    }

    public int getCrc() {
        return crc;
    }

    public short getPreloadBytes() {
        return preloadBytes;
    }

    public short getArchiveIndex() {
        return archiveIndex;
    }

    public boolean hasArchiveIndex() {
        return archiveIndex != Short.MAX_VALUE;
    }

    public int getEntryOffset() {
        return entryOffset;
    }

    public int getEntryLength() {
        return entryLength;
    }

    public ByteBuffer getPreload() {
        return preload != null ? preload.asReadOnlyBuffer() : null;
    }
}