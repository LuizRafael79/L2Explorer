package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a 64-bit Integer to a Float (Opcode 0x62).
 * <p>This token handles the conversion of large integer values into 
 * 32-bit floating point numbers, often used in UI calculations or 
 * scaled game mechanics.</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
@ConversionToken
public class INT64ToFloat extends Token {
    /**
     * The bytecode operation code for INT64ToFloat.
     */
    public static final int OPCODE = 0x62;

    private Token value;

    /**
     * Default constructor for INT64ToFloat.
     */
    public INT64ToFloat() {
    }

    /**
     * Constructs an INT64ToFloat conversion with the specified inner token.
     * * @param value The INT64 token value to be converted to float.
     */
    public INT64ToFloat(Token value) {
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
        if (!(o instanceof INT64ToFloat)) return false;
        INT64ToFloat that = (INT64ToFloat) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "INT64ToFloat(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the explicit float cast.
     * * @param context The runtime context for the Unreal engine.
     * @return The string formatted as "float(value)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String valStr = (value == null) ? "null" : value.toString(context);
        return "float(" + valStr + ")";
    }
}