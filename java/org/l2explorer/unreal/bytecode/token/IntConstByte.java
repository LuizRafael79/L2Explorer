package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.UByte;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a constant integer value stored as a single byte (Opcode 0x2c).
 * <p>This is a space-saving optimization for integer literals between 0 and 255.
 * The VM reads one unsigned byte and treats it as a 32-bit integer internally.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class IntConstByte extends Token {
    /**
     * The bytecode operation code for IntConstByte.
     */
    public static final int OPCODE = 0x2c;

    @UByte
    private int value;

    /**
     * Default constructor for IntConstByte.
     */
    public IntConstByte() {
    }

    /**
     * Constructs an IntConstByte with the specified byte-sized value.
     *
     * @param value The integer value (internally read as a byte).
     */
    public IntConstByte(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntConstByte)) return false;
        IntConstByte that = (IntConstByte) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "IntConstByte(" + value + ")";
    }

    /**
     * Returns the literal string representation for the decompiler.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string representation of the value.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return String.valueOf(value);
    }
}