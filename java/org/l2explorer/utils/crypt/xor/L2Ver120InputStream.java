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

import static org.l2explorer.utils.crypt.xor.L2Ver120.START_IND;
import static org.l2explorer.utils.crypt.xor.L2Ver120.getXORKey;

/**
 * InputStream implementation for XOR encryption version 120.
 * <p>Uses a rolling index starting at {@link L2Ver120#START_IND} 
 * to generate dynamic XOR keys for each byte processed.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public final class L2Ver120InputStream extends FilterInputStream {
    private int ind = START_IND;
    private int markInd;

    /**
     * Constructs a new L2Ver120InputStream.
     *
     * @param input the underlying input stream to be decrypted.
     * @throws NullPointerException if the input stream is null.
     */
    public L2Ver120InputStream(InputStream input) {
        super(Objects.requireNonNull(input, "Source input stream cannot be null"));
    }

    @Override
    public int read() throws IOException {
        int b = in.read();
        // If not end of stream, XOR the byte with the dynamically generated key
        return (b < 0) ? b : (b ^ getXORKey(ind++)) & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int r = in.read(b, off, len);
        if (r > 0) {
            for (int i = 0; i < r; i++) {
                // Apply the rolling XOR key to each byte in the buffer
                b[off + i] = (byte) (b[off + i] ^ getXORKey(ind++));
            }
        }
        return r;
    }

    @Override
    public synchronized void mark(int readlimit) {
        super.mark(readlimit);
        // Save the current index for reset operations
        markInd = ind;
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        // Restore the index to the state it was when mark() was called
        ind = markInd;
    }
}