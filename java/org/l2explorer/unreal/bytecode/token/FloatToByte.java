package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a Float to a Byte (Opcode 0x43).
 * <p>Usually encountered in implicit assignments or specific math operations 
 * where a floating point value is clamped or truncated to a byte range (0-255).</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Pro (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
@ConversionToken
public class FloatToByte extends Token {
    /**
     * The bytecode operation code for FloatToByte.
     */
    public static final int OPCODE = 0x43;

    private Token value;

    /**
     * Default constructor for FloatToByte.
     */
    public FloatToByte() {
    }

    /**
     * Constructs a FloatToByte conversion with the specified inner token.
     * * @param value The token value to be converted.
     */
    public FloatToByte(Token value) {
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
        if (!(o instanceof FloatToByte)) return false;
        FloatToByte that = (FloatToByte) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "FloatToByte(" + value + ")";
    }

    /**
     * Returns the decompiler representation.
     * <p>This conversion is typically implicit in UnrealScript, so it returns 
     * only the string representation of the inner value.</p>
     * * @param context The runtime context for the Unreal engine.
     * @return The string representation of the inner value.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return value == null ? "null" : value.toString(context);
    }
}