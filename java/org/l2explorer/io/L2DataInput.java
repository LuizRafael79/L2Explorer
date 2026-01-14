package org.l2explorer.io;

import static java.nio.charset.StandardCharsets.UTF_16LE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public interface L2DataInput {
    
    // --- Métodos de Contexto (Devem ser implementados na DataInputStream) ---
    Charset getCharset();
    int getPosition() throws IOException;
    int readUnsignedByte() throws IOException;
    int skipBytes(int n) throws IOException;

    // --- Métodos de leitura com lógica Little-Endian padrão L2 ---

    default void skip(int n) throws IOException {
        if (n <= 0) return;
        byte[] skipBuffer = new byte[0x1000];
        while (n > skipBuffer.length) {
            readFully(skipBuffer);
            n -= skipBuffer.length;
        }
        readFully(new byte[n]);
    }

    default void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    default void readFully(byte[] b, int off, int len) throws IOException {
        if (b == null) throw new NullPointerException();
        if (off < 0 || len < 0 || len > b.length - off) throw new IndexOutOfBoundsException();
        for (int i = 0; i < len; i++) {
            b[off + i] = (byte) readUnsignedByte();
        }
    }

    default boolean readBoolean() throws IOException {
        return readInt() != 0;
    }

    default byte readByte() throws IOException {
        return (byte) readUnsignedByte();
    }

    default short readShort() throws IOException {
        return (short) readUnsignedShort();
    }

    default int readUnsignedShort() throws IOException {
        int ch1 = readUnsignedByte();
        int ch2 = readUnsignedByte();
        return ch1 + (ch2 << 8);
    }

    default char readChar() throws IOException {
        return (char) readUnsignedShort();
    }

    default int readInt() throws IOException {
        int ch1 = readUnsignedByte();
        int ch2 = readUnsignedByte();
        int ch3 = readUnsignedByte();
        int ch4 = readUnsignedByte();
        return (ch1 + (ch2 << 8) + (ch3 << 16) + (ch4 << 24));
    }

    default long readLong() throws IOException {
        return ((((long) readUnsignedByte())) |
                (((long) readUnsignedByte()) << 8) |
                (((long) readUnsignedByte()) << 16) |
                (((long) readUnsignedByte()) << 24) |
                (((long) readUnsignedByte()) << 32) |
                (((long) readUnsignedByte()) << 40) |
                (((long) readUnsignedByte()) << 48) |
                (((long) readUnsignedByte()) << 56));
    }

    default float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    default double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    // --- Métodos Específicos de String e Arrays do L2 ---

    default int readCompactInt() throws IOException {
        // Implementação direta sem depender de ByteUtil se preferir simplificar
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

    default String readLine() throws IOException {
        int len = readCompactInt();
        if (len == 0) return "";

        byte[] bytes = new byte[len > 0 ? len : -2 * len];
        readFully(bytes);
        // L2 usa null terminator (0x00), por isso o length - 1 ou - 2
        return new String(bytes, 0, bytes.length - (len > 0 ? 1 : 2), 
                         (len > 0 && getCharset() != null) ? getCharset() : UTF_16LE);
    }

    default String readUTF() throws IOException {
        int len = readInt();
        if (len <= 0) return "";
        byte[] bytes = new byte[len];
        readFully(bytes);
        return new String(bytes, UTF_16LE);
    }

    default byte[] readByteArray() throws IOException {
        int len = readCompactInt();
        if (len < 0) throw new IllegalStateException("Invalid array length");
        byte[] array = new byte[len];
        readFully(array);
        return array;
    }

    // --- Métodos Estáticos de Fábrica ---

    static L2DataInput dataInput(InputStream inputStream, Charset charset) {
        return dataInput(inputStream, charset, 0);
    }

    static L2DataInput dataInput(InputStream inputStream, Charset charset, int position) {
        return (L2DataInput) new L2DataInputStream(inputStream, charset, position);
    }

    static L2DataInput dataInput(ByteBuffer buffer, Charset charset) {
        return dataInput(buffer, charset, 0);
    }

    static L2DataInput dataInput(ByteBuffer buffer, Charset charset, int position) {
        // Certifique-se que a classe RandomAccess existe no seu projeto
        return (L2DataInput) RandomAccess.randomAccess(buffer, null, charset, position);
    }
}