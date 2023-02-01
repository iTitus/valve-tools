package io.github.ititus.valve_tools.vpk.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

public final class WrappedFileChannel extends FileChannel {

    private final FileChannel fch;
    private final long offset;
    private final long size;

    public WrappedFileChannel(FileChannel fch, long offset, long size) throws IOException {
        Objects.requireNonNull(fch, "fch");
        if (offset < 0 || size < 0 || fch.size() < size) {
            throw new IllegalArgumentException();
        }

        this.fch = fch;
        this.offset = offset;
        this.size = size;

        position(0);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int oldLimit = dst.limit();
        dst.limit(Math.toIntExact(Math.addExact(dst.position(), Math.min(dst.remaining(), Math.subtractExact(size, position())))));
        try {
            return fch.read(dst);
        } finally {
            dst.limit(oldLimit);
        }
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        int[] oldLimits = new int[length];
        long sizeRemaining = Math.subtractExact(size, position());
        for (int i = 0; i < length; i++) {
            ByteBuffer dst = dsts[offset + i];
            oldLimits[i] = dst.limit();

            int rem = dst.remaining();
            if (rem > sizeRemaining) {
                dst.limit(Math.toIntExact(Math.addExact(dst.position(), sizeRemaining)));
                sizeRemaining = 0;
            } else {
                sizeRemaining -= rem;
            }
        }

        try {
            return fch.read(dsts, offset, length);
        } finally {
            for (int i = 0; i < length; i++) {
                dsts[offset + i].limit(oldLimits[i]);
            }
        }
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        int oldLimit = src.limit();
        src.limit(Math.toIntExact(Math.addExact(src.position(), Math.min(src.remaining(), Math.subtractExact(size, position())))));
        try {
            return fch.write(src);
        } finally {
            src.limit(oldLimit);
        }
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        int[] oldLimits = new int[length];
        long sizeRemaining = Math.subtractExact(size, position());
        for (int i = 0; i < length; i++) {
            ByteBuffer src = srcs[offset + i];
            oldLimits[i] = src.limit();

            int rem = src.remaining();
            if (rem > sizeRemaining) {
                src.limit(Math.toIntExact(Math.addExact(src.position(), sizeRemaining)));
                sizeRemaining = 0;
            } else {
                sizeRemaining -= rem;
            }
        }

        try {
            return fch.write(srcs, offset, length);
        } finally {
            for (int i = 0; i < length; i++) {
                srcs[offset + i].limit(oldLimits[i]);
            }
        }
    }

    @Override
    public long position() throws IOException {
        return Math.subtractExact(fch.position(), offset);
    }

    @Override
    public FileChannel position(long newPosition) throws IOException {
        if (newPosition < 0) {
            throw new IllegalArgumentException();
        }

        return fch.position(Math.addExact(this.offset, newPosition));
    }

    @Override
    public long size() throws IOException {
        return size;
    }

    @Override
    public FileChannel truncate(long size) throws IOException {
        if (size >= this.size) {
            return this;
        }

        return fch.truncate(size);
    }

    @Override
    public void force(boolean metaData) throws IOException {
        fch.force(metaData);
    }

    @Override
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException();
        }

        return fch.transferTo(Math.addExact(this.offset, position), Math.min(this.size, count), target);
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException();
        }

        return fch.transferFrom(src, Math.addExact(this.offset, position), Math.min(this.size, count));
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException();
        }

        int oldLimit = dst.limit();
        dst.limit(Math.toIntExact(Math.addExact(dst.position(), Math.min(dst.remaining(), Math.subtractExact(size, position)))));
        try {
            return fch.read(dst, Math.addExact(this.offset, position));
        } finally {
            dst.limit(oldLimit);
        }
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException();
        }

        int oldLimit = src.limit();
        src.limit(Math.toIntExact(Math.addExact(src.position(), Math.min(src.remaining(), Math.subtractExact(size, position)))));
        try {
            return fch.write(src, Math.addExact(this.offset, position));
        } finally {
            src.limit(oldLimit);
        }
    }

    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException();
        }

        return fch.map(mode, Math.addExact(this.offset, position), Math.min(this.size, size));
    }

    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException();
        }

        return fch.lock(Math.addExact(this.offset, position), Math.min(this.size, size), shared);
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException();
        }

        return fch.tryLock(Math.addExact(this.offset, position), Math.min(this.size, size), shared);
    }

    @Override
    protected void implCloseChannel() throws IOException {
        fch.close();
    }
}
