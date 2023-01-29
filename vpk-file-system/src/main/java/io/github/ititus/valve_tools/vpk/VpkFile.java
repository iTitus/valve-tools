package io.github.ititus.valve_tools.vpk;

import io.github.ititus.commons.io.PathUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VpkFile {

    static final int SIGNATURE = 0x55aa1234;
    private static final int MAP_THRESHOLD = 8192;

    private final Path path;
    private final VpkHeader headerV1;
    private final VpkHeader2 headerV2;
    private final VpkDirEntry rootEntry;
    private final List<VpkArchiveMd5Entry> archiveMd5Entries;
    private final VpkOtherMd5Section otherMd5Section;
    private final VpkSignatureSection signatureSection;

    private VpkFile(Path path, VpkHeader headerV1, VpkHeader2 headerV2, VpkDirEntry rootEntry, List<VpkArchiveMd5Entry> archiveMd5Entries, VpkOtherMd5Section otherMd5Section, VpkSignatureSection signatureSection) {
        this.path = path;
        this.headerV1 = headerV1;
        this.headerV2 = headerV2;
        this.rootEntry = rootEntry;
        this.archiveMd5Entries = archiveMd5Entries;
        this.otherMd5Section = otherMd5Section;
        this.signatureSection = signatureSection;
    }

    public static VpkFile load(Path path) throws IOException {
        path = PathUtil.resolveRealFile(path);
        try (InputStream is = new BufferedInputStream(Files.newInputStream(path))) {
            return load(path, is);
        }
    }

    private static VpkFile load(Path path, InputStream is) throws IOException {
        return load(path, new DataReader() {

            @Override
            public byte readByte() throws IOException {
                int n = is.read();
                if (n == -1) {
                    throw new EOFException();
                }

                return (byte) n;
            }

            @Override
            public void read(ByteBuffer target, long size) throws IOException {
                var sizeInt = Math.toIntExact(size);
                if (target.hasArray()) {
                    if (is.readNBytes(target.array(), target.arrayOffset() + target.position(), sizeInt) != sizeInt) {
                        throw new EOFException();
                    }
                    target.position(target.position() + sizeInt);
                } else {
                    byte[] arr = new byte[sizeInt];
                    if (is.readNBytes(arr, 0, sizeInt) != sizeInt) {
                        throw new EOFException();
                    }
                    target.put(arr, 0, sizeInt);
                }
            }

            @Override
            public void skip(long n) throws IOException {
                if (n < 0) {
                    throw new IllegalArgumentException();
                }

                is.skipNBytes(n);
            }
        });
    }

    private static VpkFile load(Path path, DataInput di) throws IOException {
        return load(path, new DataReader() {

            @Override
            public byte readByte() throws IOException {
                return di.readByte();
            }

            @Override
            public void read(ByteBuffer target, long size) throws IOException {
                var sizeInt = Math.toIntExact(size);
                if (target.hasArray()) {
                    di.readFully(target.array(), target.arrayOffset() + target.position(), sizeInt);
                    target.position(target.position() + sizeInt);
                } else {
                    byte[] arr = new byte[sizeInt];
                    di.readFully(arr, 0, sizeInt);
                    target.put(arr, 0, sizeInt);
                }
            }

            @Override
            public void skip(long n) throws IOException {
                if (n < 0) {
                    throw new IllegalArgumentException();
                }

                var intN = Math.toIntExact(n);
                while (intN > 0) {
                    var ns = di.skipBytes(intN);
                    if (ns > 0 && ns <= intN) {
                        intN -= ns;
                    } else if (ns == 0) {
                        readByte();
                        intN--;
                    } else {
                        throw new IOException("unable to skip exactly");
                    }
                }
            }
        });
    }

    private static VpkFile load(Path path, DataReader r) throws IOException {
        VpkHeader headerV1 = VpkHeader.load(r);
        VpkHeader2 headerV2 = null;
        if (headerV1.getVersion() == 2) {
            headerV2 = VpkHeader2.load(r);
        } else if (headerV1.getVersion() != 1) {
            throw new VpkException("unknown version " + headerV1.getVersion());
        }

        VpkDirEntry rootEntry = readTree(r);

        List<VpkArchiveMd5Entry> archiveMd5Entries = null;
        VpkOtherMd5Section otherMd5Section = null;
        VpkSignatureSection signatureSection = null;
        if (headerV2 != null) {
            // assume that the actual directory index has the same size as advertised
            r.skip(headerV2.getFileDataSectionSize());

            int archiveMd5SectionSize = headerV2.getArchiveMD5SectionSizeInt();
            int count = Integer.divideUnsigned(archiveMd5SectionSize, VpkArchiveMd5Entry.SIZE);
            archiveMd5Entries = new ArrayList<>(count);
            for (int i = 0; Integer.compareUnsigned(i, count) < 0; i++) {
                archiveMd5Entries.add(VpkArchiveMd5Entry.load(r));
            }

            otherMd5Section = VpkOtherMd5Section.load(r);

            if (headerV2.getSignatureSectionSize() != 0) {
                signatureSection = VpkSignatureSection.load(r);
            }
        }

        return new VpkFile(path, headerV1, headerV2, rootEntry, archiveMd5Entries != null ? List.copyOf(archiveMd5Entries) : null, otherMd5Section, signatureSection);
    }

    private static VpkDirEntry readTree(DataReader r) throws IOException {
        VpkDirEntry rootEntry = new VpkDirEntry(null, "");

        for (String extension; !(extension = r.readString()).isEmpty(); ) {
            for (String path; !(path = r.readString()).isEmpty(); ) {
                VpkDirEntry dirEntry = " ".equals(path) ? rootEntry : rootEntry.resolveOrCreateDirs(path);
                for (String name; !(name = r.readString()).isEmpty(); ) {
                    String fullName = name + (" ".equals(extension) ? "" : "." + extension);
                    dirEntry.addChild(VpkFileEntry.load(dirEntry, fullName, r));
                }
            }
        }

        return rootEntry;
    }

    public Path getPath() {
        return path;
    }

    public VpkHeader getHeaderV1() {
        return headerV1;
    }

    public VpkHeader2 getHeaderV2() {
        return headerV2;
    }

    public VpkDirEntry getRootEntry() {
        return rootEntry;
    }

    public List<VpkArchiveMd5Entry> getArchiveMd5Entries() {
        return archiveMd5Entries;
    }

    public VpkOtherMd5Section getOtherMd5Section() {
        return otherMd5Section;
    }

    public VpkSignatureSection getSignatureSection() {
        return signatureSection;
    }

    public VpkEntry resolve(String path) throws IOException {
        return rootEntry.resolve(path);
    }

    public VpkEntry resolveFile(String path) throws IOException {
        return rootEntry.resolveFile(path);
    }

    ByteBuffer loadContent(VpkFileEntry file) throws IOException {
        var length = file.getEntryLength();
        if (length == 0) {
            return null;
        }

        long offset;
        Path path;
        if (file.hasExternalArchiveIndex()) {
            String name = PathUtil.getNameWithoutExtension(this.path);
            if (!name.endsWith("_dir")) {
                throw new VpkException("cannot resolve external archive file if main file (" + this.path + ") is not named with suffix '_dir'");
            }

            offset = file.getEntryOffset();
            var archiveIndex = file.getArchiveIndex();
            if (archiveIndex < 0 || archiveIndex >= 1000) {
                throw new VpkException("cannot get file with archive index " + archiveIndex);
            }

            path = this.path.resolveSibling(name.substring(0, name.length() - 3) + String.format(Locale.ROOT, "%03d", archiveIndex) + "." + PathUtil.getExtension(this.path).orElseThrow());
        } else {
            offset = VpkHeader.SIZE + (headerV2 != null ? VpkHeader2.SIZE : 0) + headerV1.getTreeSize() + file.getEntryOffset();
            path = this.path;
        }

        try (FileChannel ch = FileChannel.open(path, StandardOpenOption.READ)) {
            var size = ch.size();
            if (size < offset || (size - offset) < length) {
                throw new VpkException("not enough bytes in file to read full data");
            }

            if (length < MAP_THRESHOLD) {
                var bb = ByteBuffer.allocate((int) length);
                ch.read(bb, offset);
                bb.flip();
                return bb;
            }

            return ch.map(FileChannel.MapMode.READ_ONLY, offset, length);
        }
    }
}
