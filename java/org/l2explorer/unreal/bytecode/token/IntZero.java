package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a constant integer value of 0 (Opcode 0x25).
 * <p>A specialized shortcut token used in the Lineage II version of the 
 * Unreal VM to represent the number 0 without additional data bytes.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class IntZero extends Token {
    /**
     * The bytecode operation code for IntZero.
     */
    public static final int OPCODE = 0x25;

    /**
     * Default constructor for IntZero.
     */
    public IntZero() {
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IntZero;
    }

    @Override
    public int hashCode() {
        return OPCODE;
    }

    @Override
    public String toString() {
        return "IntZero()";
    }

    /**
     * Returns the literal string "0" for the decompiler.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string "0".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "0";
    }
}