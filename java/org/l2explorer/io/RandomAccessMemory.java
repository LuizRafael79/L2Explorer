package org.l2explorer.io;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class RandomAccessMemory implements RandomAccess {
    private static final int DEFAULT_CAPACITY = 1 << 16;

    private final String name;
    private final Charset charset;
    private ByteBuffer buffer;

    public RandomAccessMemory(String name, byte[] data, Charset charset) {
        this.name = name;
        this.buffer = ByteBuffer.wrap(data);
        this.charset = charset;
    }

    public RandomAccessMemory(String name, Charset charset) {
        this(name, new byte[DEFAULT_CAPACITY], charset);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public int getPosition() throws IOException {
        return buffer.position();
    }

    @Override
    public void setPosition(int position) throws IOException {
        ensureCapacity(position);
        buffer.position(position);
    }

    @Override
    public void trimToPosition() throws IOException {
        buffer.limit(buffer.position());
    }

    // MUDANÇA: Implementação obrigatória de skipBytes da interface DataInput
    @Override
    public int skipBytes(int n) throws IOException {
        if (n <= 0) return 0;
        
        int available = buffer.limit() - buffer.position();
        int toSkip = Math.min(n, available);
        
        buffer.position(buffer.position() + toSkip);
        return toSkip;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        if (buffer.position() >= buffer.limit()) {
            throw new EOFException();
        }
        return buffer.get() & 0xff;
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }

        if (buffer.position() + len > buffer.limit()) {
            throw new EOFException();
        }

        buffer.get(b, off, len);
    }

    @Override
    public void writeByte(int b) throws IOException {
        ensureCapacity(buffer.position() + 1);
        buffer.put((byte) b);
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(buffer.position() + len);
        buffer.put(b, off, len);
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > buffer.capacity()) {
            grow(minCapacity);
        }
        if (minCapacity > buffer.limit()) {
            buffer.limit(minCapacity);
        }
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private void grow(int minCapacity) {
        int oldCapacity = buffer.capacity();
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }
        int limit = buffer.limit();
        int position = buffer.position();
        buffer = ByteBuffer.wrap(Arrays.copyOf(buffer.array(), newCapacity));
        buffer.limit(limit);
        buffer.position(position);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    @Override
    public RandomAccess openNewSession(boolean readOnly) throws IOException {
        // Retorna uma nova instância com um buffer duplicado para sessões independentes
        return new RandomAccessMemory(name, buffer.array(), charset);
    }

    @Override
    public void close() throws IOException {
        // Buffers de memória não exigem fechamento de stream físico
    }

    // MUDANÇA: Corrigido para IOException
    public void writeTo(L2DataOutput output) throws IOException {
        output.writeBytes(buffer.array(), 0, buffer.limit());
    }
}