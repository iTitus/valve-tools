package io.github.ititus.valve_tools.vpk;

import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Map;

public abstract class VpkEntry implements BasicFileAttributes, BasicFileAttributeView {

    private final VpkDirEntry parent;
    private final String name;

    VpkEntry(VpkDirEntry parent, String name) {
        this.parent = parent;
        this.name = name;
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
            path = parent.getPath() + getName();
        }

        if (isDirectory()) {
            path += "/";
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
}
