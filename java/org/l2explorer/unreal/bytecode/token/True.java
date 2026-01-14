package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import java.util.Objects;

/**
 * Represents a constant Boolean 'true' literal (Opcode 0x27).
 * <p>In UnrealScript, this token is used to represent the logical true value
 * in expressions and assignments.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class True extends Token {
    /**
     * The bytecode opcode for the 'true' constant.
     */
    public static final int OPCODE = 0x27;

    /**
     * Default constructor for the True token.
     */
    public True() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    /**
     * Returns a string representation of the token for debugging purposes.
     *
     * @return The string "True()".
     */
    @Override
    public String toString() {
        return "True()";
    }

    /**
     * Returns the UnrealScript representation of this token.
     *
     * @param context The runtime context for deparsing.
     * @return The literal string "true".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "true";
    }

    /**
     * Compares this token to the specified object.
     * Since True has no internal state, all instances are considered equal.
     *
     * @param o The object to compare with.
     * @return true if the object is an instance of True; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }

    /**
     * Returns the hash code for this token.
     *
     * @return A constant hash code value based on the opcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(OPCODE);
    }
}