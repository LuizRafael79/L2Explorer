package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a Name to a Boolean (Opcode 0x48).
 * <p>In the Unreal VM, this evaluates whether a Name literal or variable 
 * is non-empty. It returns true if the name is not 'None', and false otherwise.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
@ConversionToken
public class NameToBool extends Token {
    /**
     * The bytecode operation code for NameToBool.
     */
    public static final int OPCODE = 0x48;

    private Token value;

    /**
     * Default constructor for NameToBool.
     */
    public NameToBool() {
    }

    /**
     * Constructs a NameToBool conversion with the specified inner token.
     *
     * @param value The name token to be converted to boolean.
     */
    public NameToBool(Token value) {
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
        if (!(o instanceof NameToBool)) return false;
        NameToBool that = (NameToBool) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "NameToBool(" + value + ")";
    }

    /**
     * Returns the decompiler representation as an explicit boolean cast.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string "bool(value)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String valStr = (value == null) ? "null" : value.toString(context);
        return "bool(" + valStr + ")";
    }
}