package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.Map;
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

    boolean exists(VpkPath path) {
        try {
            readBasicAttributes(path);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    BasicFileAttributes readBasicAttributes(VpkPath path) throws IOException {
        return getBasicFileAttributeView(path).readAttributes();
    }

    BasicFileAttributeView getBasicFileAttributeView(VpkPath path) {
        try {
            return vpkFile.resolve(path.getPath());
        } catch (IOException ignored) {
            return null;
        }
    }

    Map<String, Object> readBasicAttributesAsMap(VpkPath path, String attrs) throws IOException {
        return vpkFile.resolve(path.getPath()).readAttributes(attrs);
    }

    SeekableByteChannel newByteChannel(VpkPath path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return vpkFile.resolveFile(path.getPath()).newByteChannel(options, attrs);
    }

    @Override
    public String toString() {
        return vpkPath.toString();
    }
}
