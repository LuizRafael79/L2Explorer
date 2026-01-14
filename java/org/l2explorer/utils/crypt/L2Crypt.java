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
package org.l2explorer.utils.crypt;

import static org.l2explorer.utils.crypt.blowfish.L2Ver21x.BLOWFISH_KEY_211;
import static org.l2explorer.utils.crypt.blowfish.L2Ver21x.BLOWFISH_KEY_212;
import static org.l2explorer.utils.crypt.rsa.L2Ver41x.*;
import static org.l2explorer.utils.crypt.xor.L2Ver1x1.XOR_KEY_111;
import static org.l2explorer.utils.crypt.xor.L2Ver1x1.getXORKey121;
import static java.nio.charset.StandardCharsets.UTF_16LE;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import org.l2explorer.utils.crypt.blowfish.L2Ver21xInputStream;
import org.l2explorer.utils.crypt.blowfish.L2Ver21xOutputStream;
import org.l2explorer.utils.crypt.lame.LameCrypt;
import org.l2explorer.utils.crypt.rsa.L2Ver41xInputStream;
import org.l2explorer.utils.crypt.rsa.L2Ver41xOutputStream;
import org.l2explorer.utils.crypt.xor.L2Ver120InputStream;
import org.l2explorer.utils.crypt.xor.L2Ver120OutputStream;
import org.l2explorer.utils.crypt.xor.L2Ver1x1InputStream;
import org.l2explorer.utils.crypt.xor.L2Ver1x1OutputStream;

/**
 * Central utility for Lineage II file encryption and decryption.
 * <p>Supports various headers (Lineage2Ver111 to 912) covering XOR, Blowfish, 
 * and RSA algorithms used across different game chronicles.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public class L2Crypt {
    /** Version constant for unencrypted files. */
    public static final int NO_CRYPT = -1;
    /** Fixed size of the Lineage2VerXXX header (28 bytes in UTF-16LE). */
    public static final int HEADER_SIZE = 28;

    private static final BigInteger[][] RSA_KEYS = new BigInteger[][]{
            {MODULUS_411, PRIVATE_EXPONENT_411},
            {MODULUS_412, PRIVATE_EXPONENT_412},
            {MODULUS_413, PRIVATE_EXPONENT_413},
            {MODULUS_414, PRIVATE_EXPONENT_414}
    };

    private static BigInteger publicModulus = MODULUS_L2ENCDEC;
    private static BigInteger publicExponent = PUBLIC_EXPONENT_L2ENCDEC;

    /**
     * Updates the RSA private key for a specific 41x version.
     */
    public static void set41xPrivateKey(int version, BigInteger modulus, BigInteger exponent) {
        int index = version - 411;
        if (index >= 0 && index < RSA_KEYS.length) {
            RSA_KEYS[index][0] = modulus;
            RSA_KEYS[index][1] = exponent;
        }
    }

    /**
     * Sets the global RSA public key used for encryption.
     */
    public static void set41xPublicKey(BigInteger modulus, BigInteger exponent) {
        publicModulus = modulus;
        publicExponent = exponent;
    }

    /**
     * Reads the 28-byte header and extracts the version number.
     *
     * @param input Stream to read from.
     * @return Version number (e.g., 413) or NO_CRYPT if header is missing.
     * @throws IOException If reading fails.
     */
    public static int readHeader(InputStream input) throws IOException {
        byte[] header = new byte[HEADER_SIZE];
        DataInputStream dis = new DataInputStream(input);
        dis.readFully(header);
        
        String headerStr = new String(header, UTF_16LE);
        if (!headerStr.startsWith("Lineage2Ver")) {
            return NO_CRYPT;
        }

        try {
            return Integer.parseInt(headerStr.substring(11).trim());
        } catch (NumberFormatException e) {
            return NO_CRYPT;
        }
    }

    /**
     * Writes the Lineage2VerXXX header to the stream.
     */
    public static void writeHeader(OutputStream output, int version) throws IOException {
        String header = "Lineage2Ver" + version;
        byte[] bytes = header.getBytes(UTF_16LE);
        // Ensure we always write exactly 28 bytes
        if (bytes.length < HEADER_SIZE) {
            byte[] padded = new byte[HEADER_SIZE];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            output.write(padded);
        } else {
            output.write(bytes, 0, HEADER_SIZE);
        }
    }

    public static InputStream getInputStream(File file) throws IOException, CryptoException {
        return decrypt(new FileInputStream(file), file.getName());
    }

    /**
     * Decrypts an input stream based on its detected version.
     */
    public static InputStream decrypt(InputStream input, String fileName) throws IOException, CryptoException {
        int version = readHeader(input);
        
        // Handle LameCrypt (8xx and 9xx versions)
        if (version >= 811 && version <= 912) {
            input = LameCrypt.wrapInput(input);
            if (version >= 911) version -= 700; // 911 -> 211 (Blowfish)
            else if (version >= 811) version -= 700; // 811 -> 111 (XOR)
        } else if (version >= 611 && version <= 614) {
            input = LameCrypt.wrapInput(input);
            version -= 200; // 611 -> 411 (RSA)
        } else if (version == 820) {
            input = LameCrypt.wrapInput(input);
            version = 120;
        }

        return switch (version) {
            case NO_CRYPT -> input;
            
            // XOR Family
            case 111 -> new L2Ver1x1InputStream(input, XOR_KEY_111);
            case 121 -> new L2Ver1x1InputStream(input, getXORKey121(fileName));
            case 120 -> new L2Ver120InputStream(input);
            
            // Blowfish Family
            case 211 -> new L2Ver21xInputStream(input, BLOWFISH_KEY_211);
            case 212 -> new L2Ver21xInputStream(input, BLOWFISH_KEY_212);
            
            // RSA Family
            case 411, 412, 413, 414 -> {
                BigInteger mod = RSA_KEYS[version - 411][0];
                BigInteger exp = RSA_KEYS[version - 411][1];
                yield new L2Ver41xInputStream(input, mod, exp);
            }
            
            default -> throw new CryptoException("Unsupported crypt version: " + version);
        };
    }

    public static OutputStream getOutputStream(File file, int version) throws IOException, CryptoException {
        return encrypt(new FileOutputStream(file), file.getName(), version);
    }

    /**
     * Encrypts an output stream using the specified version.
     */
    public static OutputStream encrypt(OutputStream output, String fileName, int version) throws IOException, CryptoException {
        if (version == NO_CRYPT) {
            return output;
        }

        writeHeader(output, version);
        
        int effectiveVersion = version;
        OutputStream target = output;

        // Apply LameCrypt wrapper for legacy/custom versions
        if (version >= 811 && version <= 912) {
            target = LameCrypt.wrapOutput(target);
            if (version >= 911) effectiveVersion -= 700;
            else if (version >= 811) effectiveVersion -= 700;
        } else if (version >= 611 && version <= 614) {
            target = LameCrypt.wrapOutput(target);
            effectiveVersion -= 200;
        } else if (version == 820) {
            target = LameCrypt.wrapOutput(target);
            effectiveVersion = 120;
        }

        return switch (effectiveVersion) {
            // XOR Family
            case 111 -> new L2Ver1x1OutputStream(target, XOR_KEY_111);
            case 121 -> new L2Ver1x1OutputStream(target, getXORKey121(fileName));
            case 120 -> new L2Ver120OutputStream(target);
            
            // Blowfish Family
            case 211 -> new L2Ver21xOutputStream(target, BLOWFISH_KEY_211);
            case 212 -> new L2Ver21xOutputStream(target, BLOWFISH_KEY_212);
            
            // RSA Family
            case 411, 412, 413, 414 -> new L2Ver41xOutputStream(target, publicModulus, publicExponent);
            
            default -> throw new CryptoException("Unsupported version: " + version);
        };
    }
}