package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a return statement (Opcode 0x04).
 * <p>Exits the current function and optionally returns a value to the caller.
 * This instruction triggers the VM to pop the current stack frame.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class Return extends Token {
    /**
     * The bytecode operation code for Return.
     */
    public static final int OPCODE = 0x04;

    private Token value;

    /**
     * Default constructor for Return.
     */
    public Return() {
    }

    /**
     * Constructs a Return token with the specified expression.
     *
     * @param value The token representing the return value.
     */
    public Return(Token value) {
        this.value = value;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Return)) return false;
        Return aReturn = (Return) o;
        return Objects.equals(value, aReturn.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Return(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the return statement.
     * <p>If the value is null or represents 'Nothing', it returns just 'return'.
     * Otherwise, it returns 'return' followed by the expression.</p>
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string for the decompiler.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String valStr = (value == null || value instanceof Nothing) ? "" : value.toString(context);
        return ("return " + valStr).trim();
    }
}