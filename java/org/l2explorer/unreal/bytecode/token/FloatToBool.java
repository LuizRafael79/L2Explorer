package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

/**
 * Represents a conversion from a Float to a Boolean (Opcode 0x45).
 * <p>In UnrealScript, any non-zero float value is generally considered true,
 * while 0.0 is considered false when cast to a boolean.</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Pro (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
@ConversionToken
public class FloatToBool extends Token {
    /**
     * The bytecode operation code for FloatToBool.
     */
    public static final int OPCODE = 0x45;

    private Token value;

    /**
     * Default constructor for FloatToBool.
     */
    public FloatToBool() {
    }

    /**
     * Constructs a FloatToBool conversion with the specified inner token.
     * * @param value The token value to be converted.
     */
    public FloatToBool(Token value) {
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
        if (!(o instanceof FloatToBool)) return false;
        FloatToBool that = (FloatToBool) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "FloatToBool(" + value + ")";
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