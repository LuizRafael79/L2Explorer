package org.l2explorer.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static org.l2explorer.io.ByteUtil.compactIntToByteArray;
import static java.nio.charset.StandardCharsets.UTF_16LE;

public interface L2DataOutput {
    // Métodos que devem ser implementados na L2DataOutputStream concreta
    void writeByte(int b) throws IOException;

    Charset getCharset();

    int getPosition() throws IOException;

    // --- Métodos Default com lógica Little-Endian ---

    default void writeBytes(byte[] b) throws IOException {
        writeBytes(b, 0, b.length);
    }

    default void writeBytes(byte[] b, int off, int len) throws IOException {
        if ((off | len | (b.length - (len + off)) | (off + len)) < 0) {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 0; i < len; i++) {
            writeByte(b[off + i]);
        }
    }

    default void writeShort(int val) throws IOException {
        writeByte((val) & 0xFF);
        writeByte((val >>> 8) & 0xFF);
    }

    default void writeInt(int val) throws IOException {
        writeByte((val) & 0xFF);
        writeByte((val >>> 8) & 0xFF);
        writeByte((val >>> 16) & 0xFF);
        writeByte((val >>> 24) & 0xFF);
    }

    default void writeCompactInt(int val) throws IOException {
        writeBytes(compactIntToByteArray(val));
    }

    default void writeLong(long val) throws IOException {
        writeByte((int) (val & 0xFF));
        writeByte((int) ((val >> 8) & 0xFF));
        writeByte((int) ((val >> 16) & 0xFF));
        writeByte((int) ((val >> 24) & 0xFF));
        writeByte((int) ((val >> 32) & 0xFF));
        writeByte((int) ((val >> 40) & 0xFF));
        writeByte((int) ((val >> 48) & 0xFF));
        writeByte((int) ((val >> 56) & 0xFF));
    }

    default void writeFloat(float val) throws IOException {
        writeInt(Float.floatToIntBits(val));
    }

    default void writeDouble(double val) throws IOException {
        writeLong(Double.doubleToLongBits(val));
    }

    default void writeBoolean(boolean val) throws IOException {
        writeInt(val ? 1 : 0);
    }

    default void writeChar(int val) throws IOException {
        writeShort(val);
    }

    // --- Métodos de Escrita de String (L2 Style) ---

    default void writeBytes(String s) throws IOException {
        if (s == null || s.isEmpty()) {
            writeCompactInt(0);
        } else {
            byte[] strBytes = (s + '\0').getBytes(getCharset() != null ? getCharset() : Charset.forName("EUC-KR"));
            writeCompactInt(strBytes.length);
            writeBytes(strBytes);
        }
    }

    default void writeChars(String s) throws IOException {
        if (s == null || s.isEmpty()) {
            writeCompactInt(0);
        } else {
            byte[] strBytes = (s + '\0').getBytes(UTF_16LE);
            writeCompactInt(-strBytes.length / 2); // L2 Unicode strings costumam ter comprimento negativo
            writeBytes(strBytes);
        }
    }

    default void writeLine(String s) throws IOException {
        if (s == null || s.isEmpty()) {
            writeCompactInt(0);
        } else if (getCharset() != null && getCharset().canEncode() && getCharset().newEncoder().canEncode(s)) {
            writeBytes(s);
        } else {
            writeChars(s);
        }
    }

    default void writeUTF(String s) throws IOException {
        if (s == null || s.isEmpty()) {
            writeInt(0);
        } else {
            byte[] strBytes = s.getBytes(UTF_16LE);
            writeInt(strBytes.length);
            writeBytes(strBytes);
        }
    }

    default void writeByteArray(byte[] array) throws IOException {
        writeCompactInt(array.length);
        writeBytes(array);
    }

    // --- Fábricas Estáticas ---

    static L2DataOutput dataOutput(OutputStream outputStream, Charset charset) {
        return dataOutput(outputStream, charset, 0);
    }

    static L2DataOutput dataOutput(OutputStream outputStream, Charset charset, int position) {
        return (L2DataOutput) new L2DataOutputStream(outputStream, charset, position);
    }

    static L2DataOutput dataOutput(ByteBuffer buffer, Charset charset) {
        return dataOutput(buffer, charset, 0);
    }

    static L2DataOutput dataOutput(ByteBuffer buffer, Charset charset, int position) {
        return (L2DataOutput) RandomAccess.randomAccess(buffer, null, charset, position);
    }
}