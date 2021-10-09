package io.github.ititus.valve_tools.vpk;

import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

class VpkFileStore extends FileStore {

    private final VpkFileSystem vpkfs;

    VpkFileStore(VpkFileSystem vpkfs) {
        this.vpkfs = vpkfs;
    }

    @Override
    public String name() {
        return vpkfs.toString() + "/";
    }

    @Override
    public String type() {
        return "vpkfs";
    }

    @Override
    public boolean isReadOnly() {
        return vpkfs.isReadOnly();
    }

    @Override
    public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
        return false;
    }

    @Override
    public boolean supportsFileAttributeView(String name) {
        return false;
    }

    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
        if (type == null) {
            throw new NullPointerException();
        }

        return null;
    }

    @Override
    public long getTotalSpace() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getUsableSpace() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getUnallocatedSpace() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(String attribute) {
        throw new UnsupportedOperationException("does not support the given attribute");
    }
}
