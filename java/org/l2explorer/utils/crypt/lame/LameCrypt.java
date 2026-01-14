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
package org.l2explorer.utils.crypt.lame;

import org.l2explorer.utils.crypt.L2Crypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implements the "LameCrypt" (L2EncDec) encryption layer.
 * <p>This is a simple XOR stream cipher that uses a specific Delphi VCL 
 * error string as the key. It is used as an additional layer for 
 * encrypted versions like 811, 821, 911, and 912.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public class LameCrypt {
    /**
     * The magic string used as the XOR key. 
     * This is a standard Delphi runtime error message.
     */
    public static final String CRYPT_STRING = "Range check error while converting variant of type (%s) into type (%s)";

    /**
     * Wraps an {@link InputStream} with LameCrypt decryption.
     *
     * @param input The encrypted input stream.
     * @return A stream that decrypts data on the fly.
     */
    public static InputStream wrapInput(InputStream input) {
        return new InputStream() {
            // The position starts based on the HEADER_SIZE offset
            private int pos = L2Crypt.HEADER_SIZE % CRYPT_STRING.length();

            @Override
            public int read() throws IOException {
                int b = input.read();

                if (b == -1) {
                    return -1;
                }

                // XOR operation with the character at current position
                b ^= CRYPT_STRING.charAt(pos++);

                // Loop back to the start of the string if end is reached
                if (pos == CRYPT_STRING.length()) {
                    pos = 0;
                }

                return b;
            }

            @Override
            public void close() throws IOException {
                input.close();
            }
        };
    }

    /**
     * Wraps an {@link OutputStream} with LameCrypt encryption.
     *
     * @param output The raw output stream.
     * @return A stream that encrypts data on the fly.
     */
    public static OutputStream wrapOutput(OutputStream output) {
        return new OutputStream() {
            private int pos = L2Crypt.HEADER_SIZE % CRYPT_STRING.length();

            @Override
            public void write(int b) throws IOException {
                // XOR operation for encryption (XOR is its own inverse)
                b ^= CRYPT_STRING.charAt(pos++);

                if (pos == CRYPT_STRING.length()) {
                    pos = 0;
                }

                output.write(b);
            }

            @Override
            public void flush() throws IOException {
                output.flush();
            }

            @Override
            public void close() throws IOException {
                output.close();
            }
        };
    }
}