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
package org.l2explorer.unreal.core;

import org.l2explorer.io.ObjectInput;
import org.l2explorer.io.ObjectOutput;
import org.l2explorer.io.annotation.ReadMethod;
import org.l2explorer.io.annotation.WriteMethod;
import org.l2explorer.unreal.UnrealRuntimeContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Base class for all Property types in the Unreal Engine (inherits from Field).
 * <p>Properties define data storage, including array dimensions, flags for 
 * editor behavior, and network replication offsets.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class Property extends Field {
    private int arrayDimension;
    private int elementSize;
    private int propertyFlags;
    private String category;
    private int replicationOffset;

    public Property() {
    }

    /**
     * Reads property metadata from the input stream.
     *
     * @param input The object input stream.
     * @throws IOException If an I/O error occurs.
     */
    @ReadMethod
    public final void readProperty(ObjectInput<UnrealRuntimeContext> input) throws IOException {
        this.arrayDimension = input.readUnsignedShort();
        this.elementSize = input.readUnsignedShort();
        this.propertyFlags = input.readInt();
        this.category = (String) input.getContext().getUnrealPackage().nameReference(input.readCompactInt());
        this.replicationOffset = (this.propertyFlags & CPF.Net.getMask()) != 0 ? input.readUnsignedShort() : 0;
    }

    /**
     * Writes property metadata to the output stream.
     *
     * @param output The object output stream.
     * @throws IOException If an I/O error occurs.
     */
    @WriteMethod
    public final void writeProperty(ObjectOutput<UnrealRuntimeContext> output) throws IOException {
        output.writeShort(this.arrayDimension);
        output.writeShort(this.elementSize);
        output.writeInt(this.propertyFlags);
        output.writeCompactInt(output.getContext().getUnrealPackage().nameReference(this.category));
        if ((this.propertyFlags & CPF.Net.getMask()) != 0) {
            output.writeShort(this.replicationOffset);
        }
    }

    // --- Getters and Setters ---

    public int getArrayDimension() {
        return arrayDimension;
    }

    public void setArrayDimension(int arrayDimension) {
        this.arrayDimension = arrayDimension;
    }

    public int getElementSize() {
        return elementSize;
    }

    public void setElementSize(int elementSize) {
        this.elementSize = elementSize;
    }

    public int getPropertyFlags() {
        return propertyFlags;
    }

    public void setPropertyFlags(int propertyFlags) {
        this.propertyFlags = propertyFlags;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getReplicationOffset() {
        return replicationOffset;
    }

    public void setReplicationOffset(int replicationOffset) {
        this.replicationOffset = replicationOffset;
    }

    /**
     * Enum representing Class Property Flags (CPF).
     */
    public enum CPF {
        Edit(0x00000001),
        Const(0x00000002),
        Input(0x00000004),
        ExportObject(0x00000008),
        OptionalParm(0x00000010),
        Net(0x00000020),
        ConstRef(0x00000040),
        Parm(0x00000080),
        OutParm(0x00000100),
        SkipParm(0x00000200),
        ReturnParm(0x00000400),
        CoerceParm(0x00000800),
        Native(0x00001000), 
        Transient(0x00002000),
        Config(0x00004000),
        Localized(0x00008000),
        Travel(0x00010000),
        EditConst(0x00020000),
        GlobalConfig(0x00040000),
        OnDemand(0x00100000),
        New(0x00200000),
        NeedCtorLink(0x00400000),
        UNK_00800000(0x00800000),
        UNK_01000000(0x01000000),
        UNK_02000000(0x02000000),
        EditInline(0x04000000),
        EdFindable(0x08000000),
        EditInlineUse(0x10000000),
        Deprecated(0x20000000),
        EditInlineNotify(0x40000000),
        UNK_80000000(0x80000000);

        private final int mask;

        CPF(int mask) {
            this.mask = mask;
        }

        public int getMask() {
            return mask;
        }

        public static Collection<CPF> getFlags(int flags) {
            return Arrays.stream(values())
                    .filter(e -> (e.getMask() & flags) != 0)
                    .collect(Collectors.toList());
        }

        public static int getFlags(CPF... flags) {
            int v = 0;
            for (CPF flag : flags) {
                v |= flag.getMask();
            }
            return v;
        }

        @Override
        public String toString() {
            return "CPF_" + name();
        }
    }
}