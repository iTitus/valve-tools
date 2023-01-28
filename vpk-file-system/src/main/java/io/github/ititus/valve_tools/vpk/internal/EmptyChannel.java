package io.github.ititus.valve_tools.vpk.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;

public final class EmptyChannel implements SeekableByteChannel {

    private boolean closed;

    private void ensureOpen() throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        ensureOpen();
        return -1;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        ensureOpen();
        throw new NonWritableChannelException();
    }

    @Override
    public long position() throws IOException {
        ensureOpen();
        return 0;
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        ensureOpen();
        if (newPosition < 0) {
            throw new IllegalArgumentException("illegal position " + newPosition);
        }

        return this;
    }

    @Override
    public long size() throws IOException {
        ensureOpen();
        return 0;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        ensureOpen();
        throw new NonWritableChannelException();
    }

    @Override
    public boolean isOpen() {
        return !closed;
    }

    @Override
    public void close() {
        closed = true;
    }
}
