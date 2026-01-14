package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a Float to a 64-bit Integer (Opcode 0x5d).
 * <p>Used in UnrealScript when a floating-point value needs to be explicitly 
 * cast to an INT64, commonly for handling large numbers like Adena or XP.</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Pro (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
@ConversionToken
public class FloatToINT64 extends Token {
    /**
     * The bytecode operation code for FloatToINT64.
     */
    public static final int OPCODE = 0x5d;

    private Token value;

    /**
     * Default constructor for FloatToINT64.
     */
    public FloatToINT64() {
    }

    /**
     * Constructs a FloatToINT64 conversion with the specified inner token.
     * * @param value The token value to be converted to INT64.
     */
    public FloatToINT64(Token value) {
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
        if (!(o instanceof FloatToINT64)) return false;
        FloatToINT64 that = (FloatToINT64) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "FloatToINT64(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the explicit cast.
     * <p>Formatted as "INT64(value)" to match UnrealScript explicit casting syntax.</p>
     * * @param context The runtime context for the Unreal engine.
     * @return The string formatted as "INT64(innerValue)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String valStr = (value == null) ? "null" : value.toString(context);
        return "INT64(" + valStr + ")";
    }
}