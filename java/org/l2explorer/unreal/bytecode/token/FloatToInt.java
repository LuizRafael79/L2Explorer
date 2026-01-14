package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a Float to an Integer (Opcode 0x44).
 * <p>This token performs a truncation of the floating-point value to its 
 * integer part (e.g., 3.9 becomes 3).</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Pro (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
@ConversionToken
public class FloatToInt extends Token {
    /**
     * The bytecode operation code for FloatToInt.
     */
    public static final int OPCODE = 0x44;

    private Token value;

    /**
     * Default constructor for FloatToInt.
     */
    public FloatToInt() {
    }

    /**
     * Constructs a FloatToInt conversion with the specified inner token.
     * * @param value The token value to be truncated to an integer.
     */
    public FloatToInt(Token value) {
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
        if (!(o instanceof FloatToInt)) return false;
        FloatToInt that = (FloatToInt) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "FloatToInt(" + value + ")";
    }

    /**
     * Returns the decompiler representation.
     * <p>In UnrealScript, this is usually an implicit conversion, so it 
     * returns the string representation of the inner value without explicit casting.</p>
     * * @param context The runtime context for the Unreal engine.
     * @return The string representation of the inner value.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return value == null ? "null" : value.toString(context);
    }
}