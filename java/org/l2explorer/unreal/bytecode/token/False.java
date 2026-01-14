package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a boolean 'false' literal in the bytecode (Opcode 0x28).
 * * <p>This token is used whenever a hardcoded false value is encountered 
 * within the UnrealScript source code.</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Pro (Clean Room Reimplementation)
 * 
 * @since 12-01-2026
 */
public class False extends Token {
    /**
     * The bytecode operation code for False.
     */
    public static final int OPCODE = 0x28;

    /**
     * Default constructor for the False token.
     */
    public False() {
    }

    /**
     * Returns the opcode associated with this boolean literal.
     * * @return {@link #OPCODE}
     */
    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    /**
     * Compares this token to another object for equality.
     * * @param o The object to compare with.
     * @return true if the other object is also an instance of {@code False}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof False;
    }

    /**
     * Returns the hash code for this token.
     * * @return The constant opcode value as hash.
     */
    @Override
    public int hashCode() {
        return OPCODE;
    }

    /**
     * Returns a string representation for internal debugging.
     */
    @Override
    public String toString() {
        return "False()";
    }

    /**
     * Returns the literal string "false" as it appears in UnrealScript source.
     * * @param context The runtime context for the Unreal engine.
     * @return The string "false".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "false";
    }
}