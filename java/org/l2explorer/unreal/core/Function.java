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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.l2explorer.io.L2DataInput;
import org.l2explorer.io.L2DataOutput;
import org.l2explorer.io.annotation.ReadMethod;
import org.l2explorer.io.annotation.UByte;
import org.l2explorer.io.annotation.UShort;
import org.l2explorer.io.annotation.WriteMethod;
import org.l2explorer.unreal.annotation.Bytecode;
import org.l2explorer.unreal.bytecode.token.Token;

/**
 * Represents a Function definition in the Unreal Engine (inherits from Struct).
 * <p>Functions contain the bytecode and metadata for execution, including 
 * native indices for C++ calls and flags for network replication.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class Function extends Struct {
    @UShort
    private int nativeIndex;
    @UByte
    private int operatorPrecedence;
    public int functionFlags;
    @UShort
    private int replicationOffset;

    @Bytecode
	public Token[] bytecode;
    
    public Token[] getBytecode() {
        return bytecode;
    }
    
    public void setBytecode(Token[] bytecode) {
        this.bytecode = bytecode;
    } 
   
    public Function() {
    }

    /**
     * Reads function metadata from the input stream.
     *
     * @param input The data input stream.
     * @throws IOException If an I/O error occurs.
     */
    @ReadMethod
    public void readFunction(L2DataInput input) throws IOException {
        nativeIndex = input.readUnsignedShort();
        operatorPrecedence = input.readUnsignedByte();
        functionFlags = input.readInt();
        replicationOffset = (functionFlags & Flag.NET.getMask()) != 0 ? input.readUnsignedShort() : 0;
    }

    /**
     * Writes function metadata to the output stream.
     *
     * @param output The data output stream.
     * @throws IOException If an I/O error occurs.
     */
    @WriteMethod
    public final void writeFunction(L2DataOutput output) throws IOException {
        output.writeShort(nativeIndex);
        output.writeByte(operatorPrecedence);
        output.writeInt(functionFlags);
        if ((functionFlags & Flag.NET.getMask()) != 0) {
            output.writeShort(replicationOffset);
        }
    }

    // --- Getters and Setters ---

    public int getNativeIndex() {
        return nativeIndex;
    }

    public void setNativeIndex(int nativeIndex) {
        this.nativeIndex = nativeIndex;
    }

    public int getOperatorPrecedence() {
        return operatorPrecedence;
    }

    public void setOperatorPrecedence(int operatorPrecedence) {
        this.operatorPrecedence = operatorPrecedence;
    }

    public int getFunctionFlags() {
        return functionFlags;
    }

    public void setFunctionFlags(int functionFlags) {
        this.functionFlags = functionFlags;
    }

    public int getReplicationOffset() {
        return replicationOffset;
    }

    public void setReplicationOffset(int replicationOffset) {
        this.replicationOffset = replicationOffset;
    }

    /**
     * Enum representing the various flags available for an Unreal function.
     */
    public enum Flag {
        FINAL,
        DEFINED,
        ITERATOR,
        LATENT,
        PRE_OPERATOR,
        SINGULAR,
        NET,
        NET_RELIABLE,
        SIMULATED,
        EXEC,
        NATIVE,
        EVENT,
        OPERATOR,
        STATIC,
        NO_EXPORT,
        CONST,
        INVARIANT,
        PROTECTED,
        Flag18,
        Flag19,
        DELEGATE;

        private final int mask = 1 << ordinal();

        public int getMask() {
            return mask;
        }

        @Override
        public String toString() {
            return "FF_" + name();
        }

        /**
         * Returns a collection of flags present in the given bitmask.
         *
         * @param flags The bitmask to analyze.
         * @return A collection of active Flag enums.
         */
        public static Collection<Flag> getFlags(int flags) {
            return Arrays.stream(values())
                    .filter(e -> (e.getMask() & flags) != 0)
                    .collect(Collectors.toList());
        }

        /**
         * Combines multiple flags into a single integer bitmask.
         *
         * @param flags The flags to combine.
         * @return The resulting bitmask.
         */
        public static int getFlags(Flag... flags) {
            int v = 0;
            for (Flag flag : flags) {
                v |= flag.getMask();
            }
            return v;
        }
    }
}