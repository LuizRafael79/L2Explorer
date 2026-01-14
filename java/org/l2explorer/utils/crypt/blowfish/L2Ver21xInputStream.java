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
package org.l2explorer.utils.crypt.blowfish;

import org.l2explorer.utils.crypt.CryptoException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * InputStream decorator that decrypts data using the Blowfish algorithm (v21x).
 * <p>Data is processed in 8-byte blocks, as required by the Blowfish cipher 
 * implementation for Lineage II client files.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public final class L2Ver21xInputStream extends InputStream {
    private final DataInputStream in;
    private final BlowfishEngine blowfish = new BlowfishEngine();

    private final byte[] readBuffer = new byte[8];
    private final ByteBuffer dataBuffer = ByteBuffer.allocate(8);

    {
        // Set position to limit so the first read() triggers a block processing
        dataBuffer.position(dataBuffer.limit());
    }

    /**
     * Creates a new L2Ver21xInputStream.
     *
     * @param input the underlying input stream.
     * @param key   the 128-bit (or custom) key for Blowfish initialization.
     * @throws NullPointerException if input or key is null.
     */
    public L2Ver21xInputStream(InputStream input, byte[] key) {
        this.in = new DataInputStream(Objects.requireNonNull(input, "InputStream cannot be null"));
        this.blowfish.init(false, Objects.requireNonNull(key, "Blowfish key cannot be null"));
    }

    @Override
    public int read() throws IOException {
        // If buffer is exhausted, read and decrypt the next block
        if (dataBuffer.position() == dataBuffer.limit()) {
            fillBuffer();
        }
        return dataBuffer.get() & 0xff;
    }

    /**
     * Reads an 8-byte block from the source stream and decrypts it.
     *
     * @throws IOException if an I/O error occurs or decryption fails.
     */
    private void fillBuffer() throws IOException {
        try {
            in.readFully(readBuffer);
            dataBuffer.clear();
            
            // Blowfish processBlock handles exactly 8 bytes (64 bits)
            blowfish.processBlock(readBuffer, 0, dataBuffer.array(), dataBuffer.arrayOffset());
        } catch (Exception e) {
            throw new CryptoException("Blowfish decryption failed", e);
        }
    }

    @Override
    public int available() throws IOException {
        // Return remaining data in buffer plus available data in the underlying stream
        return (dataBuffer.limit() - dataBuffer.position()) + in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}