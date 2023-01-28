package io.github.ititus.valve_tools.vpk;

import io.github.ititus.valve_tools.vpk.internal.ByteBufferChannel;
import io.github.ititus.valve_tools.vpk.internal.EmptyChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

class VpkFileSystem extends FileSystem {

    private final VpkFileSystemProvider provider;
    private final Path vpkPath;
    private final VpkPath rootDir;
    private final FileStore fileStore;
    private final VpkFile vpkFile;

    private boolean isOpen = true;

    VpkFileSystem(VpkFileSystemProvider provider, Path vpkPath, Map<String, ?> env) throws IOException {
        this.provider = provider;
        this.vpkPath = vpkPath;
        this.rootDir = new VpkPath(this, "/");
        this.fileStore = new VpkFileStore(this);
        this.vpkFile = VpkFile.load(vpkPath);

        // TODO: implement
        if (!env.isEmpty()) {
            throw new UnsupportedOperationException(env.toString());
        }
    }

    public Path getVpkPath() {
        return vpkPath;
    }

    public VpkFile getVpkFile() {
        return vpkFile;
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public void close() throws IOException {
        if (!isOpen) {
            return;
        }

        isOpen = false;
        provider.removeFileSystem(vpkPath, this);
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return List.of(rootDir);
    }

    VpkPath getRootDir() {
        return rootDir;
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return List.of(fileStore);
    }

    FileStore getFileStore() {
        return fileStore;
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return Set.of();
    }

    @Override
    public Path getPath(String first, String... more) {
        if (more.length == 0) {
            return new VpkPath(this, first);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(first);
        for (String path : more) {
            if (path.length() > 0) {
                if (sb.length() > 0) {
                    sb.append(getSeparator());
                }

                sb.append(path);
            }
        }

        return new VpkPath(this, sb.toString());
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchService newWatchService() {
        throw new UnsupportedOperationException();
    }

    public boolean exists(VpkPath path) {
        try {
            readAttributes(path);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    public BasicFileAttributes readAttributes(VpkPath path) throws IOException {
        return vpkFile.resolve(path.getPath());
    }

    public BasicFileAttributeView getFileAttributeView(VpkPath path) {
        try {
            return vpkFile.resolve(path.getPath());
        } catch (IOException ignored) {
            return null;
        }
    }

    public SeekableByteChannel newByteChannel(VpkPath path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
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

        if (!Files.isRegularFile(path)) {
            throw new NoSuchFileException(path.getPath());
        }

        VpkFileEntry file = (VpkFileEntry) vpkFile.resolveFile(path.getPath());

        var preload = file.getPreload();
        var content = vpkFile.loadContent(file);
        if (content == null && preload == null) {
            return new EmptyChannel();
        } else if (content == null) {
            return new ByteBufferChannel(preload);
        } else if (preload == null) {
            return new ByteBufferChannel(content);
        }

        var allContent = ByteBuffer.allocateDirect(Math.toIntExact(file.size()));
        allContent.put(preload);
        allContent.put(content);
        allContent.rewind();
        return new ByteBufferChannel(allContent);
    }

    @Override
    public String toString() {
        return vpkPath.toString();
    }
}
