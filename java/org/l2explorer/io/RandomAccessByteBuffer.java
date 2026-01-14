package org.l2explorer.io;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.Charset;

public class RandomAccessByteBuffer implements RandomAccess {
    private final ByteBuffer buffer;
    private final String name;
    private final Charset charset;
    private final int offset; // Renomeado para clareza: é o ponto inicial no buffer global

    public RandomAccessByteBuffer(ByteBuffer buffer, String name, Charset charset, int offset) {
        this.buffer = buffer;
        this.name = name;
        this.charset = charset;
        this.offset = offset;
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
    public int getPosition() {
        return offset + buffer.position();
    }

    @Override
    public void setPosition(int pos) throws IOException {
        try {
            buffer.position(pos - offset);
        } catch (IllegalArgumentException e) {
            throw new IOException("Posição inválida no buffer: " + pos, e);
        }
    }

    @Override
    public void trimToPosition() {
        buffer.limit(buffer.position());
    }

    @Override
    public int readUnsignedByte() throws IOException {
        try {
            return buffer.get() & 0xff;
        } catch (BufferUnderflowException e) {
            throw new IOException("Fim do buffer atingido (EOF)", e);
        }
    }

    @Override
    public void writeByte(int b) throws IOException {
        try {
            buffer.put((byte) b);
        } catch (BufferOverflowException | ReadOnlyBufferException e) {
            throw new IOException("Falha ao escrever no buffer (Overflow ou ReadOnly)", e);
        }
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        try {
            buffer.get(b, off, len);
        } catch (BufferUnderflowException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        try {
            buffer.put(b, off, len);
        } catch (BufferOverflowException | ReadOnlyBufferException e) {
            throw new IOException(e);
        }
    }

    @Override
    public RandomAccess openNewSession(boolean readOnly) {
        // Para uma nova sessão real, o ideal seria usar buffer.duplicate()
        return new RandomAccessByteBuffer(buffer.duplicate(), name, charset, offset);
    }

    @Override
    public void close() {
        // ByteBuffers não precisam de fechamento explícito, mas o método deve existir
    }
    
    @Override
    public int skipBytes(int n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        
        int currentPos = getPosition();
        int available = buffer.limit() - buffer.position();
        int skipCount = Math.min(n, available);
        
        setPosition(currentPos + skipCount);
        return skipCount;
    }
}