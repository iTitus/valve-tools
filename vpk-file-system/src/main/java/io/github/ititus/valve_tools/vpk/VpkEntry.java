package io.github.ititus.valve_tools.vpk;

import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public abstract sealed class VpkEntry implements BasicFileAttributes, BasicFileAttributeView permits VpkDirEntry, VpkFileEntry {

    private final VpkFile file;
    private final VpkDirEntry parent;
    private final String name;

    VpkEntry(VpkFile file, VpkDirEntry parent, String name) {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(name, "name");
        if (name.indexOf('\u0000') >= 0 || name.indexOf('/') >= 0) {
            throw new IllegalArgumentException();
        } else if (name.isEmpty() && parent != null) {
            throw new IllegalArgumentException();
        }

        this.file = file;
        this.parent = parent;
        this.name = name;
    }

    public VpkFile getFile() {
        return file;
    }

    public VpkDirEntry getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        String path;
        if (parent == null) {
            path = getName();
        } else {
            path = parent.getPath() + '/' + getName();
        }

        return path;
    }

    @Override
    public FileTime lastModifiedTime() {
        return FileTime.from(Instant.EPOCH);
    }

    @Override
    public FileTime lastAccessTime() {
        return FileTime.from(Instant.EPOCH);
    }

    @Override
    public FileTime creationTime() {
        return FileTime.from(Instant.EPOCH);
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    @Override
    public boolean isOther() {
        return false;
    }

    @Override
    public Object fileKey() {
        return null;
    }

    @Override
    public String name() {
        return "basic";
    }

    @Override
    public BasicFileAttributes readAttributes() {
        return this;
    }

    @Override
    public void setTimes(FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime) {
        throw new UnsupportedOperationException();
    }

    public Map<String, Object> readAttributes(String attrs) {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return getPath();
    }
}
