package org.l2explorer.io;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Implementação de leitura de dados para arquivos do Lineage 2 / Unreal Engine.
 * Suporta Little-Endian e CompactInt.
 */
public class L2DataInputStream extends FilterInputStream implements L2DataInput {
    private final Charset charset;
    private int position;

    public L2DataInputStream(InputStream in, Charset charset) {
        super(in);
        this.charset = charset;
        this.position = 0;
    }

    public L2DataInputStream(InputStream in, Charset charset, int position) {
        super(in);
        this.charset = charset;
        this.position = position;
    }

    public Charset getCharset() {
        return charset;
    }

    public int getPosition() {
        return position;
    }

    private void incCount(int value) {
        if (value > 0) {
            position += value;
        }
    }

    // --- Implementação de InputStream ---

    @Override
    public int read() throws IOException {
        int tmp = in.read();
        if (tmp >= 0) {
            incCount(1);
        }
        return tmp;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int tmp = in.read(b, off, len);
        if (tmp >= 0) {
            incCount(tmp);
        }
        return tmp;
    }

    // --- Implementação de DataInput (Lógica Unreal/L2) ---

    @Override
    public void readFully(byte[] b) throws IOException { // Mudado de Unchecked para IOException
        readFully(b, 0, b.length);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        if (len < 0) throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = read(b, off + n, len - n);
            if (count < 0) throw new EOFException();
            n += count;
        }
    }

    public int skipBytes(int n) throws IOException {
        int total = 0;
        int cur = 0;
        while ((total < n) && ((cur = (int) in.skip(n - total)) > 0)) {
            total += cur;
        }
        incCount(total);
        return total;
    }

    @Override
    public boolean readBoolean() throws IOException {
        return readInt() != 0;
    }

    @Override
    public byte readByte() throws IOException {
        int tmp = read();
        if (tmp < 0) throw new EOFException();
        return (byte) tmp;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int tmp = read();
        if (tmp < 0) throw new EOFException();
        return tmp;
    }

    @Override
    public short readShort() throws IOException {
        byte[] b = new byte[2];
        readFully(b, 0, 2);
        return (short) (((b[1] & 0xFF) << 8) | (b[0] & 0xFF));
    }

    @Override
    public int readUnsignedShort() throws IOException {
        byte[] b = new byte[2];
        readFully(b, 0, 2);
        return ((b[1] & 0xFF) << 8) | (b[0] & 0xFF);
    }

    @Override
    public char readChar() throws IOException {
        return (char) readShort();
    }

    @Override
    public int readInt() throws IOException {
        byte[] b = new byte[4];
        readFully(b, 0, 4);
        return ((b[3] & 0xFF) << 24) |
               ((b[2] & 0xFF) << 16) |
               ((b[1] & 0xFF) << 8)  |
               (b[0] & 0xFF);
    }

    @Override
    public long readLong() throws IOException {
        byte[] b = new byte[8];
        readFully(b, 0, 8);
        return ((long)(b[7] & 0xFF) << 56) |
               ((long)(b[6] & 0xFF) << 48) |
               ((long)(b[5] & 0xFF) << 40) |
               ((long)(b[4] & 0xFF) << 32) |
               ((long)(b[3] & 0xFF) << 24) |
               ((long)(b[2] & 0xFF) << 16) |
               ((long)(b[1] & 0xFF) << 8)  |
               ((long)(b[0] & 0xFF));
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public int readCompactInt() throws IOException {
        int output = 0;
        boolean signed = false;
        for (int i = 0; i < 5; i++) {
            int b = readUnsignedByte();
            if (i == 0) {
                if ((b & 0x80) != 0) signed = true;
                output |= (b & 0x3F);
                if ((b & 0x40) == 0) break;
            } else {
                output |= (b & 0x7F) << (6 + (i - 1) * 7);
                if ((b & 0x80) == 0) break;
            }
        }
        return signed ? -output : output;
    }

    public String readString() throws IOException {
        int len = readCompactInt();
        if (len == 0) return "";

        if (len < 0) {
            // Unicode (UTF-16LE)
            int actualLen = -len * 2;
            byte[] data = new byte[actualLen];
            readFully(data);
            return new String(data, 0, actualLen - 2, java.nio.charset.StandardCharsets.UTF_16LE);
        } else {
            // ANSI / Charset definido (EUC-KR)
            byte[] data = new byte[len];
            readFully(data);
            return new String(data, 0, len - 1, charset);
        }
    }

    @Override
    public String readLine() throws IOException {
        throw new UnsupportedOperationException("Use readString() para arquivos de L2");
    }

    @Override
    public String readUTF() throws IOException {
        throw new UnsupportedOperationException("Use readString() para arquivos de L2");
    }
}