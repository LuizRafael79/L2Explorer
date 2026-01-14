package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a null object reference literal (Opcode 0x2a).
 * <p>In UnrealScript source code, this is represented by the keyword 'None'.
 * It is used to signify that a variable or parameter does not point to any valid object.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class NoObject extends Token {
    /**
     * The bytecode operation code for NoObject.
     */
    public static final int OPCODE = 0x2a;

    /**
     * Default constructor for NoObject.
     */
    public NoObject() {
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NoObject;
    }

    @Override
    public int hashCode() {
        return OPCODE;
    }

    @Override
    public String toString() {
        return "NoObject()";
    }

    /**
     * Returns the literal string "None" for the decompiler.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string "None".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "None";
    }
}