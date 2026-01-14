package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.UShort;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.Offset;

/**
 * Represents a conditional jump instruction (Opcode 0x07).
 * <p>The VM evaluates the associated condition token. If the condition 
 * result is false (0), the instruction pointer jumps to the target offset. 
 * This is primarily used for 'if' statements and loop conditions.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class JumpIfNot extends Token {
    /**
     * The bytecode operation code for JumpIfNot.
     */
    public static final int OPCODE = 0x07;

    @UShort
    @Offset
    private int targetOffset;

    private Token condition;

    /**
     * Default constructor for JumpIfNot.
     */
    public JumpIfNot() {
    }

    /**
     * Constructs a JumpIfNot instruction with a target offset and a condition.
     *
     * @param targetOffset The bytecode offset to jump to if the condition is false.
     * @param condition The token representing the expression to be evaluated.
     */
    public JumpIfNot(int targetOffset, Token condition) {
        this.targetOffset = targetOffset;
        this.condition = condition;
    }

    public int getTargetOffset() {
        return targetOffset;
    }

    public void setTargetOffset(int targetOffset) {
        this.targetOffset = targetOffset;
    }

    public Token getCondition() {
        return condition;
    }

    public void setCondition(Token condition) {
        this.condition = condition;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JumpIfNot)) return false;
        JumpIfNot that = (JumpIfNot) o;
        return targetOffset == that.targetOffset && 
               Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetOffset, condition);
    }

    @Override
    public String toString() {
        return String.format("JumpIfNot(0x%04x, %s)", targetOffset, condition);
    }

    /**
     * Returns the decompiler representation of the conditional jump.
     *
     * @param context The runtime context for the Unreal engine.
     * @return A formatted string showing the target offset and evaluated condition.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String condStr = (condition == null) ? "null" : condition.toString(context);
        return String.format("JumpIfNot(0x%04x, %s)", targetOffset, condStr);
    }
}