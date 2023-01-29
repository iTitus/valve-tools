package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VpkOtherMd5Section {

    static final int SIZE = 48;

    private final ByteBuffer treeChecksum;
    private final ByteBuffer archiveMd5SectionChecksum;
    private final ByteBuffer totalChecksum;

    private VpkOtherMd5Section(ByteBuffer treeChecksum, ByteBuffer archiveMd5SectionChecksum, ByteBuffer totalChecksum) {
        this.treeChecksum = treeChecksum;
        this.archiveMd5SectionChecksum = archiveMd5SectionChecksum;
        this.totalChecksum = totalChecksum;
    }

    static VpkOtherMd5Section load(DataReader r) throws IOException {
        var treeChecksum = r.readByteBuffer(16).asReadOnlyBuffer();
        var archiveMd5SectionChecksum = r.readByteBuffer(16).asReadOnlyBuffer();
        var totalChecksum = r.readByteBuffer(16).asReadOnlyBuffer();
        return new VpkOtherMd5Section(treeChecksum, archiveMd5SectionChecksum, totalChecksum);
    }

    public ByteBuffer getTreeChecksum() {
        return treeChecksum;
    }

    public ByteBuffer getArchiveMd5SectionChecksum() {
        return archiveMd5SectionChecksum;
    }

    public ByteBuffer getTotalChecksum() {
        return totalChecksum;
    }
}
