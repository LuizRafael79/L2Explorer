package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.io.annotation.UShort;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.Offset;

import java.util.Objects;

/**
 * Represents a Skip token (Opcode 0x18).
 * <p>The Skip token is used for short-circuit evaluation in logical expressions 
 * and other control flow structures, providing an offset to jump to if a 
 * certain condition is met.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class Skip extends Token {
    /**
     * The bytecode opcode for the Skip instruction.
     */
    public static final int OPCODE = 0x18;

    /**
     * The target offset in the bytecode to jump to.
     */
    @UShort
    @Offset
    private int targetOffset;

    /**
     * Default constructor for serialization and reflection.
     */
    public Skip() {
    }

    /**
     * Constructs a Skip token with a specific target offset.
     *
     * @param targetOffset The bytecode address to skip to.
     */
    public Skip(int targetOffset) {
        this.targetOffset = targetOffset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    /**
     * Gets the jump target offset.
     *
     * @return The bytecode offset value.
     */
    public int getTargetOffset() {
        return targetOffset;
    }

    /**
     * Sets the jump target offset.
     *
     * @param targetOffset The bytecode offset value to set.
     */
    public void setTargetOffset(int targetOffset) {
        this.targetOffset = targetOffset;
    }

    /**
     * Returns a string representation of the token for debugging purposes.
     *
     * @return String formatted as Skip(0xXXXX).
     */
    @Override
    public String toString() {
        return String.format("Skip(0x%04x)", targetOffset);
    }

    /**
     * Returns the UnrealScript representation of this token.
     * <p>Skip tokens are usually transparent in high-level UnrealScript 
     * source code as they are part of the underlying control flow logic.</p>
     *
     * @param context The runtime context for deparsing.
     * @return An empty string as it doesn't have a direct keyword representation.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "";
    }

    /**
     * Compares this token to the specified object.
     *
     * @param o The object to compare with.
     * @return true if the target offsets are identical; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skip skip = (Skip) o;
        return targetOffset == skip.targetOffset;
    }

    /**
     * Returns the hash code for this token.
     *
     * @return The hash code value based on the target offset.
     */
    @Override
    public int hashCode() {
        return Objects.hash(targetOffset);
    }
}