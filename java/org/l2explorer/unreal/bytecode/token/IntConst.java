package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a 32-bit signed integer constant (Opcode 0x1d).
 * <p>This token stores literal integer values used within the script bytecode.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class IntConst extends Token {
    /**
     * The bytecode operation code for IntConst.
     */
    public static final int OPCODE = 0x1d;

    private int value;

    /**
     * Default constructor for IntConst.
     */
    public IntConst() {
    }

    /**
     * Constructs an IntConst with a specific integer value.
     *
     * @param value The 32-bit integer value.
     */
    public IntConst(int value) {
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
        if (!(o instanceof IntConst)) return false;
        IntConst intConst = (IntConst) o;
        return value == intConst.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "IntConst(" + value + ")";
    }

    /**
     * Returns the literal integer value as a string for the decompiler.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string representation of the integer value.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return String.valueOf(value);
    }
}