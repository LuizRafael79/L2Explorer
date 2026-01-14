package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;
import java.util.Objects;

/**
 * Represents a conversion token that casts a Vector value to a String (Opcode 0x58).
 * <p>In UnrealScript, this conversion formats the X, Y, and Z components 
 * of a vector into a human-readable string representation.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
@ConversionToken
public class VectorToString extends Token {
    /**
     * The bytecode opcode for VectorToString conversion.
     */
    public static final int OPCODE = 0x58;

    /**
     * The vector expression/token to be converted.
     */
    private Token value;

    /**
     * Default constructor for serialization and reflection.
     */
    public VectorToString() {
    }

    /**
     * Constructs a VectorToString token with the specified value.
     *
     * @param value The token expression to convert to string.
     */
    public VectorToString(Token value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    /**
     * Gets the token value being converted.
     *
     * @return The value expression.
     */
    public Token getValue() {
        return value;
    }

    /**
     * Sets the token value to be converted.
     *
     * @param value The value expression to set.
     */
    public void setValue(Token value) {
        this.value = value;
    }

    /**
     * Returns a string representation of the token for debugging purposes.
     *
     * @return String formatted as VectorToString(value).
     */
    @Override
    public String toString() {
        return "VectorToString(" + value + ")";
    }

    /**
     * Returns the UnrealScript representation of this conversion.
     * <p>Note: Vector to String conversion in UnrealScript is often implicit 
     * in deparsing contexts.</p>
     *
     * @param context The runtime context for deparsing.
     * @return The string representation of the inner value.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return value != null ? value.toString(context) : "";
    }

    /**
     * Compares this token to the specified object.
     *
     * @param o The object to compare with.
     * @return true if the inner tokens are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VectorToString that = (VectorToString) o;
        return Objects.equals(value, that.value);
    }

    /**
     * Returns the hash code for this token.
     *
     * @return The hash code value based on the inner value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}