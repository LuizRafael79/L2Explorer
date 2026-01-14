package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents the instruction to proceed to the next iteration in an iterator loop (Opcode 0x31).
 * <p>Used internally by the 'ForEach' mechanism to fetch the next element 
 * from the iterator's state. In source code, it may represent a 'continue' statement.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class IteratorNext extends Token {
    /**
     * The bytecode operation code for IteratorNext.
     */
    public static final int OPCODE = 0x31;

    /**
     * Default constructor for IteratorNext.
     */
    public IteratorNext() {
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IteratorNext;
    }

    @Override
    public int hashCode() {
        return OPCODE;
    }

    @Override
    public String toString() {
        return "IteratorNext()";
    }

    /**
     * Returns the decompiler representation of the iterator progression.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string "continue".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "continue";
    }
}