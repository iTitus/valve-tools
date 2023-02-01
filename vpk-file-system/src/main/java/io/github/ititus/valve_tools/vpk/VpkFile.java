package io.github.ititus.valve_tools.vpk;

import io.github.ititus.commons.io.PathUtil;
import io.github.ititus.valve_tools.vpk.internal.ByteBufferChannel;
import io.github.ititus.valve_tools.vpk.internal.IoUtil;
import io.github.ititus.valve_tools.vpk.internal.WrappedFileChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VpkFile {

    static final int SIGNATURE = 0x55aa1234;

    private final Path path;
    private final VpkHeader headerV1;
    private final VpkHeader2 headerV2;
    private final VpkDirEntry rootEntry;
    private final List<VpkArchiveMd5Entry> archiveMd5Entries;
    private final VpkOtherMd5Section otherMd5Section;
    private final VpkSignatureSection signatureSection;

    private VpkFile(Path path, VpkHeader headerV1, VpkHeader2 headerV2, List<VpkArchiveMd5Entry> archiveMd5Entries, VpkOtherMd5Section otherMd5Section, VpkSignatureSection signatureSection) {
        this.path = path;
        this.headerV1 = headerV1;
        this.headerV2 = headerV2;
        this.rootEntry = new VpkDirEntry(this, null, "");
        this.archiveMd5Entries = archiveMd5Entries;
        this.otherMd5Section = otherMd5Section;
        this.signatureSection = signatureSection;
    }

    public static VpkFile load(Path path) throws IOException {
        path = PathUtil.resolveRealFile(path);
        try (var fch = FileChannel.open(path, StandardOpenOption.READ)) {
            return load(path, fch.map(FileChannel.MapMode.READ_ONLY, 0, fch.size()).order(ByteOrder.LITTLE_ENDIAN));
        } catch (UnsupportedOperationException ignored) {
            try (var ch = Files.newByteChannel(path, StandardOpenOption.READ)) {
                return loadFromChannel(path, ch);
            }
        }
    }

    private static VpkFile load(Path path, ByteBuffer bb) throws IOException {
        return loadFromBuf(path, bb.order(ByteOrder.LITTLE_ENDIAN));
    }

    private static VpkFile loadFromBuf(Path path, ByteBuffer bb) throws IOException {
        VpkHeader headerV1 = VpkHeader.load(bb);
        VpkHeader2 headerV2 = null;
        if (headerV1.getVersion() == 2) {
            headerV2 = VpkHeader2.load(bb);
        } else if (headerV1.getVersion() != 1) {
            throw new VpkException("unknown version " + headerV1.getVersion());
        }

        List<VpkArchiveMd5Entry> archiveMd5Entries = null;
        VpkOtherMd5Section otherMd5Section = null;
        VpkSignatureSection signatureSection = null;
        if (headerV2 != null) {
            // skip file data section
            bb.position(Math.toIntExact(VpkHeader.SIZE + VpkHeader2.SIZE + headerV1.getTreeSize() + headerV2.getFileDataSectionSize()));

            int archiveMd5SectionSize = headerV2.getArchiveMD5SectionSizeInt();
            int count = Integer.divideUnsigned(archiveMd5SectionSize, VpkArchiveMd5Entry.SIZE);
            archiveMd5Entries = new ArrayList<>(count);
            for (int i = 0; Integer.compareUnsigned(i, count) < 0; i++) {
                archiveMd5Entries.add(VpkArchiveMd5Entry.load(bb));
            }

            otherMd5Section = VpkOtherMd5Section.load(bb);

            if (headerV2.getSignatureSectionSize() != 0) {
                signatureSection = VpkSignatureSection.load(bb);
            }
        }

        VpkFile vpkFile = new VpkFile(path, headerV1, headerV2, archiveMd5Entries != null ? List.copyOf(archiveMd5Entries) : null, otherMd5Section, signatureSection);
        bb.position(VpkHeader.SIZE + (headerV2 != null ? VpkHeader2.SIZE : 0));
        vpkFile.readTree(bb);
        return vpkFile;
    }

    private static VpkFile loadFromChannel(Path path, SeekableByteChannel ch) throws IOException {
        VpkHeader headerV1 = VpkHeader.load(IoUtil.sliceAdvance(ch, VpkHeader.SIZE));
        VpkHeader2 headerV2 = null;
        if (headerV1.getVersion() == 2) {
            headerV2 = VpkHeader2.load(IoUtil.sliceAdvance(ch, VpkHeader2.SIZE));
        } else if (headerV1.getVersion() != 1) {
            throw new VpkException("unknown version " + headerV1.getVersion());
        }

        var treeBuf = IoUtil.sliceAdvance(ch, Math.toIntExact(headerV1.getTreeSize()));

        List<VpkArchiveMd5Entry> archiveMd5Entries = null;
        VpkOtherMd5Section otherMd5Section = null;
        VpkSignatureSection signatureSection = null;
        if (headerV2 != null) {
            // skip file data section
            ch.position(Math.toIntExact(VpkHeader.SIZE + VpkHeader2.SIZE + headerV1.getTreeSize() + headerV2.getFileDataSectionSize()));

            int archiveMd5SectionSize = headerV2.getArchiveMD5SectionSizeInt();
            var archiveMd5SectionBuf = IoUtil.sliceAdvance(ch, archiveMd5SectionSize);
            int count = Integer.divideUnsigned(archiveMd5SectionSize, VpkArchiveMd5Entry.SIZE);
            archiveMd5Entries = new ArrayList<>(count);
            for (int i = 0; Integer.compareUnsigned(i, count) < 0; i++) {
                archiveMd5Entries.add(VpkArchiveMd5Entry.load(archiveMd5SectionBuf));
            }

            otherMd5Section = VpkOtherMd5Section.load(IoUtil.sliceAdvance(ch, VpkOtherMd5Section.SIZE));

            if (headerV2.getSignatureSectionSize() != 0) {
                signatureSection = VpkSignatureSection.load(IoUtil.sliceAdvance(ch, Math.toIntExact(ch.size() - ch.position())));
            }
        }

        VpkFile vpkFile = new VpkFile(path, headerV1, headerV2, archiveMd5Entries != null ? List.copyOf(archiveMd5Entries) : null, otherMd5Section, signatureSection);
        vpkFile.readTree(treeBuf);
        return vpkFile;
    }

    private void readTree(ByteBuffer bb) throws IOException {
        for (String extension; !(extension = IoUtil.readString(bb)).isEmpty(); ) {
            for (String path; !(path = IoUtil.readString(bb)).isEmpty(); ) {
                VpkDirEntry dirEntry = " ".equals(path) ? rootEntry : rootEntry.resolveOrCreateDirs(path);
                for (String name; !(name = IoUtil.readString(bb)).isEmpty(); ) {
                    String fullName = name + (" ".equals(extension) ? "" : "." + extension);
                    dirEntry.addChild(VpkFileEntry.load(dirEntry, fullName, bb));
                }
            }
        }
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

    public VpkFileEntry resolveFile(String path) throws IOException {
        return rootEntry.resolveFile(path);
    }

    public VpkDirEntry resolveDir(String path) throws IOException {
        return rootEntry.resolveDir(path);
    }

    SeekableByteChannel loadContent(VpkFileEntry file) throws IOException {
        var length = file.getEntryLength();
        if (length <= 0) {
            return null;
        }

        var entry = file.getEntry();
        if (entry != null) {
            return new ByteBufferChannel(entry);
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

        return new WrappedFileChannel(FileChannel.open(path, StandardOpenOption.READ), offset, length);
    }
}
