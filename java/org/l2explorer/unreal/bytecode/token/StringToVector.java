package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;
import java.util.Objects;

/**
 * Represents a conversion token that casts a String value to a Vector (Opcode 0x4D).
 * <p>In UnrealScript, this conversion parses a string into a Vector struct.
 * The input string is expected to contain numeric components for X, Y, and Z,
 * typically separated by commas or within the standard vector string format.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
@ConversionToken
public class StringToVector extends Token {
    /**
     * The bytecode opcode for StringToVector conversion.
     */
    public static final int OPCODE = 0x4D;

    /**
     * The expression/token to be converted.
     */
    private Token value;

    /**
     * Default constructor for serialization and reflection.
     */
    public StringToVector() {
    }

    /**
     * Constructs a StringToVector token with the specified value.
     *
     * @param value The token expression to convert to vector.
     */
    public StringToVector(Token value) {
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
     * @return String formatted as StringToVector(value).
     */
    @Override
    public String toString() {
        return "StringToVector(" + value + ")";
    }

    /**
     * Returns the UnrealScript representation of this conversion.
     *
     * @param context The runtime context for deparsing.
     * @return The formatted conversion string, e.g., "vector(StringValue)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "vector(" + (value != null ? value.toString(context) : "") + ")";
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
        StringToVector that = (StringToVector) o;
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