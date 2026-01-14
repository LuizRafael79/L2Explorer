package org.l2explorer.unreal.bytecode.token;

import java.util.Locale;
import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a 32-bit floating point constant (Opcode 0x1e).
 * <p>Used for literal float values within the UnrealScript bytecode.</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Pro (Reimplementation)
 * * @since 12-01-2026
 */
public class FloatConst extends Token {
    /**
     * The bytecode operation code for FloatConst.
     */
    public static final int OPCODE = 0x1e;

    private float value;

    /**
     * Default constructor for FloatConst.
     */
    public FloatConst() {
    }

    /**
     * Constructs a FloatConst with the specified float value.
     * * @param value The floating point value.
     */
    public FloatConst(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FloatConst)) return false;
        FloatConst that = (FloatConst) o;
        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "FloatConst(" + value + ")";
    }

    /**
     * Returns the float value formatted for UnrealScript.
     * <p>Ensures the use of US Locale to maintain the dot (.) decimal separator.</p>
     * * @param context The runtime context.
     * @return String representation of the float.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return String.format(Locale.US, "%s", value);
    }
}