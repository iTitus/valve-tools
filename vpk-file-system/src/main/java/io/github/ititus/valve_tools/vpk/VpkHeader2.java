package io.github.ititus.valve_tools.vpk;

import java.io.IOException;

public class VpkHeader2 {

    public static final int SIZE = 16;

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

    static VpkHeader2 load(DataReader r) throws IOException {
        return new VpkHeader2(
                r.readUInt(),
                r.readUInt(),
                r.readUInt(),
                r.readUInt()
        );
    }

    public int getFileDataSectionSize() {
        return fileDataSectionSize;
    }

    public int getArchiveMD5SectionSize() {
        return archiveMD5SectionSize;
    }

    public int getOtherMD5SectionSize() {
        return otherMD5SectionSize;
    }

    public int getSignatureSectionSize() {
        return signatureSectionSize;
    }
}
