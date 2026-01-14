package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import java.util.Objects;

/**
 * Represents the Stop token (Opcode 0x08).
 * <p>In UnrealScript, the Stop opcode is used to terminate the execution 
 * of code within a state or function. It effectively stops the 
 * virtual machine's execution thread for the current context.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class Stop extends Token {
    /**
     * The bytecode opcode for the Stop instruction.
     */
    public static final int OPCODE = 0x08;

    /**
     * Default constructor for the Stop token.
     */
    public Stop() {
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
     * @return The string "Stop()".
     */
    @Override
    public String toString() {
        return "Stop()";
    }

    /**
     * Returns the UnrealScript representation of this token.
     * <p>The Stop opcode is an internal control flow instruction 
     * and does not have a direct textual representation in deparsed source code.</p>
     *
     * @param context The runtime context for deparsing.
     * @return An empty string.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "";
    }

    /**
     * Compares this token to the specified object.
     * Since Stop has no internal state, all instances are considered equal.
     *
     * @param o The object to compare with.
     * @return true if the object is an instance of Stop; false otherwise.
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