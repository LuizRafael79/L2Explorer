package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a 64-bit Integer to a Byte (Opcode 0x5f).
 * <p>This token performs a narrowing conversion, truncating the 64-bit value 
 * to its least significant 8 bits (0-255).</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
@ConversionToken
public class INT64ToByte extends Token {
    /**
     * The bytecode operation code for INT64ToByte.
     */
    public static final int OPCODE = 0x5f;

    private Token value;

    /**
     * Default constructor for INT64ToByte.
     */
    public INT64ToByte() {
    }

    /**
     * Constructs an INT64ToByte conversion with the specified inner token.
     * * @param value The INT64 token value to be truncated to a byte.
     */
    public INT64ToByte(Token value) {
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
        if (!(o instanceof INT64ToByte)) return false;
        INT64ToByte that = (INT64ToByte) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "INT64ToByte(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the explicit byte cast.
     * * @param context The runtime context for the Unreal engine.
     * @return The string formatted as "byte(value)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String valStr = (value == null) ? "null" : value.toString(context);
        return "byte(" + valStr + ")";
    }
}