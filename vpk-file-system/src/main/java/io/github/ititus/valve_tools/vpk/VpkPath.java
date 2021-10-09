package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;

class VpkPath implements Path {

    private final VpkFileSystem fs;
    private final String path;

    VpkPath(VpkFileSystem fs, String path) {
        this.fs = fs;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public VpkFileSystem getFileSystem() {
        return fs;
    }

    @Override
    public boolean isAbsolute() {
        return path.length() > 0 && path.charAt(0) == '/';
    }

    @Override
    public Path getRoot() {
        return isAbsolute() ? fs.getRootDir() : null;
    }

    @Override
    public Path getFileName() {
        if (path.isEmpty() || "/".equals(path)) {
            return null;
        }

        int i = path.lastIndexOf('/');
        if (i < 0) {
            return this;
        }

        return new VpkPath(fs, path.substring(i + 1));
    }

    @Override
    public Path getParent() {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNameCount() {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public Path getName(int index) {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startsWith(Path other) {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean endsWith(Path other) {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public Path normalize() {
        // TODO: implement
        return this;
    }

    @Override
    public Path resolve(Path other) {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public Path relativize(Path other) {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public URI toUri() {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public Path toAbsolutePath() {
        if (isAbsolute()) {
            return this;
        }

        return new VpkPath(fs, "/" + path);
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        Path realPath = toAbsolutePath().normalize();
        fs.provider().checkAccess(realPath);
        return realPath;
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Path other) {
        if (!(other instanceof VpkPath)) {
            throw new ProviderMismatchException();
        }

        return this.path.compareTo(((VpkPath) other).path);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VpkPath
                && this.fs == ((VpkPath) obj).fs
                && compareTo((Path) obj) == 0;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return path;
    }
}
