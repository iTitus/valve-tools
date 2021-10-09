package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class VpkDirEntry extends VpkEntry {

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

    public List<VpkFileEntry> getAllFiles() {
        List<VpkFileEntry> files = new ArrayList<>();

        Deque<VpkDirEntry> toVisit = new ArrayDeque<>();
        toVisit.addFirst(this);
        while (!toVisit.isEmpty()) {
            VpkDirEntry dir = toVisit.removeFirst();
            for (VpkEntry child : dir.children.values()) {
                if (child instanceof VpkDirEntry) {
                    toVisit.addFirst((VpkDirEntry) child);
                } else {
                    files.add((VpkFileEntry) child);
                }
            }
        }

        return files;
    }

    public List<VpkDirEntry> getAllDirectories() {
        List<VpkDirEntry> dirs = new ArrayList<>();

        Deque<VpkDirEntry> toVisit = new ArrayDeque<>();
        toVisit.addFirst(this);
        while (!toVisit.isEmpty()) {
            VpkDirEntry dir = toVisit.removeFirst();
            for (VpkEntry child : dir.children.values()) {
                if (child instanceof VpkDirEntry) {
                    toVisit.addFirst((VpkDirEntry) child);
                    dirs.add((VpkDirEntry) child);
                }
            }
        }

        return dirs;
    }

    public List<VpkEntry> getAllChildren() {
        List<VpkEntry> children = new ArrayList<>();

        Deque<VpkDirEntry> toVisit = new ArrayDeque<>();
        toVisit.addFirst(this);
        while (!toVisit.isEmpty()) {
            VpkDirEntry dir = toVisit.removeFirst();
            for (VpkEntry child : dir.children.values()) {
                children.add(child);
                if (child instanceof VpkDirEntry) {
                    toVisit.addFirst((VpkDirEntry) child);
                }
            }
        }

        return children;
    }

    public VpkEntry resolve(String name) throws IOException {
        return resolveAndCreateDirs(name, false);
    }

    VpkEntry resolveAndCreateDirs(String name) throws IOException {
        return resolveAndCreateDirs(name, true);
    }

    private VpkEntry resolveAndCreateDirs(String name, boolean create) throws IOException {
        if (name.isEmpty() || ".".equals(name)) {
            return this;
        } else if ("..".equals(name)) {
            return getParent();
        }

        int i = name.indexOf('/');
        if (i < 0) {
            VpkEntry child = children.get(name);
            if (child == null) {
                if (!create) {
                    throw new NoSuchFileException("entry not found");
                }

                child = new VpkDirEntry(this, name);
                addChild(child);
            }

            return child;
        } else {
            String parentPath = name.substring(0, i);
            VpkEntry parent = resolveAndCreateDirs(parentPath, create);
            if (parent instanceof VpkFileEntry) {
                throw new VpkException("cannot create or read directory because file of the same name already exists");
            }

            return ((VpkDirEntry) parent).resolveAndCreateDirs(name.substring(i + 1), create);
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

    @Override
    public String toString() {
        return getPath();
    }
}
