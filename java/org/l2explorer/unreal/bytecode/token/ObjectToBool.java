package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from an Object reference to a Boolean (Opcode 0x47).
 * <p>In UnrealScript, this is used to check if an object reference is valid.
 * It returns true if the reference is not 'None', and false otherwise.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
@ConversionToken
public class ObjectToBool extends Token {
    /**
     * The bytecode operation code for ObjectToBool.
     */
    public static final int OPCODE = 0x47;

    private Token value;

    /**
     * Default constructor for ObjectToBool.
     */
    public ObjectToBool() {
    }

    /**
     * Constructs an ObjectToBool conversion for the specified token.
     *
     * @param value The object reference token to be evaluated.
     */
    public ObjectToBool(Token value) {
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
        if (!(o instanceof ObjectToBool)) return false;
        ObjectToBool that = (ObjectToBool) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ObjectToBool(" + value + ")";
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