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
import org.l2explorer.utils.crypt.FinishableOutputStream;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.DeflaterOutputStream;

/**
 * OutputStream implementation for Lineage II RSA encryption (Version 41x).
 * <p>Data is first compressed using Zlib (Deflater) and then encrypted 
 * in 128-byte blocks using the RSA algorithm.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public final class L2Ver41xOutputStream extends FinishableOutputStream {
    private final ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
    private boolean finished;

    /**
     * Constructs a new RSA output stream with Zlib compression.
     *
     * @param output   The destination stream.
     * @param modulus  RSA Modulus.
     * @param exponent RSA Public Exponent.
     */
    public L2Ver41xOutputStream(OutputStream output, BigInteger modulus, BigInteger exponent) {
        super(new RSAOutputStream(
                Objects.requireNonNull(output, "Destination stream cannot be null"), 
                Objects.requireNonNull(modulus, "RSA modulus cannot be null"), 
                Objects.requireNonNull(exponent, "RSA exponent cannot be null")));
    }

    @Override
    public void write(int b) throws IOException {
        if (finished) {
            throw new IOException("Stream already finished. Cannot write more data.");
        }
        dataBuffer.write(b);
    }

    /**
     * Finalizes the compression and encryption process.
     * <p>Writes the uncompressed data size (Little Endian), compresses the buffer, 
     * and triggers the final RSA block processing.</p>
     */
    @Override
    public void finish() throws IOException {
        if (finished) {
            return;
        }
        finished = true;

        // Write uncompressed size in Little Endian (standard L2 format)
        new DataOutputStream(out).writeInt(Integer.reverseBytes(dataBuffer.size()));

        // Compress data into the RSAOutputStream
        try (DeflaterOutputStream deflater = new DeflaterOutputStream(out)) {
            dataBuffer.writeTo(deflater);
            deflater.finish();
        }

        // Finalize the underlying RSA stream
        ((RSAOutputStream) out).finish();
    }

    /**
     * Inner class that handles raw RSA encryption in 128-byte chunks.
     */
    private static class RSAOutputStream extends FinishableOutputStream {
        private final Cipher cipher;
        private final ByteBuffer dataBuffer = ByteBuffer.allocate(124);
        private final byte[] block = new byte[128];
        private boolean finished;

        public RSAOutputStream(OutputStream output, BigInteger modulus, BigInteger exponent) {
            super(output);
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
                this.cipher = Cipher.getInstance("RSA/ECB/NoPadding");
                this.cipher.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(keySpec));
            } catch (GeneralSecurityException e) {
                throw new CryptoException("Failed to initialize RSA encryption", e);
            }
        }

        @Override
        public void write(int b) throws IOException {
            if (finished) {
                throw new IOException("RSA stream already finished");
            }

            dataBuffer.put((byte) b);

            if (dataBuffer.position() == dataBuffer.limit()) {
                writeData();
                dataBuffer.clear();
            }
        }

        @Override
        public void finish() throws IOException {
            if (finished) {
                return;
            }
            finished = true;
            writeData();
            flush();
        }

        private void writeData() throws IOException {
            int size = dataBuffer.position();
            if (size == 0) {
                return;
            }

            // Fill block with zeros and set size byte at index 3
            Arrays.fill(block, (byte) 0);
            block[3] = (byte) (size & 0xff);
            
            // Align data according to NCSoft's custom RSA padding
            int offset = 128 - size - ((124 - size) % 4);
            System.arraycopy(dataBuffer.array(), 0, block, offset, size);

            try {
                cipher.doFinal(block, 0, 128, block);
            } catch (GeneralSecurityException e) {
                throw new CryptoException("RSA block encryption failed", e);
            }

            out.write(block);
        }
    }
}