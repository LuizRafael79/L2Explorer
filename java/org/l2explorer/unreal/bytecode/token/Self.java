package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import java.util.Objects;

/**
 * Represents the 'self' keyword (Opcode 0x17).
 * <p>In UnrealScript, 'self' is a reference to the object in which 
 * the current code is executing, similar to 'this' in Java or C++.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class Self extends Token {
    /**
     * The bytecode opcode for Self reference.
     */
    public static final int OPCODE = 0x17;

    /**
     * Default constructor for the Self token.
     */
    public Self() {
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
     * @return The string "Self()".
     */
    @Override
    public String toString() {
        return "Self()";
    }

    /**
     * Returns the UnrealScript representation of this token.
     *
     * @param context The runtime context for deparsing.
     * @return The literal string "self".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "self";
    }

    /**
     * Compares this token to the specified object.
     * Since Self has no state, all instances are considered equal.
     *
     * @param o The object to compare with.
     * @return true if the object is an instance of Self; false otherwise.
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
     * @return A constant hash code value for Self.
     */
    @Override
    public int hashCode() {
        return Objects.hash(OPCODE);
    }
}