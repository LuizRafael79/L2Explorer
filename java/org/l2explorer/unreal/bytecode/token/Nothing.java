package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents the absence of an expression (Opcode 0x0b).
 * <p>This token is used by the Unreal VM as a placeholder for omitted 
 * optional parameters or empty expressions within a bytecode stream.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class Nothing extends Token {
    /**
     * The bytecode operation code for Nothing.
     */
    public static final int OPCODE = 0x0b;

    /**
     * Default constructor for Nothing.
     */
    public Nothing() {
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Nothing;
    }

    @Override
    public int hashCode() {
        return OPCODE;
    }

    @Override
    public String toString() {
        return "Nothing()";
    }

    /**
     * Returns an empty string as this token represents an omitted expression.
     *
     * @param context The runtime context for the Unreal engine.
     * @return An empty string.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "";
    }
}