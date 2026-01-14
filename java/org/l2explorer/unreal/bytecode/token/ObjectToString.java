package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from an Object reference to a String (Opcode 0x56).
 * <p>This token transforms an object reference into its string representation, 
 * which usually corresponds to the object's full path or name within the package.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
@ConversionToken
public class ObjectToString extends Token {
    /**
     * The bytecode operation code for ObjectToString.
     */
    public static final int OPCODE = 0x56;

    private Token value;

    /**
     * Default constructor for ObjectToString.
     */
    public ObjectToString() {
    }

    /**
     * Constructs an ObjectToString conversion for the specified token.
     *
     * @param value The object reference token to be converted.
     */
    public ObjectToString(Token value) {
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
        if (!(o instanceof ObjectToString)) return false;
        ObjectToString that = (ObjectToString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ObjectToString(" + value + ")";
    }

    /**
     * Returns the decompiler representation as an explicit string cast.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string "string(value)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String valStr = (value == null) ? "null" : value.toString(context);
        return "string(" + valStr + ")";
    }
}