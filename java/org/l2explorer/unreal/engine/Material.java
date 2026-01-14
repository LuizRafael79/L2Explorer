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
package org.l2explorer.unreal.engine;

import org.l2explorer.io.L2DataInput;
import org.l2explorer.io.L2DataOutput;
import org.l2explorer.io.L2DataOutputStream;
import org.l2explorer.io.ObjectInput;
import org.l2explorer.io.ObjectOutput;
import org.l2explorer.io.annotation.ReadMethod;
import org.l2explorer.io.annotation.WriteMethod;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.core.Object;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Represents a Material in the Unreal Engine.
 * <p>The serialization of Materials in Lineage II is highly version-dependent, 
 * as the engine's rendering pipeline evolved through different chronicles.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class Material extends Object {
    /**
     * Raw unknown data block, serialized based on license version.
     */
    private byte[] unk;

    public Material() {
    }

    /**
     * Reads the material data from the input stream using version-specific logic.
     *
     * @param input The object input stream.
     */
    @ReadMethod
    public final void readMaterial(ObjectInput<UnrealRuntimeContext> input) {
        int version = input.getContext().getUnrealPackage().getVersion();
        int license = input.getContext().getUnrealPackage().getLicense();
        this.unk = readUnk(input, version, license);
    }

    /**
     * Writes the material data to the output stream.
     *
     * @param output The object output stream.
     * @throws IOException 
     */
    @WriteMethod
    public final void write(ObjectOutput<UnrealRuntimeContext> output) throws IOException {
        output.writeBytes(this.unk);
    }

    /**
     * Internal helper to parse the 'unk' block based on the engine license version.
     *
     * @param input   The data input.
     * @param version The package version.
     * @param license The license version.
     * @return Byte array containing the serialized material data.
     */
    protected byte[] readUnk(L2DataInput input, int version, int license) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             L2DataOutputStream output = new L2DataOutputStream(baos, input.getCharset())) {

            if (license <= 10) {
                // Early C0 versions - implementation unknown or minimal
            } else if (license <= 28) {
                // C0 to Interlude (CT0)
                output.writeInt(input.readInt());
            } else if (license <= 32) {
                // Early Kamael/Gracia
            } else if (license <= 35) {
                // CT1 (Hellbound) to CT2.2 (Freya)
                serializeBlock(input, output, 1067, 16);
            } else if (license == 36) {
                // CT2.2 (High Five)
                serializeBlock(input, output, 1058, 16);
            } else if (license <= 39) {
                // Epeisodion / Lindvior
                int headerSize = (version == 129) ? 92 : 36;
                serializeDynamicStrings(input, output, headerSize);
            } else {
                // Ertheia and newer
                serializeDynamicStrings(input, output, 92);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Error serializing Material unknown data", e);
        }
    }

    private void serializeBlock(L2DataInput input, L2DataOutput output, int byteSize, int lineCount) throws IOException {
        byte[] buffer = new byte[byteSize];
        input.readFully(buffer);
        output.writeBytes(buffer);
        for (int i = 0; i < lineCount; i++) {
            output.writeLine(input.readLine());
        }
        output.writeLine(input.readLine());
        output.writeInt(input.readInt());
    }

    private void serializeDynamicStrings(L2DataInput input, L2DataOutput output, int headerSize) throws IOException {
        byte[] buffer = new byte[headerSize];
        input.readFully(buffer);
        output.writeBytes(buffer);
        
        int stringCount = input.readCompactInt();
        output.writeCompactInt(stringCount);
        
        for (int i = 0; i < stringCount; i++) {
            output.writeLine(input.readLine());
            int addStringCount = input.readUnsignedByte();
            output.writeByte(addStringCount);
            for (int j = 0; j < addStringCount; j++) {
                output.writeLine(input.readLine());
            }
        }
        output.writeLine(input.readLine());
        output.writeInt(input.readInt());
    }

    // --- Getters and Setters ---

    public byte[] getUnk() {
        return unk;
    }

    public void setUnk(byte[] unk) {
        this.unk = unk;
    }
}