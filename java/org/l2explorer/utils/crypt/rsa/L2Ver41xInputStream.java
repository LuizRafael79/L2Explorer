/*
 * Copyright (c) 2021 acmi
 * Copyright (c) 2026 Galagard/L2Explorer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.l2explorer.utils.crypt.rsa;

import org.l2explorer.utils.crypt.CryptoException;

import javax.crypto.Cipher;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Objects;
import java.util.zip.InflaterInputStream;

/**
 * InputStream implementation for Lineage II RSA encryption (Version 41x).
 * <p>This stream handles the RSA decryption of 128-byte blocks followed by 
 * Zlib decompression of the resulting data.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public final class L2Ver41xInputStream extends FilterInputStream {
    private final int uncompressedSize;

    /**
     * Constructs a new RSA input stream with Zlib decompression.
     *
     * @param input    The encrypted source stream.
     * @param modulus  RSA Modulus.
     * @param exponent RSA Private Exponent.
     * @throws IOException     If an I/O error occurs.
     * @throws CryptoException If RSA initialization fails.
     */
    @SuppressWarnings("resource")
	public L2Ver41xInputStream(InputStream input, BigInteger modulus, BigInteger exponent) throws IOException, CryptoException {
        super(null);
        RSAInputStream rsaInputStream = new RSAInputStream(
                Objects.requireNonNull(input, "Source input stream cannot be null"),
                Objects.requireNonNull(modulus, "RSA modulus cannot be null"),
                Objects.requireNonNull(exponent, "RSA exponent cannot be null"));
        
        // Read the uncompressed size from the first 4 bytes of the RSA stream (Little Endian)
        this.uncompressedSize = Integer.reverseBytes(new DataInputStream(rsaInputStream).readInt());
        
        // Wrap the RSA stream with Zlib decompression
        this.in = new InflaterInputStream(rsaInputStream);
    }

    /**
     * Returns the expected size of the data after decompression.
     *
     * @return uncompressed size in bytes.
     */
    public int getUncompressedSize() {
        return uncompressedSize;
    }

    /**
     * Inner class to handle raw RSA block decryption.
     */
    public static class RSAInputStream extends InputStream {
        private final DataInputStream input;
        private final Cipher cipher;
        private final byte[] buffer = new byte[128];
        
        private int startPosition;
        private int position;
        private int currentBlockSize;
        private boolean closed;

        public RSAInputStream(InputStream source, BigInteger modulus, BigInteger exponent) throws CryptoException {
            this.input = new DataInputStream(source);

            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulus, exponent);
                this.cipher = Cipher.getInstance("RSA/ECB/NoPadding");
                this.cipher.init(Cipher.DECRYPT_MODE, keyFactory.generatePrivate(keySpec));
            } catch (GeneralSecurityException e) {
                throw new CryptoException("Failed to initialize RSA Cipher", e);
            }
        }

        private void ensureOpen() throws IOException {
            if (closed) {
                throw new IOException("Stream closed");
            }
        }

        /**
         * Decrypts the next 128-byte block if the current buffer is empty.
         */
        private boolean ensureFilled() throws IOException {
            if (position == currentBlockSize) {
                int remaining = buffer.length;
                while (remaining > 0) {
                    int count = input.read(buffer, buffer.length - remaining, remaining);
                    if (count < 0) {
                        return false;
                    }
                    remaining -= count;
                }

                try {
                    cipher.doFinal(buffer, 0, 128, buffer);
                } catch (GeneralSecurityException e) {
                    throw new CryptoException("RSA block decryption failed", e);
                }

                // The 4th byte of the decrypted block contains the size of valid data
                currentBlockSize = buffer[3] & 0xff;
                if (currentBlockSize > 124) {
                    throw new IllegalStateException("RSA block data size exceeds maximum allowed (124 bytes)");
                }

                // Calculate the start position within the 128-byte block
                startPosition = 128 - currentBlockSize - ((124 - currentBlockSize) % 4);
                position = 0;
            }
            return true;
        }

        @Override
        public int read() throws IOException {
            ensureOpen();
            if (!ensureFilled()) {
                return -1;
            }

            return buffer[startPosition + position++] & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException("Buffer cannot be null");
            } else if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            }

            ensureOpen();
            if (!ensureFilled()) {
                return -1;
            }

            int toRead = Math.min(len, available());
            System.arraycopy(buffer, startPosition + position, b, off, toRead);
            position += toRead;
            return toRead;
        }

        @Override
        public int available() throws IOException {
            ensureOpen();
            return currentBlockSize - position;
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                closed = true;
                input.close();
            }
        }
    }
}