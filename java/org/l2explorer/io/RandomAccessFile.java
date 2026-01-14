package org.l2explorer.io;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_16LE;

public class RandomAccessFile implements RandomAccess {
    protected final java.io.RandomAccessFile file;
    private final String packageName;
    private final String path;

    private final int cryptVer;
    protected final int xorKey;
    protected final int startOffset;

    private final Charset charset;

    // CONSTRUTOR: Agora lança IOException real
    public RandomAccessFile(File f, boolean readOnly, Charset charset) throws IOException {
        file = new java.io.RandomAccessFile(f, readOnly ? "r" : "rw");
        
        String name = f.getName();
        packageName = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name;
        path = f.getPath();

        String l2CryptHeader;
        if (file.length() >= 28 && (l2CryptHeader = getCryptHeader(file)).startsWith("Lineage2Ver")) {
            startOffset = 28;
            cryptVer = Integer.parseInt(l2CryptHeader.substring(11));
            switch (cryptVer) {
                case 111:
                    xorKey = 0xACACACAC;
                    break;
                case 121:
                    int xb = getCryptKey(f.getName());
                    xorKey = xb | (xb << 8) | (xb << 16) | (xb << 24);
                    break;
                default:
                    throw new IOException("Crypt " + cryptVer + " is not supported.");
            }
        } else {
            startOffset = 0;
            cryptVer = 0;
            xorKey = 0;
        }

        this.charset = charset;
        setPosition(0);
    }

    public RandomAccessFile(String path, boolean readOnly, Charset charset) throws IOException {
        this(new File(path), readOnly, charset);
    }

    private static String getCryptHeader(java.io.RandomAccessFile file) throws IOException {
        byte[] l2CryptHeaderBytes = new byte[28];
        file.readFully(l2CryptHeaderBytes);
        return new String(l2CryptHeaderBytes, UTF_16LE);
    }

    private static int getCryptKey(String filename) {
        filename = filename.toLowerCase();
        int ind = 0;
        for (int i = 0; i < filename.length(); i++) {
            ind += filename.charAt(i);
        }
        return ind & 0xff;
    }

    @Override
    public String getName() {
        return packageName;
    }

    public String getPath() {
        return path;
    }

    public int getCryptVersion() {
        return cryptVer;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public void setPosition(int pos) throws IOException {
        file.seek(pos + startOffset);
    }

    @Override
    public int getPosition() throws IOException {
        return (int) file.getFilePointer() - startOffset;
    }

    @Override
    public void trimToPosition() throws IOException {
        file.setLength(file.getFilePointer());
    }

    // OBRIGATÓRIO: Implementar skipBytes para satisfazer a interface DataInput
    @Override
    public int skipBytes(int n) throws IOException {
        if (n <= 0) return 0;
        int current = getPosition();
        int available = (int) file.length() - (int) file.getFilePointer();
        int toSkip = Math.min(n, available);
        setPosition(current + toSkip);
        return toSkip;
    }

    @Override
    public void close() throws IOException {
        file.close();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int b = file.read();
        if (b < 0) {
            throw new EOFException();
        }

        if (cryptVer != 0) {
            return (b ^ xorKey) & 0xff;
        }
        return b;
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        file.readFully(b, off, len);

        if (cryptVer != 0) {
            for (int i = 0; i < len; i++) {
                b[off + i] ^= xorKey;
            }
        }
    }

    @Override
    public void writeByte(int b) throws IOException {
        if (cryptVer != 0) {
            file.write(b ^ xorKey);
        } else {
            file.write(b);
        }
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        if ((off | len | (b.length - (len + off)) | (off + len)) < 0) {
            throw new IndexOutOfBoundsException();
        }

        if (cryptVer != 0) {
            byte[] toWrite = Arrays.copyOfRange(b, off, off + len);
            for (int i = 0; i < toWrite.length; i++) {
                toWrite[i] ^= xorKey;
            }
            file.write(toWrite);
        } else {
            file.write(b, off, len);
        }
    }

    @Override
    public RandomAccessFile openNewSession(boolean readOnly) throws IOException {
        return new RandomAccessFile(getPath(), readOnly, getCharset());
    }
}