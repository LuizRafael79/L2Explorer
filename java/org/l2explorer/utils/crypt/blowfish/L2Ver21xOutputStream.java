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
import org.l2explorer.utils.crypt.FinishableOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * OutputStream decorator that encrypts data using the Blowfish algorithm (v21x).
 * <p>Data is buffered into 8-byte blocks before being processed by the cipher.
 * If the final block is incomplete, it is padded with null bytes (0x00) before encryption.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public final class L2Ver21xOutputStream extends FinishableOutputStream {
    private final BlowfishEngine blowfish = new BlowfishEngine();

    private final byte[] writeBuffer = new byte[8];
    private final ByteBuffer dataBuffer = ByteBuffer.allocate(8);

    private boolean finished;

    /**
     * Creates a new L2Ver21xOutputStream.
     *
     * @param output the underlying output stream.
     * @param key    the secret key for Blowfish encryption.
     * @throws NullPointerException if output or key is null.
     */
    public L2Ver21xOutputStream(OutputStream output, byte[] key) {
        super(Objects.requireNonNull(output, "Output stream cannot be null"));
        this.blowfish.init(true, Objects.requireNonNull(key, "Blowfish key cannot be null"));
    }

    @Override
    public void write(int b) throws IOException {
        if (finished) {
            throw new IOException("Stream already finished. Cannot write more data.");
        }

        dataBuffer.put((byte) b);
        
        // When the 8-byte block is full, encrypt and write it
        if (dataBuffer.position() == dataBuffer.limit()) {
            writeData();
            dataBuffer.clear();
        }
    }

    /**
     * Finalizes the encryption process, padding the last block if necessary.
     */
    @Override
    public void finish() throws IOException {
        if (finished) {
            return;
        }

        finished = true;
        
        // Process the last remaining block (even if partial)
        if (dataBuffer.position() > 0) {
            writeData();
        }
        flush();
    }

    /**
     * Encrypts the current contents of the dataBuffer and writes it to the underlying stream.
     */
    private void writeData() throws IOException {
        // If the block is partial, pad the rest with zeros (Standard L2 practice)
        if (dataBuffer.position() < dataBuffer.limit()) {
            Arrays.fill(dataBuffer.array(), dataBuffer.position(), dataBuffer.limit(), (byte) 0);
        }

        try {
            // blowfish.processBlock(input, inOff, output, outOff)
            blowfish.processBlock(dataBuffer.array(), dataBuffer.arrayOffset(), writeBuffer, 0);
        } catch (Exception e) {
            throw new CryptoException("Blowfish encryption failed during write", e);
        }
        
        out.write(writeBuffer);
    }

    @Override
    public void close() throws IOException {
        try {
            finish();
        } finally {
            super.close();
        }
    }
}