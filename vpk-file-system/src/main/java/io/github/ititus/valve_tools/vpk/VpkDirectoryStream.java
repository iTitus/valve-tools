package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

class VpkDirectoryStream implements DirectoryStream<Path> {

    private final VpkPath dir;
    private final DirectoryStream.Filter<? super Path> filter;

    private volatile boolean isClosed;
    private volatile boolean itr;

    VpkDirectoryStream(VpkPath dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        this.dir = dir;
        this.filter = filter;
        if (!Files.isDirectory(dir)) {
            throw new NotDirectoryException(dir.toString());
        }
    }

    @Override
    public synchronized Iterator<Path> iterator() {
        if (isClosed) {
            throw new ClosedDirectoryStreamException();
        } else if (itr) {
            throw new IllegalStateException("Iterator has already been returned");
        }

        Iterator<? extends Path> iterator;
        try {
            VpkDirEntry dirEntry = (VpkDirEntry) dir.getFileSystem().getVpkFile().resolve(dir.getPath());
            iterator = dirEntry.getDirectChildren().stream()
                    .map(e -> new VpkPath(dir.getFileSystem(), e.getPath()))
                    .filter(e -> {
                        try {
                            return filter == null || filter.accept(e);
                        } catch (IOException ex) {
                            throw new DirectoryIteratorException(ex);
                        }
                    })
                    .iterator();
        } catch (IOException e) {
            throw new DirectoryIteratorException(e);
        }

        itr = true;
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                if (isClosed) {
                    return false;
                }

                return iterator.hasNext();
            }

            @Override
            public synchronized Path next() {
                if (isClosed) {
                    throw new NoSuchElementException();
                }

                return iterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public synchronized void close() {
        isClosed = true;
    }
}
