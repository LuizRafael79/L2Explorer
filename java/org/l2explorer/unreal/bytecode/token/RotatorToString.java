package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;
import java.util.Objects;

/**
 * Represents a conversion token that casts a Rotator value to a String.
 * <p>In UnrealScript, this conversion is typically used for logging or 
 * debugging, transforming the Pitch, Yaw, and Roll values into a readable format.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
@ConversionToken
public class RotatorToString extends Token {
    /**
     * The bytecode opcode for RotatorToString conversion.
     */
    public static final int OPCODE = 0x59;

    /**
     * The expression/token to be converted.
     */
    private Token value;

    /**
     * Default constructor for serialization and reflection.
     */
    public RotatorToString() {
    }

    /**
     * Constructs a RotatorToString token with the specified value.
     *
     * @param value The token expression to convert to string.
     */
    public RotatorToString(Token value) {
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
     * @return String formatted as RotatorToString(value).
     */
    @Override
    public String toString() {
        return "RotatorToString(" + value + ")";
    }

    /**
     * Returns the UnrealScript representation of this conversion.
     * <p>Note: In UnrealScript, explicit string casts for rotators often 
     * don't require the 'string()' keyword when used in string concatenation.</p>
     *
     * @param context The runtime context for deparsing.
     * @return The deparsed string representation of the value.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return value != null ? value.toString(context) : "";
    }

    /**
     * Compares this token to the specified object.
     *
     * @param o The object to compare with.
     * @return true if the objects are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RotatorToString that = (RotatorToString) o;
        return Objects.equals(value, that.value);
    }

    /**
     * Returns the hash code for this token.
     *
     * @return The hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}