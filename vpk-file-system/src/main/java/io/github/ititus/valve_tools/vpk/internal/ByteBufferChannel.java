package io.github.ititus.valve_tools.vpk.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ByteBufferChannel implements SeekableByteChannel {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private ByteBuffer bb;
    private boolean closed;

    public ByteBufferChannel(ByteBuffer bb) {
        this.bb = bb;
    }

    private void ensureOpen() throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }
    }

    private void ensureWritable() {
        if (bb.isReadOnly()) {
            throw new NonWritableChannelException();
        }
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        lock.writeLock().lock();
        try {
            ensureOpen();
            int rem = bb.remaining();
            if (rem <= 0) {
                return -1;
            }

            int readable = Math.min(dst.remaining(), rem);
            if (readable <= 0) {
                return 0;
            }

            int oldLimit = bb.limit();
            bb.limit(bb.position() + readable);
            dst.put(bb);
            bb.limit(oldLimit);
            return readable;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        ensureWritable();
        lock.writeLock().lock();
        try {
            ensureOpen();
            throw new UnsupportedOperationException();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public long position() throws IOException {
        lock.readLock().lock();
        try {
            ensureOpen();
            return bb.position();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        lock.writeLock().lock();
        try {
            ensureOpen();
            if (newPosition < 0 || newPosition >= Integer.MAX_VALUE) {
                throw new IllegalArgumentException("illegal position " + newPosition);
            }

            bb.position(Math.min((int) newPosition, bb.limit()));
            return this;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public long size() throws IOException {
        lock.readLock().lock();
        try {
            ensureOpen();
            return bb.limit();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        ensureWritable();
        lock.writeLock().lock();
        try {
            ensureOpen();
            throw new UnsupportedOperationException();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean isOpen() {
        return !closed;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        lock.writeLock().lock();
        try {
            closed = true;
            bb = null;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
