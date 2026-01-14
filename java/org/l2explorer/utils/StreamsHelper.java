/*
 * Copyright (c) 2016 acmi
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
package org.l2explorer.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility for reading and writing Unreal Engine's primitive stream types.
 * <p>Handles Compact Indices (variable-length integers) and Unreal Strings, 
 * which can be either single-byte (ANSI) or double-byte (UTF-16LE).</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public final class StreamsHelper {
    private static final Charset ANSI = StandardCharsets.US_ASCII;
    private static final Charset UTF16LE = StandardCharsets.UTF_16LE;
    private static final CharsetEncoder ANSI_ENCODER = ANSI.newEncoder();

    /**
     * Reads an Unreal Compact Index from the input stream.
     *
     * @param input the source InputStream.
     * @return the decoded integer.
     * @throws IOException if an I/O error occurs.
     */
    public static int readCompactInt(InputStream input) throws IOException {
        int output = 0;
        boolean signed = false;
        for (int i = 0; i < 5; i++) {
            int x = input.read();
            if (x < 0) {
                throw new EOFException("Unexpected end of stream while reading CompactInt");
            }
            if (i == 0) {
                if ((x & 0x80) > 0) signed = true;
                output |= (x & 0x3F);
                if ((x & 0x40) == 0) break;
            } else if (i == 4) {
                output |= (x & 0x1F) << (6 + (3 * 7));
            } else {
                output |= (x & 0x7F) << (6 + ((i - 1) * 7));
                if ((x & 0x80) == 0) break;
            }
        }
        return signed ? -output : output;
    }

    /**
     * Reads an Unreal string. 
     * Positive length indicates ANSI, negative indicates UTF-16LE.
     *
     * @param input the source InputStream.
     * @return the decoded String.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("resource")
	public static String readString(InputStream input) throws IOException {
        int len = readCompactInt(input);
        if (len == 0) return "";

        boolean isAnsi = len > 0;
        int absoluteLen = isAnsi ? len : -2 * len;
        byte[] bytes = new byte[absoluteLen];
        
        // Using DataInputStream to ensure we read the full buffer
        new DataInputStream(input).readFully(bytes);

        // Remove the null terminator (1 byte for ANSI, 2 bytes for UTF-16)
        int terminatorSize = isAnsi ? 1 : 2;
        return new String(bytes, 0, bytes.length - terminatorSize, isAnsi ? ANSI : UTF16LE).intern();
    }

    /**
     * Writes an integer as an Unreal Compact Index.
     */
    public static void writeCompactInt(OutputStream output, int value) throws IOException {
        output.write(compactIntToByteArray(value));
    }

    /**
     * Writes a string in Unreal format, automatically choosing between ANSI and UTF-16LE.
     */
    public static void writeString(OutputStream buffer, String s) throws IOException {
        if (s == null || s.isEmpty()) {
            writeCompactInt(buffer, 0);
            return;
        }

        // Add null terminator
        String nullTerminated = s + '\0';
        boolean canEncodeAnsi = ANSI_ENCODER.canEncode(nullTerminated);
        byte[] bytes = nullTerminated.getBytes(canEncodeAnsi ? ANSI : UTF16LE);
        
        // Positive length for ANSI, Negative (char count) for UTF-16
        writeCompactInt(buffer, canEncodeAnsi ? bytes.length : -bytes.length / 2);
        buffer.write(bytes);
    }

    /**
     * Converts an integer to the variable-length byte array used by Unreal.
     */
    public static byte[] compactIntToByteArray(int v) {
        boolean negative = v < 0;
        v = Math.abs(v);
        int[] bytes = new int[]{
                (v) & 0x3F,
                (v >> 6) & 0x7F,
                (v >> 13) & 0x7F,
                (v >> 20) & 0x7F,
                (v >> 27) & 0x7F
        };

        if (negative) bytes[0] |= 0x80;

        int size = 5;
        for (int i = 4; i > 0; i--) {
            if (bytes[i] != 0) break;
            size--;
        }
        
        byte[] res = new byte[size];
        for (int i = 0; i < size; i++) {
            if (i != size - 1) {
                bytes[i] |= (i == 0) ? 0x40 : 0x80;
            }
            res[i] = (byte) bytes[i];
        }
        return res;
    }

    private StreamsHelper() {}
}