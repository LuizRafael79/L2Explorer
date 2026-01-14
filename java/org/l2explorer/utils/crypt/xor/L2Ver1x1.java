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

/**
 * Constants and key derivation logic for XOR encryption versions 111 and 121.
 * <p>Version 111 uses a static global key, while version 121 derives its key
 * from the sum of the characters in the filename (case-insensitive).</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public final class L2Ver1x1 {
    /**
     * Static XOR key for version 111.
     */
    public static final int XOR_KEY_111 = 0xAC;

    /**
     * Calculates the XOR key for version 121 based on the filename.
     * <p>The key is the 8-bit sum of all characters in the lowercase filename.</p>
     *
     * @param filename The name of the file being processed.
     * @return An 8-bit XOR key (0-255).
     */
    public static int getXORKey121(String filename) {
        if (filename == null) {
            return 0;
        }
        
        String lowerName = filename.toLowerCase();
        int keySum = 0;
        for (int i = 0; i < lowerName.length(); i++) {
            keySum += lowerName.charAt(i);
        }
        
        // Return only the last 8 bits
        return keySum & 0xFF;
    }

    /** Private constructor to prevent instantiation. */
    private L2Ver1x1() {
    }
}