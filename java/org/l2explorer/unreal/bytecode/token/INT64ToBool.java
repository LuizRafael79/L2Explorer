package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a 64-bit Integer to a Boolean (Opcode 0x61).
 * <p>Used when a large integer value needs to be evaluated in a conditional 
 * context (e.g., if(AdenaAmount) ...). Any non-zero value returns true.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
@ConversionToken
public class INT64ToBool extends Token {
    /**
     * The bytecode operation code for INT64ToBool.
     */
    public static final int OPCODE = 0x61;

    private Token value;

    /**
     * Default constructor for INT64ToBool.
     */
    public INT64ToBool() {
    }

    /**
     * Constructs an INT64ToBool conversion with the specified inner token.
     * * @param value The INT64 token value to be evaluated as a boolean.
     */
    public INT64ToBool(Token value) {
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
        if (!(o instanceof INT64ToBool)) return false;
        INT64ToBool that = (INT64ToBool) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "INT64ToBool(" + value + ")";
    }

    /**
     * Returns the decompiler representation of the explicit cast.
     * * @param context The runtime context for the Unreal engine.
     * @return The string formatted as "bool(value)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String valStr = (value == null) ? "null" : value.toString(context);
        return "bool(" + valStr + ")";
    }
}