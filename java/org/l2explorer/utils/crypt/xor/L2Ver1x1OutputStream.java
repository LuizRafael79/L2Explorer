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
package org.l2explorer.utils.crypt.xor;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * OutputStream implementation for XOR encryption versions 111 and 121.
 * <p>Applies a static 8-bit XOR key to all outgoing data bytes. 
 * This is used for saving legacy Lineage 2 configuration and data files.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public final class L2Ver1x1OutputStream extends FilterOutputStream {
    private final int xorKey;

    /**
     * Constructs a new L2Ver1x1OutputStream.
     *
     * @param output The underlying output stream.
     * @param xorKey The 8-bit key for the XOR operation.
     * @throws NullPointerException if the output stream is null.
     */
    public L2Ver1x1OutputStream(OutputStream output, int xorKey) {
        super(Objects.requireNonNull(output, "Target output stream cannot be null"));
        this.xorKey = xorKey;
    }

    @Override
    public void write(int b) throws IOException {
        // Apply XOR encryption and write the single byte
        out.write(b ^ xorKey);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException("Buffer cannot be null");
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                   ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException("Invalid offset or length");
        } else if (len == 0) {
            return;
        }

        // Optimization: Encrypt the entire block in memory before writing
        byte[] encrypted = new byte[len];
        for (int i = 0; i < len; i++) {
            encrypted[i] = (byte) (b[off + i] ^ xorKey);
        }
        out.write(encrypted, 0, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}