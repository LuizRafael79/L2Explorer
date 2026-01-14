package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.UShort;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.Offset;

/**
 * Represents an unconditional jump instruction (Opcode 0x06).
 * <p>This token instructs the Unreal VM to move the instruction pointer 
 * directly to the specified offset. It is the foundation for loops, 
 * code block branching, and complex control flow structures.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class Jump extends Token {
    /**
     * The bytecode operation code for Jump.
     */
    public static final int OPCODE = 0x06;

    @UShort
    @Offset
    private int targetOffset;

    /**
     * Default constructor for Jump.
     */
    public Jump() {
    }

    /**
     * Constructs a Jump instruction with a specific target offset.
     *
     * @param targetOffset The bytecode offset to jump to.
     */
    public Jump(int targetOffset) {
        this.targetOffset = targetOffset;
    }

    public int getTargetOffset() {
        return targetOffset;
    }

    public void setTargetOffset(int targetOffset) {
        this.targetOffset = targetOffset;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jump)) return false;
        Jump jump = (Jump) o;
        return targetOffset == jump.targetOffset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetOffset);
    }

    @Override
    public String toString() {
        return String.format("Jump(0x%04x)", targetOffset);
    }

    /**
     * Returns the string representation of the jump for the decompiler.
     * <p>Since a jump is an internal VM instruction, it is represented 
     * by its target offset in hex format.</p>
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string "Jump(0xOffset)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return toString();
    }
}