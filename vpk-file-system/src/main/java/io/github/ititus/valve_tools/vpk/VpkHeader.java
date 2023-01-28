package io.github.ititus.valve_tools.vpk;

import java.io.IOException;

public class VpkHeader {

    static final int SIZE = 12;

    private final int version;
    private final int treeSize;

    private VpkHeader(int version, int treeSize) {
        this.version = version;
        this.treeSize = treeSize;
    }

    static VpkHeader load(DataReader r) throws IOException {
        int signature = r.readUInt();
        if (signature != VpkFile.SIGNATURE) {
            throw new VpkException("invalid vpk signature");
        }

        int version = r.readUInt();
        int treeSize = r.readUInt();
        return new VpkHeader(version, treeSize);
    }

    public int getVersion() {
        return version;
    }

    public long getTreeSize() {
        return Integer.toUnsignedLong(treeSize);
    }
}
