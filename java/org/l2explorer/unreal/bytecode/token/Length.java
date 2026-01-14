package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents the retrieval of a collection's size (Opcode 0x37).
 * <p>Commonly used for dynamic arrays in UnrealScript, translating 
 * the 'Length' property access into a bytecode instruction that 
 * returns the current element count.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class Length extends Token {
    /**
     * The bytecode operation code for Length.
     */
    public static final int OPCODE = 0x37;

    private Token value;

    /**
     * Default constructor for Length.
     */
    public Length() {
    }

    /**
     * Constructs a Length token with the specified expression.
     *
     * @param value The token representing the array or collection.
     */
    public Length(Token value) {
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
        if (!(o instanceof Length)) return false;
        Length length = (Length) o;
        return Objects.equals(value, length.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Length(" + value + ")";
    }

    /**
     * Returns the decompiler representation as a property access.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string "expression.Length".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String valStr = (value == null) ? "null" : value.toString(context);
        return valStr + ".Length";
    }
}