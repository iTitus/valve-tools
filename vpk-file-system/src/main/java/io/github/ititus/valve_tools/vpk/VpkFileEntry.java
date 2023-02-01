package io.github.ititus.valve_tools.vpk;

import io.github.ititus.valve_tools.vpk.internal.ByteBufferChannel;
import io.github.ititus.valve_tools.vpk.internal.IoUtil;
import io.github.ititus.valve_tools.vpk.internal.EmptyChannel;
import io.github.ititus.valve_tools.vpk.internal.MultiReadOnlyChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import java.util.Set;

public final class VpkFileEntry extends VpkEntry {

    static final int SIZE = 18;
    private static final short TERMINATOR = -1;
    private static final short NO_EXTERNAL_ARCHIVE_MARKER = Short.MAX_VALUE;

    private final int crc;
    private final short preloadBytes;
    private final short archiveIndex;
    private final int entryOffset;
    private final int entryLength;
    private final ByteBuffer preload;
    private final ByteBuffer entry;

    private VpkFileEntry(VpkFile file, VpkDirEntry parent, String name, int crc, short preloadBytes, short archiveIndex, int entryOffset, int entryLength, ByteBuffer preload, ByteBuffer entry) {
        super(file, parent, name);
        this.crc = crc;
        this.preloadBytes = preloadBytes;
        this.archiveIndex = archiveIndex;
        this.entryOffset = entryOffset;
        this.entryLength = entryLength;
        this.preload = preload;
        this.entry = entry;
    }

    static VpkFileEntry load(VpkDirEntry parent, String path, ByteBuffer bb) throws IOException {
        int crc = bb.getInt();
        short preloadBytes = bb.getShort();
        short archiveIndex = bb.getShort();
        int entryOffset = bb.getInt();
        int entryLength = bb.getInt();

        short terminator = bb.getShort();
        if (terminator != TERMINATOR) {
            throw new VpkException("expected terminator");
        }

        ByteBuffer preload = null;
        var preloadBytesUnsigned = Short.toUnsignedInt(preloadBytes);
        if (preloadBytesUnsigned > 0) {
            preload = IoUtil.sliceAdvance(bb, preloadBytesUnsigned);
        }

        ByteBuffer entry = null;
        var entryBytesUnsigned = Integer.toUnsignedLong(entryLength);
        if (archiveIndex == NO_EXTERNAL_ARCHIVE_MARKER && entryBytesUnsigned > 0) {
            long offset = VpkHeader.SIZE + (parent.getFile().getHeaderV2() != null ? VpkHeader2.SIZE : 0) + parent.getFile().getHeaderV1().getTreeSize() + Integer.toUnsignedLong(entryOffset);
            if (offset + entryBytesUnsigned < bb.limit()) {
                entry = bb.slice(Math.toIntExact(offset), Math.toIntExact(entryBytesUnsigned)).asReadOnlyBuffer();
            }
        }

        return new VpkFileEntry(parent.getFile(), parent, path, crc, preloadBytes, archiveIndex, entryOffset, entryLength, preload, entry);
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
        return archiveIndex != NO_EXTERNAL_ARCHIVE_MARKER;
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

    public ByteBuffer getEntry() {
        return entry != null ? entry.asReadOnlyBuffer() : null;
    }

    public SeekableByteChannel newByteChannel(Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        Objects.requireNonNull(attrs, "attrs");
        Objects.requireNonNull(options, "options");
        for (OpenOption option : options) {
            Objects.requireNonNull(option);
            if (!(option instanceof StandardOpenOption)) {
                throw new IllegalArgumentException("option class: " + option.getClass());
            }
        }

        if (options.contains(StandardOpenOption.WRITE) || options.contains(StandardOpenOption.APPEND)) {
            throw new ReadOnlyFileSystemException();
        }

        var preload = getPreload();
        var content = getFile().loadContent(this);
        if (content == null && preload == null) {
            return new EmptyChannel();
        } else if (content == null) {
            return new ByteBufferChannel(preload);
        } else if (preload == null) {
            return content;
        }

        return new MultiReadOnlyChannel(new ByteBufferChannel(preload), content);
    }
}
