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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * InputStream implementation for XOR encryption versions 111 and 121.
 * <p>Applies a single-byte XOR key to the entire data stream. This is typically 
 * used for legacy Lineage 2 configuration and interface files.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public final class L2Ver1x1InputStream extends FilterInputStream {
    private final int xorKey;

    /**
     * Constructs a new L2Ver1x1InputStream.
     *
     * @param input   The underlying input stream.
     * @param xorKey  The 8-bit key to use for the XOR operation.
     * @throws NullPointerException if the input stream is null.
     */
    public L2Ver1x1InputStream(InputStream input, int xorKey) {
        super(Objects.requireNonNull(input, "Source input stream cannot be null"));
        this.xorKey = xorKey;
    }

    @Override
    public int read() throws IOException {
        int b = in.read();
        
        // Return EOF if reached, otherwise apply XOR and ensure 0-255 range
        return (b < 0) ? b : (b ^ xorKey) & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int r = in.read(b, off, len);
        
        if (r > 0) {
            for (int i = 0; i < r; i++) {
                // Apply the static XOR key to each byte in the buffer
                b[off + i] = (byte) (b[off + i] ^ xorKey);
            }
        }
        
        return r;
    }
}