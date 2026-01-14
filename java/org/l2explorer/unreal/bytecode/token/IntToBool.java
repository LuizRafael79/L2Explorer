package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from an Integer to a Boolean (Opcode 0x3e).
 * <p>In the Unreal VM, this token evaluates an integer expression where 
 * any non-zero value is considered true, and zero is considered false.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
@ConversionToken
public class IntToBool extends Token {
    /**
     * The bytecode operation code for IntToBool.
     */
    public static final int OPCODE = 0x3e;

    private Token value;

    /**
     * Default constructor for IntToBool.
     */
    public IntToBool() {
    }

    /**
     * Constructs an IntToBool conversion with the specified inner token.
     *
     * @param value The integer token to be converted to boolean.
     */
    public IntToBool(Token value) {
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
        if (!(o instanceof IntToBool)) return false;
        IntToBool intToBool = (IntToBool) o;
        return Objects.equals(value, intToBool.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "IntToBool(" + value + ")";
    }

    /**
     * Returns the decompiler representation as an explicit boolean cast.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string formatted as "bool(value)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String inner = (value == null) ? "null" : value.toString(context);
        return "bool(" + inner + ")";
    }
}