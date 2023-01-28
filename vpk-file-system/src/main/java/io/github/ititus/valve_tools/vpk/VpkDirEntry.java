package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class VpkDirEntry extends VpkEntry {

    private final Map<String, VpkEntry> children;

    VpkDirEntry(VpkDirEntry parent, String name) {
        super(parent, name);
        this.children = new HashMap<>();
    }

    void addChild(VpkEntry child) {
        children.put(child.getName(), child);
    }

    Collection<VpkEntry> getDirectChildren() {
        return children.values();
    }

    public VpkEntry resolve(String name) throws IOException {
        return resolveOrCreate(name, Mode.ANY);
    }

    public VpkEntry resolveFile(String name) throws IOException {
        return resolveOrCreate(name, Mode.FILE);
    }

    public VpkEntry resolveDir(String name) throws IOException {
        return resolveOrCreate(name, Mode.DIR);
    }

    VpkDirEntry resolveOrCreateDirs(String name) throws IOException {
        return (VpkDirEntry) resolveOrCreate(name, Mode.CREATE_DIRS);
    }

    private VpkEntry resolveOrCreate(String name, Mode mode) throws IOException {
        if (name.isEmpty() || ".".equals(name)) {
            return this;
        } else if ("..".equals(name)) {
            if (getParent() == null) {
                throw new NoSuchFileException("root dir does not have a parent");
            }

            return getParent();
        }

        int i = name.indexOf('/');
        if (i < 0) {
            VpkEntry child = children.get(name);
            if (child == null) {
                switch (mode) {
                    case CREATE_DIR, CREATE_DIRS -> {
                        child = new VpkDirEntry(this, name);
                        addChild(child);
                    }
                    case CREATE_FILE, CREATE_FILE_AND_PARENT_DIRS -> throw new UnsupportedOperationException();
                    default -> throw new NoSuchFileException("entry '" + name + "' not found");
                }
            }

            if (child.isDirectory() && mode != Mode.ANY && mode != Mode.DIR && mode != Mode.CREATE_DIR && mode != Mode.CREATE_DIRS) {
                throw new VpkException("cannot create or resolve file because directory of the same name already exists");
            } else if (child.isRegularFile() && mode != Mode.ANY && mode != Mode.FILE && mode != Mode.CREATE_FILE && mode != Mode.CREATE_FILE_AND_PARENT_DIRS) {
                throw new VpkException("cannot create or resolve directory because file of the same name already exists");
            }

            return child;
        } else {
            String firstName = name.substring(0, i);
            VpkDirEntry parent = (VpkDirEntry) resolveOrCreate(firstName, switch (mode) {
                case CREATE_DIRS, CREATE_FILE_AND_PARENT_DIRS -> Mode.CREATE_DIRS;
                default -> Mode.DIR;
            });
            return parent.resolveOrCreate(name.substring(i + 1), mode);
        }
    }

    @Override
    public boolean isRegularFile() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public long size() {
        return children.values().stream()
                .mapToLong(BasicFileAttributes::size)
                .sum();
    }

    private enum Mode {
        ANY, DIR, FILE, CREATE_DIR, CREATE_DIRS, CREATE_FILE, CREATE_FILE_AND_PARENT_DIRS
    }
}
