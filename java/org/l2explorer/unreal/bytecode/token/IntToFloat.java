package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from an Integer to a Float (Opcode 0x3f).
 * <p>Used when an integer value is promoted to a floating-point number. 
 * This is typically an implicit operation in UnrealScript during 
 * mathematical evaluations.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
@ConversionToken
public class IntToFloat extends Token {
    /**
     * The bytecode operation code for IntToFloat.
     */
    public static final int OPCODE = 0x3f;

    private Token value;

    /**
     * Default constructor for IntToFloat.
     */
    public IntToFloat() {
    }

    /**
     * Constructs an IntToFloat conversion with the specified inner token.
     *
     * @param value The integer token to be converted to float.
     */
    public IntToFloat(Token value) {
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
        if (!(o instanceof IntToFloat)) return false;
        IntToFloat that = (IntToFloat) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "IntToFloat(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the value.
     * <p>Returns the inner token's string representation directly, 
     * as this cast is usually implicit in the source code.</p>
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string representation of the inner value.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return (value == null) ? "null" : value.toString(context);
    }
}