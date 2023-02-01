package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class VpkDirEntry extends VpkEntry {

    private final Map<String, VpkEntry> children;

    VpkDirEntry(VpkFile file, VpkDirEntry parent, String name) {
        super(file, parent, name);
        this.children = new HashMap<>();
    }

    void addChild(VpkEntry child) {
        children.put(child.getName(), child);
    }

    Collection<VpkEntry> getDirectChildren() {
        return children.values();
    }

    public VpkEntry resolve(CharSequence name) throws IOException {
        return resolveOrCreate(name, Mode.ANY);
    }

    public VpkFileEntry resolveFile(CharSequence name) throws IOException {
        return (VpkFileEntry) resolveOrCreate(name, Mode.FILE);
    }

    public VpkDirEntry resolveDir(CharSequence name) throws IOException {
        return (VpkDirEntry) resolveOrCreate(name, Mode.DIR);
    }

    VpkDirEntry resolveOrCreateDirs(CharSequence name) throws IOException {
        return (VpkDirEntry) resolveOrCreate(name, Mode.CREATE_DIRS);
    }

    private VpkEntry resolveOrCreate(CharSequence name, Mode mode) throws IOException {
        if (name.isEmpty() || ".".contentEquals(name)) {
            return this;
        } else if ("..".contentEquals(name)) {
            var parent = getParent();
            if (parent == null) {
                throw new NoSuchFileException("root dir does not have a parent");
            }

            return parent;
        }

        int idx = -1;
        for (int i = 0, len = name.length(); i < len; i++) {
            if (name.charAt(i) == '/') {
                idx = i;
                break;
            }
        }

        if (idx < 0) {
            String nameStr = name.toString();
            VpkEntry child = children.get(nameStr);
            if (child == null) {
                switch (mode) {
                    case CREATE_DIR, CREATE_DIRS -> {
                        child = new VpkDirEntry(getFile(), this, nameStr);
                        addChild(child);
                    }
                    case CREATE_FILE, CREATE_FILE_AND_PARENT_DIRS -> throw new UnsupportedOperationException();
                    default -> throw new NoSuchFileException("entry '" + nameStr + "' not found");
                }
            }

            if (child.isDirectory() && mode != Mode.ANY && mode != Mode.DIR && mode != Mode.CREATE_DIR && mode != Mode.CREATE_DIRS) {
                throw new VpkException("cannot create or resolve file because directory of the same name already exists");
            } else if (child.isRegularFile() && mode != Mode.ANY && mode != Mode.FILE && mode != Mode.CREATE_FILE && mode != Mode.CREATE_FILE_AND_PARENT_DIRS) {
                throw new VpkException("cannot create or resolve directory because file of the same name already exists");
            }

            return child;
        } else {
            CharSequence firstName = name.subSequence(0, idx);
            VpkDirEntry parent = (VpkDirEntry) resolveOrCreate(firstName, switch (mode) {
                case CREATE_DIRS, CREATE_FILE_AND_PARENT_DIRS -> Mode.CREATE_DIRS;
                default -> Mode.DIR;
            });
            return parent.resolveOrCreate(name.subSequence(idx + 1, name.length()), mode);
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
