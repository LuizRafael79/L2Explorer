package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a constant integer value of 1 (Opcode 0x26).
 * <p>A specialized shortcut token used in the Lineage II version of the 
 * Unreal VM to represent the number 1 without additional data bytes.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class IntOne extends Token {
    /**
     * The bytecode operation code for IntOne.
     */
    public static final int OPCODE = 0x26;

    /**
     * Default constructor for IntOne.
     */
    public IntOne() {
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IntOne;
    }

    @Override
    public int hashCode() {
        return OPCODE;
    }

    @Override
    public String toString() {
        return "IntOne()";
    }

    /**
     * Returns the literal string "1" for the decompiler.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string "1".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "1";
    }
}