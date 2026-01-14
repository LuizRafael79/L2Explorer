package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a 32-bit Integer to a 64-bit Integer (Opcode 0x5b).
 * <p>This token performs a widening conversion, expanding a standard integer 
 * into the 64-bit space used by the INT64 type in Lineage II.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
@ConversionToken
public class IntToINT64 extends Token {
    /**
     * The bytecode operation code for IntToINT64.
     */
    public static final int OPCODE = 0x5b;

    private Token value;

    /**
     * Default constructor for IntToINT64.
     */
    public IntToINT64() {
    }

    /**
     * Constructs an IntToINT64 conversion with the specified inner token.
     *
     * @param value The 32-bit integer token to be promoted to 64-bit.
     */
    public IntToINT64(Token value) {
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
        if (!(o instanceof IntToINT64)) return false;
        IntToINT64 that = (IntToINT64) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "IntToINT64(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the value.
     * <p>This conversion is implicit in UnrealScript, so it returns 
     * the inner token's string representation without an explicit cast.</p>
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string representation of the inner value.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return (value == null) ? "null" : value.toString(context);
    }
}