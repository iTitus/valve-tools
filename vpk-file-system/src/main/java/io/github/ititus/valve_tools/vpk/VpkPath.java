package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

final class VpkPath implements Path {

    private final VpkFileSystem fs;
    private final String path;
    private volatile int[] offsets;

    VpkPath(VpkFileSystem fs, String path) {
        this.fs = fs;
        this.path = normalize(path);
    }

    private static String normalize(String path) {
        Objects.requireNonNull(path, "path");
        if (path.isEmpty() || "/".equals(path)) {
            return path;
        }

        int nullIndex = path.indexOf('\u0000');
        if (nullIndex >= 0) {
            throw new InvalidPathException(path, "null character not allowed", nullIndex);
        }

        path = path.replace("//", "/");
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
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

    private synchronized void initOffsets() {
        if (offsets != null) {
            return;
        }

        if (path.isEmpty()) {
            offsets = new int[] {0};
        } else if ("/".equals(path)) {
            offsets = new int[0];
        } else {
            List<Integer> offsetsList = new ArrayList<>();
            int i = isAbsolute() ? 1 : 0;
            while (true) {
                offsetsList.add(i++);
                int nextSlash = path.indexOf('/', i);
                if (nextSlash < 0) {
                    break;
                }

                i = nextSlash + 1;
            }

            offsets = offsetsList.stream().mapToInt(n -> n).toArray();
        }
    }

    private int getStartFromOffset(int i) {
        initOffsets();
        if (i < 0 || i >= offsets.length) {
            throw new IllegalArgumentException();
        }

        return offsets[i];
    }

    private int getEndFromOffset(int i) {
        initOffsets();
        if (i < 0 || i >= offsets.length) {
            throw new IllegalArgumentException();
        } else if (i == offsets.length - 1) {
            return path.length();
        }

        return offsets[i + 1];
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
        initOffsets();
        return offsets.length;
    }

    @Override
    public VpkPath getName(int index) {
        if (index < 0 || index >= getNameCount()) {
            throw new IllegalArgumentException("index: " + index + ", name count: " + getNameCount());
        }

        return new VpkPath(fs, path.substring(getStartFromOffset(index), getEndFromOffset(index)));
    }

    @Override
    public VpkPath subpath(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > getNameCount() || beginIndex >= endIndex) {
            throw new IllegalArgumentException("beginIndex: " + beginIndex + ", endIndex: " + endIndex + ", name count: " + getNameCount());
        }

        return new VpkPath(fs, path.substring(getStartFromOffset(beginIndex), getEndFromOffset(endIndex - 1)));
    }

    @Override
    public boolean startsWith(Path other) {
        if (!(Objects.requireNonNull(other, "other") instanceof VpkPath o)) {
            return false;
        } else if (isAbsolute() != o.isAbsolute()) {
            return false;
        }

        int nameCount1 = getNameCount();
        int nameCount2 = o.getNameCount();
        if (nameCount1 < nameCount2) {
            return false;
        }

        for (int i = 0; i < nameCount2; i++) {
            if (!getName(i).path.equals(o.getName(i).path)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean endsWith(Path other) {
        if (!(Objects.requireNonNull(other, "other") instanceof VpkPath o)) {
            return false;
        } else if (!isAbsolute() && o.isAbsolute()) {
            return false;
        }

        int nameCount1 = getNameCount();
        int nameCount2 = o.getNameCount();
        if (nameCount1 < nameCount2) {
            return false;
        } else if (o.isAbsolute() && nameCount1 > nameCount2) {
            return false;
        }

        for (int i = nameCount2 - 1; i >= 0; i--) {
            if (!getName(i).path.equals(o.getName(i).path)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public VpkPath normalize() {
        if (path.isEmpty() || "/".equals(path)) {
            return this;
        }

        List<String> names = IntStream.range(0, getNameCount())
                .mapToObj(i -> getName(i).path)
                .filter(name -> !".".equals(name))
                .collect(Collectors.toList());
        int i = names.size() - 1;
        while (i >= 0) {
            if (i > 0 && "..".equals(names.get(i)) && !"..".equals(names.get(i - 1))) {
                names.remove(i);
                names.remove(i - 1);
                i = names.size() - 1;
            } else {
                i--;
            }
        }

        return new VpkPath(fs, (isAbsolute() ? "/" : "") + String.join("/", names));
    }

    @Override
    public VpkPath resolve(Path other) {
        if (!(Objects.requireNonNull(other, "other") instanceof VpkPath o)) {
            throw new ProviderMismatchException();
        }

        if (o.path.isEmpty()) {
            return this;
        } else if (o.isAbsolute() || path.isEmpty()) {
            return o;
        }

        return new VpkPath(fs, path + '/' + o.path);
    }

    @Override
    public VpkPath relativize(Path other) {
        if (!(Objects.requireNonNull(other, "other") instanceof VpkPath o)) {
            throw new ProviderMismatchException();
        } else if (o.equals(this)) {
            return new VpkPath(fs, "");
        } else if (path.isEmpty()) {
            return o;
        } else if (fs != o.fs || isAbsolute() != o.isAbsolute()) {
            throw new IllegalArgumentException("incorrect filesystem or path: " + o);
        } else if ("/".equals(path)) {
            return new VpkPath(fs, o.path.substring(1)); // o must be absolute at this point because this is absolute
        }

        int nameCount1 = getNameCount();
        int nameCount2 = o.getNameCount();
        int nameCountMin = Math.min(nameCount1, nameCount2);
        int commonPrefixLength = 0;
        while (commonPrefixLength < nameCountMin) {
            if (!getName(commonPrefixLength).equals(o.getName(commonPrefixLength))) {
                break;
            }

            commonPrefixLength++;
        }

        return new VpkPath(fs,
                Stream.concat(
                        IntStream.range(0, nameCount1 - commonPrefixLength).mapToObj(i -> ".."),
                        IntStream.range(commonPrefixLength, nameCount2).mapToObj(i -> o.getName(i).path)
                ).collect(Collectors.joining("/"))
        );
    }

    @Override
    public URI toUri() {
        try {
            return new URI(
                    fs.provider().getScheme(),
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

        return fs.getRootDir().resolve(this);
    }

    @Override
    public VpkPath toRealPath(LinkOption... options) throws IOException {
        VpkPath realPath = toAbsolutePath().normalize();
        fs.provider().checkAccess(realPath);
        return realPath;
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) {
        Objects.requireNonNull(watcher, "watcher");
        Objects.requireNonNull(events, "events");
        Objects.requireNonNull(modifiers, "modifiers");
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Path other) {
        if (!(Objects.requireNonNull(other, "other") instanceof VpkPath o)) {
            throw new ProviderMismatchException();
        }

        return this.path.compareTo(o.path);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof VpkPath o && this.fs == o.fs && compareTo(o) == 0);
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
