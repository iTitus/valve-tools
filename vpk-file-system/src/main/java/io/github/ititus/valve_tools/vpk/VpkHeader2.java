package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VpkHeader2 {

    static final int SIZE = 16;

    private final int fileDataSectionSize;
    private final int archiveMD5SectionSize;
    private final int otherMD5SectionSize;
    private final int signatureSectionSize;

    private VpkHeader2(int fileDataSectionSize, int archiveMD5SectionSize, int otherMD5SectionSize, int signatureSectionSize) {
        this.fileDataSectionSize = fileDataSectionSize;
        this.archiveMD5SectionSize = archiveMD5SectionSize;
        this.otherMD5SectionSize = otherMD5SectionSize;
        this.signatureSectionSize = signatureSectionSize;
    }

    static VpkHeader2 load(ByteBuffer bb) throws IOException {
        int fileDataSectionSize = bb.getInt();

        int archiveMD5SectionSize = bb.getInt();
        if (Integer.remainderUnsigned(archiveMD5SectionSize, VpkArchiveMd5Entry.SIZE) != 0) {
            throw new VpkException("wrong size of archiveMd5Section");
        }

        int otherMD5SectionSize = bb.getInt();
        if (otherMD5SectionSize != VpkOtherMd5Section.SIZE) {
            throw new VpkException("wrong size of otherMD5Section");
        }

        int signatureSectionSize = bb.getInt();

        return new VpkHeader2(fileDataSectionSize, archiveMD5SectionSize, otherMD5SectionSize, signatureSectionSize);
    }

    public long getFileDataSectionSize() {
        return Integer.toUnsignedLong(fileDataSectionSize);
    }

    public long getArchiveMD5SectionSize() {
        return Integer.toUnsignedLong(archiveMD5SectionSize);
    }

    public int getArchiveMD5SectionSizeInt() {
        return archiveMD5SectionSize;
    }

    public long getOtherMD5SectionSize() {
        return Integer.toUnsignedLong(otherMD5SectionSize);
    }

    public long getSignatureSectionSize() {
        return Integer.toUnsignedLong(signatureSectionSize);
    }
}
