package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a 64-bit Integer to a String (Opcode 0x63).
 * <p>This token is used to convert large 64-bit numeric values into a 
 * string representation, commonly for UI display, logging, or complex 
 * string concatenations in Lineage II.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
@ConversionToken
public class INT64ToString extends Token {
    /**
     * The bytecode operation code for INT64ToString.
     */
    public static final int OPCODE = 0x63;

    private Token value;

    /**
     * Default constructor for INT64ToString.
     */
    public INT64ToString() {
    }

    /**
     * Constructs an INT64ToString conversion with the specified inner token.
     *
     * @param value The INT64 token value to be converted to a string.
     */
    public INT64ToString(Token value) {
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
        if (!(o instanceof INT64ToString)) return false;
        INT64ToString that = (INT64ToString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "INT64ToString(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the explicit string cast.
     * <p>Formatted as "string(value)" to ensure correct type handling 
     * in the decompiled UnrealScript code.</p>
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string formatted as "string(innerValue)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String valStr = (value == null) ? "null" : value.toString(context);
        return "string(" + valStr + ")";
    }
}