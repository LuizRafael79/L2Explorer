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

import java.nio.charset.StandardCharsets;

/**
 * Static constants for Blowfish encryption versions 211 and 212.
 * <p>These keys are used by the BlowfishEngine to decrypt Lineage 2 
 * client files with headers Lineage2Ver211 and Lineage2Ver212.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public final class L2Ver21x {
    /**
     * Secret key used for Blowfish version 211.
     */
    public static final byte[] BLOWFISH_KEY_211 = "31==-%&@!^+][;'.]94-\0".getBytes(StandardCharsets.US_ASCII);

    /**
     * Secret key used for Blowfish version 212.
     */
    public static final byte[] BLOWFISH_KEY_212 = "[;'.]94-&@%!^+]-31==\0".getBytes(StandardCharsets.US_ASCII);

    /**
     * Private constructor to prevent instantiation of this constant class.
     */
    private L2Ver21x() {
    }
}