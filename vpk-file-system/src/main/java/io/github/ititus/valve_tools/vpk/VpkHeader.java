package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VpkHeader {

    static final int SIZE = 12;

    private final int version;
    private final int treeSize;

    private VpkHeader(int version, int treeSize) {
        this.version = version;
        this.treeSize = treeSize;
    }

    static VpkHeader load(ByteBuffer bb) throws IOException {
        int signature = bb.getInt();
        if (signature != VpkFile.SIGNATURE) {
            throw new VpkException("invalid vpk signature");
        }

        int version = bb.getInt();
        int treeSize = bb.getInt();
        return new VpkHeader(version, treeSize);
    }

    public int getVersion() {
        return version;
    }

    public long getTreeSize() {
        return Integer.toUnsignedLong(treeSize);
    }
}
