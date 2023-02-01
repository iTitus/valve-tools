package io.github.ititus.valve_tools.vpk.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class MultiReadOnlyChannel implements SeekableByteChannel {

    private final List<SeekableByteChannel> channels;
    private final long size;
    private long position;

    public MultiReadOnlyChannel(SeekableByteChannel... channels) throws IOException {
        this(Arrays.asList(channels));
    }

    public MultiReadOnlyChannel(Collection<? extends SeekableByteChannel> channels) throws IOException {
        if (Objects.requireNonNull(channels, "channels").size() < 2) {
            throw new IllegalArgumentException();
        }

        this.channels = List.copyOf(channels);
        this.position = 0;

        long size = 0;
        for (var channel : channels) {
            if (!Objects.requireNonNull(channel, "channel").isOpen()) {
                throw new IllegalArgumentException();
            }

            size = Math.addExact(size, channel.size());
        }
        this.size = size;

        position(0);
    }

    @Override
    public synchronized int read(ByteBuffer dst) throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }

        long rem = size - position;
        if (rem <= 0) {
            return -1;
        }


        int read = 0;
        SeekableByteChannel channel = null;
        for (var it = channels.iterator(); dst.hasRemaining() && it.hasNext(); ) {
            if (channel == null) {
                channel = it.next();
            }

            int chRead;
            try {
                chRead = channel.read(dst);
            } catch (IOException e) {
                position(this.position);
                throw new IOException("cannot read from multi channel", e);
            }

            if (chRead < 0) {
                channel = null;
            } else {
                read += chRead;
                if (channel.position() >= channel.size()) {
                    channel = null;
                }
            }
        }

        this.position += read;
        return read;
    }

    @Override
    public int write(ByteBuffer src) {
        throw new NonWritableChannelException();
    }

    @Override
    public long position() {
        return position;
    }

    @Override
    public synchronized SeekableByteChannel position(long newPosition) throws IOException {
        this.position = 0;
        for (var channel : channels) {
            long size = channel.size();
            if (size >= newPosition) {
                channel.position(newPosition);
                this.position += newPosition;
                newPosition = 0;
            } else {
                channel.position(size);
                this.position += size;
                newPosition -= size;
            }
        }

        return this;
    }

    @Override
    public long size() throws IOException {
        return size;
    }

    @Override
    public SeekableByteChannel truncate(long size) {
        throw new NonWritableChannelException();
    }

    @Override
    public boolean isOpen() {
        return channels.get(0).isOpen();
    }

    @Override
    public void close() throws IOException {
        IOException ex = null;
        for (var channel : channels) {
            try {
                channel.close();
            } catch (IOException e) {
                if (ex == null) {
                    ex = new IOException("failed to close multi channel");
                }

                ex.addSuppressed(e);
            }
        }

        if (ex != null) {
            throw ex;
        }
    }
}
