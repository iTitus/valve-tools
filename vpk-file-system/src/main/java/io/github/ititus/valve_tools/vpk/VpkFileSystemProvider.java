package io.github.ititus.valve_tools.vpk;

import io.github.ititus.commons.io.PathUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VpkFileSystemProvider extends FileSystemProvider {

    private final Map<Path, VpkFileSystem> filesystems = new HashMap<>();

    private Path uriToPath(URI uri) {
        if (!getScheme().equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("URI scheme is not '" + getScheme() + "'");
        }

        try {
            // only support syntax like the legacy JAR URL syntax: vpk:{uri}!/{entry}
            String spec = uri.getRawSchemeSpecificPart();
            int sep = spec.indexOf("!/");
            if (sep != -1) {
                spec = spec.substring(0, sep);
            }
            return Paths.get(new URI(spec)).toAbsolutePath();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private boolean ensureFileOrNotExist(Path path) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            if (!attrs.isRegularFile()) {
                throw new UnsupportedOperationException();
            }

            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private VpkFileSystem createVpkFileSystem(Path path, Map<String, ?> env) throws IOException {
        try {
            return new VpkFileSystem(this, path, env);
        } catch (VpkException e) {
            if (PathUtil.getExtension(path).map(ext -> getScheme().equals(ext)).orElse(false)) {
                throw e;
            }

            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String getScheme() {
        return "vpk";
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        return newFileSystem(uriToPath(uri), env);
    }

    @Override
    public FileSystem newFileSystem(Path path, Map<String, ?> env) throws IOException {
        synchronized (filesystems) {
            path = path.toRealPath();
            if (ensureFileOrNotExist(path)) {
                if (filesystems.containsKey(path)) {
                    throw new FileSystemAlreadyExistsException();
                }
            }

            VpkFileSystem vpkfs = createVpkFileSystem(path, env);
            filesystems.put(path, vpkfs);

            return vpkfs;
        }
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        synchronized (filesystems) {
            try {
                return filesystems.get(uriToPath(uri).toRealPath());
            } catch (IOException ignored) {
                throw new FileSystemNotFoundException();
            }
        }
    }

    @Override
    public Path getPath(URI uri) {
        String spec = uri.getSchemeSpecificPart();
        int sep = spec.indexOf("!/");
        if (sep == -1) {
            throw new IllegalArgumentException("URI: "
                    + uri
                    + " does not contain path info ex. vpk:file:/c:/foo.vpk!/BAR");
        }

        return getFileSystem(uri).getPath(spec.substring(sep + 1));
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        if (!(path instanceof VpkPath p)) {
            throw new ProviderMismatchException();
        }

        return p.getFileSystem().newByteChannel(p, options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        if (!(dir instanceof VpkPath p)) {
            throw new ProviderMismatchException();
        }

        return new VpkDirectoryStream(p, filter);
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void delete(Path path) {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public boolean isSameFile(Path path, Path path2) {
        return path.equals(path2);
    }

    @Override
    public boolean isHidden(Path path) {
        return false;
    }

    @Override
    public FileStore getFileStore(Path path) {
        if (!(path instanceof VpkPath p)) {
            throw new ProviderMismatchException();
        }

        return p.getFileSystem().getFileStore();
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        if (!(path instanceof VpkPath p)) {
            throw new ProviderMismatchException();
        }

        boolean w = false;
        boolean x = false;
        for (AccessMode mode : modes) {
            switch (mode) {
                case READ:
                    break;
                case WRITE:
                    w = true;
                    break;
                case EXECUTE:
                    x = true;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        p.getFileSystem().exists(p);
        if ((w && path.getFileSystem().isReadOnly()) || x) {
            throw new AccessDeniedException(toString());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        if (!(path instanceof VpkPath p)) {
            throw new ProviderMismatchException();
        } else if (type == BasicFileAttributeView.class) {
            return (V) p.getFileSystem().getBasicFileAttributeView(p);
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        if (!(path instanceof VpkPath p)) {
            throw new ProviderMismatchException();
        } else if (type == BasicFileAttributes.class) {
            return (A) p.getFileSystem().readBasicAttributes(p);
        }

        throw new UnsupportedOperationException("Attributes of type " + type.getName() + " not supported");
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        if (!(path instanceof VpkPath p)) {
            throw new ProviderMismatchException();
        }

        String view;
        String attrs;
        int colonPos = attributes.indexOf(':');
        if (colonPos == -1) {
            view = "basic";
            attrs = attributes;
        } else {
            view = attributes.substring(0, colonPos);
            attrs = attributes.substring(colonPos + 1);
        }

        if (!"basic".equals(view)) {
            throw new UnsupportedOperationException("Attribute view " + view + " is not supported");
        }

        return p.getFileSystem().readBasicAttributesAsMap(p, attrs);
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) {
        throw new ReadOnlyFileSystemException();
    }

    void removeFileSystem(Path path, VpkFileSystem fs) throws IOException {
        synchronized (filesystems) {
            filesystems.remove(path.toRealPath(), fs);
        }
    }
}
