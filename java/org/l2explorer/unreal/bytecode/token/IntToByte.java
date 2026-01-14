package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from an Integer to a Byte (Opcode 0x3d).
 * <p>This token is used for narrowing conversions where a 32-bit integer 
 * is truncated to an 8-bit byte (0-255). In UnrealScript, this is 
 * typically an implicit conversion.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
@ConversionToken
public class IntToByte extends Token {
    /**
     * The bytecode operation code for IntToByte.
     */
    public static final int OPCODE = 0x3d;

    private Token value;

    /**
     * Default constructor for IntToByte.
     */
    public IntToByte() {
    }

    /**
     * Constructs an IntToByte conversion with the specified inner token.
     *
     * @param value The integer token to be converted to byte.
     */
    public IntToByte(Token value) {
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
        if (!(o instanceof IntToByte)) return false;
        IntToByte intToByte = (IntToByte) o;
        return Objects.equals(value, intToByte.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "IntToByte(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the value.
     * <p>Since this conversion is usually implicit in UnrealScript, 
     * it returns the inner token's string representation directly.</p>
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string representation of the inner value.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return (value == null) ? "null" : value.toString(context);
    }
}