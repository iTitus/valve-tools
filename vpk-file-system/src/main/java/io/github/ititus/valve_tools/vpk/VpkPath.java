package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;

import static java.nio.charset.StandardCharsets.UTF_8;

class VpkPath implements Path {

    private final VpkFileSystem fs;
    private final String path;

    VpkPath(VpkFileSystem fs, String path) {
        this.fs = fs;
        this.path = path;
    }

    private static int decode(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        } else if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }

        assert false;
        return -1;
    }

    // to avoid double escape
    private static String decodeUri(String s) {
        if (s == null) {
            return null;
        }

        int n = s.length();
        if (n == 0) {
            return s;
        } else if (s.indexOf('%') < 0) {
            return s;
        }

        StringBuilder sb = new StringBuilder(n);
        byte[] bb = new byte[n];
        boolean betweenBrackets = false;

        for (int i = 0; i < n; ) {
            char c = s.charAt(i);
            if (c == '[') {
                betweenBrackets = true;
            } else if (betweenBrackets && c == ']') {
                betweenBrackets = false;
            }

            if (c != '%' || betweenBrackets) {
                sb.append(c);
                i++;
                continue;
            }

            int nb = 0;
            while (c == '%') {
                assert (n - i >= 2);
                bb[nb++] = (byte) (((decode(s.charAt(++i)) & 0xf) << 4) | (decode(s.charAt(++i)) & 0xf));
                if (++i >= n) {
                    break;
                }
                c = s.charAt(i);
            }

            sb.append(new String(bb, 0, nb, UTF_8));
        }
        return sb.toString();
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
    public VpkPath getRoot() {
        return isAbsolute() ? fs.getRootDir() : null;
    }

    @Override
    public VpkPath getFileName() {
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
    public VpkPath getParent() {
        if (path.isEmpty() || "/".equals(path)) {
            return null;
        }

        int i = path.lastIndexOf('/');
        if (i <= 0) {
            return getRoot();
        }

        return new VpkPath(fs, path.substring(0, i));
    }

    @Override
    public int getNameCount() {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public VpkPath getName(int index) {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public VpkPath subpath(int beginIndex, int endIndex) {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startsWith(Path other) {
        if (!(other instanceof VpkPath)) {
            throw new ProviderMismatchException();
        }
        VpkPath o = (VpkPath) other;

        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean endsWith(Path other) {
        if (!(other instanceof VpkPath)) {
            throw new ProviderMismatchException();
        }
        VpkPath o = (VpkPath) other;

        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public VpkPath normalize() {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public VpkPath resolve(Path other) {
        if (!(other instanceof VpkPath)) {
            throw new ProviderMismatchException();
        }
        VpkPath o = (VpkPath) other;

        if (o.path.isEmpty()) {
            return this;
        } else if (o.isAbsolute() || path.isEmpty()) {
            return o;
        }

        String resolved = path;
        if (!resolved.endsWith("/")) {
            resolved += "/";
        }
        resolved += o.path;

        return new VpkPath(fs, resolved);
    }

    @Override
    public VpkPath relativize(Path other) {
        if (!(other instanceof VpkPath)) {
            throw new ProviderMismatchException();
        }
        VpkPath o = (VpkPath) other;


        if (o.equals(this)) {
            return new VpkPath(fs, "");
        } else if (path.isEmpty()) {
            return o;
        } else if (fs != o.fs || isAbsolute() != o.isAbsolute()) {
            throw new IllegalArgumentException();
        } else if ("/".equals(path)) {
            return new VpkPath(fs, o.path.substring(1));
        }

        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public URI toUri() {
        try {
            return new URI(
                    "vpk",
                    decodeUri(fs.getVpkPath().toUri().toString()) + "!" + toAbsolutePath().path,
                    null
            );
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public VpkPath toAbsolutePath() {
        if (isAbsolute()) {
            return this;
        }

        return new VpkPath(fs, "/" + path);
    }

    @Override
    public VpkPath toRealPath(LinkOption... options) throws IOException {
        VpkPath realPath = toAbsolutePath().normalize();
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
