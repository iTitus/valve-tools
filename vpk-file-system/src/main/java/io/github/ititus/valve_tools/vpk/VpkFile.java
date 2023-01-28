package io.github.ititus.valve_tools.vpk;

import io.github.ititus.commons.io.PathUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

public class VpkFile {

    static final int SIGNATURE = 0x55aa1234;
    private static final int MAP_THRESHOLD = 8192;

    private final Path path;
    private final VpkHeader headerV1;
    private final VpkHeader2 headerV2;
    private final VpkDirEntry rootEntry;

    private VpkFile(Path path, VpkHeader headerV1, VpkHeader2 headerV2, VpkDirEntry rootEntry) {
        this.path = path;
        this.headerV1 = headerV1;
        this.headerV2 = headerV2;
        this.rootEntry = rootEntry;
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
            public void read(ByteBuffer target, int size) throws IOException {
                if (target.hasArray()) {
                    if (is.readNBytes(target.array(), target.arrayOffset() + target.position(), size) != size) {
                        throw new EOFException();
                    }
                    target.position(target.position() + size);
                } else {
                    byte[] arr = new byte[size];
                    if (is.readNBytes(arr, 0, size) != size) {
                        throw new EOFException();
                    }
                    target.put(arr, 0, size);
                }
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
            public void read(ByteBuffer target, int size) throws IOException {
                if (target.hasArray()) {
                    di.readFully(target.array(), target.arrayOffset() + target.position(), size);
                    target.position(target.position() + size);
                } else {
                    byte[] arr = new byte[size];
                    di.readFully(arr, 0, size);
                    target.put(arr, 0, size);
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

        return new VpkFile(path, headerV1, headerV2, rootEntry);
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
                throw new VpkException("cannot resolve archive file if main file (" + this.path + ") is not named with suffix '_dir'");
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
