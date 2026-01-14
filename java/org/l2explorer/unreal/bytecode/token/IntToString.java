package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from an Integer to a String (Opcode 0x53).
 * <p>This token is typically used for implicit string conversions in UnrealScript,
 * allowing integer values to be concatenated with strings or passed to string parameters.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
@ConversionToken
public class IntToString extends Token {
    /**
     * The bytecode operation code for IntToString.
     */
    public static final int OPCODE = 0x53;

    private Token value;

    /**
     * Default constructor for IntToString.
     */
    public IntToString() {
    }

    /**
     * Constructs an IntToString conversion with the specified inner token.
     *
     * @param value The integer token to be converted to a string.
     */
    public IntToString(Token value) {
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
        if (!(o instanceof IntToString)) return false;
        IntToString that = (IntToString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "IntToString(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the value.
     * <p>Returns the inner token's string representation directly, 
     * as this conversion is usually implicit in UnrealScript.</p>
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string representation of the inner value.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return (value == null) ? "null" : value.toString(context);
    }
}