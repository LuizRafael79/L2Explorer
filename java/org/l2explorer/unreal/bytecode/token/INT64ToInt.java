package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a 64-bit Integer to a 32-bit Integer (Opcode 0x60).
 * <p>This token performs a narrowing conversion by truncating the 64-bit value 
 * to its 32-bit integer equivalent. Used when high-precision numeric data 
 * must be passed to functions or properties limited to 32-bit registers.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
@ConversionToken
public class INT64ToInt extends Token {
    /**
     * The bytecode operation code for INT64ToInt.
     */
    public static final int OPCODE = 0x60;

    private Token value;

    /**
     * Default constructor for INT64ToInt.
     */
    public INT64ToInt() {
    }

    /**
     * Constructs an INT64ToInt conversion with the specified inner token.
     * * @param value The INT64 token value to be truncated.
     */
    public INT64ToInt(Token value) {
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
        if (!(o instanceof INT64ToInt)) return false;
        INT64ToInt that = (INT64ToInt) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "INT64ToInt(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the explicit integer cast.
     * * @param context The runtime context for the Unreal engine.
     * @return The string formatted as "int(value)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String valStr = (value == null) ? "null" : value.toString(context);
        return "int(" + valStr + ")";
    }
}