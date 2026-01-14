package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a Name to a String (Opcode 0x57).
 * <p>Used to transform an indexed name identifier into a character string.
 * This is typically an implicit operation in UnrealScript, such as when
 * concatenating a name literal with a string.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
@ConversionToken
public class NameToString extends Token {
    /**
     * The bytecode operation code for NameToString.
     */
    public static final int OPCODE = 0x57;

    private Token value;

    /**
     * Default constructor for NameToString.
     */
    public NameToString() {
    }

    /**
     * Constructs a NameToString conversion with the specified inner token.
     *
     * @param value The name token to be converted to a string.
     */
    public NameToString(Token value) {
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
        if (!(o instanceof NameToString)) return false;
        NameToString that = (NameToString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "NameToString(" + value + ")";
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