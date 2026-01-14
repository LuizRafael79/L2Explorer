package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a Float to a String (Opcode 0x55).
 * <p>This token is typically injected by the compiler for implicit string 
 * conversions, such as when concatenating a float with a string literal.</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Pro (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
@ConversionToken
public class FloatToString extends Token {
    /**
     * The bytecode operation code for FloatToString.
     */
    public static final int OPCODE = 0x55;

    private Token value;

    /**
     * Default constructor for FloatToString.
     */
    public FloatToString() {
    }

    /**
     * Constructs a FloatToString conversion with the specified inner token.
     * * @param value The token value to be converted to a string.
     */
    public FloatToString(Token value) {
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
        if (!(o instanceof FloatToString)) return false;
        FloatToString that = (FloatToString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "FloatToString(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the float value.
     * <p>Since this conversion is usually implicit in UnrealScript, 
     * it returns the inner token's string representation directly.</p>
     * * @param context The runtime context for the Unreal engine.
     * @return The string representation of the inner value.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return value == null ? "null" : value.toString(context);
    }
}