package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;
import java.util.Objects;

/**
 * Represents a conversion token that casts a String value to a Rotator (Opcode 0x4E).
 * <p>In UnrealScript, this conversion attempts to parse a string into a Rotator struct.
 * The expected format is typically equivalent to the string representation of a rotator,
 * containing Pitch, Yaw, and Roll components.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
@ConversionToken
public class StringToRotator extends Token {
    /**
     * The bytecode opcode for StringToRotator conversion.
     */
    public static final int OPCODE = 0x4E;

    /**
     * The expression/token to be converted.
     */
    private Token value;

    /**
     * Default constructor for serialization and reflection.
     */
    public StringToRotator() {
    }

    /**
     * Constructs a StringToRotator token with the specified value.
     *
     * @param value The token expression to convert to rotator.
     */
    public StringToRotator(Token value) {
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
     * @return String formatted as StringToRotator(value).
     */
    @Override
    public String toString() {
        return "StringToRotator(" + value + ")";
    }

    /**
     * Returns the UnrealScript representation of this conversion.
     *
     * @param context The runtime context for deparsing.
     * @return The formatted conversion string, e.g., "rotator(StringValue)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "rotator(" + (value != null ? value.toString(context) : "") + ")";
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
        StringToRotator that = (StringToRotator) o;
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