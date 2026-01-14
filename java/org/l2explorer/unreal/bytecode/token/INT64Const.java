package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a 64-bit integer constant (Opcode 0x46).
 * <p>This token stores large integer values by splitting them into two 32-bit components (high and low).
 * Essential for handling large quantities like Adena, XP, or timestamps in Lineage II.</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
public class INT64Const extends Token {
    /**
     * The bytecode operation code for INT64Const.
     */
    public static final int OPCODE = 0x46;

    private int h;
    private int l;

    /**
     * Default constructor for INT64Const.
     */
    public INT64Const() {
    }

    /**
     * Constructs an INT64Const from a long value by splitting it into 
     * high (32-bit) and low (32-bit) integers.
     * * @param value The 64-bit long value.
     */
    public INT64Const(long value) {
        this.h = (int) (value >> 32);
        this.l = (int) (value & 0xFFFFFFFFL);
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    /**
     * Reconstructs the 64-bit long value from the high and low 32-bit parts.
     * * @return The combined 64-bit long value.
     */
    public long getValue() {
        return (((long) h) << 32) | (l & 0xFFFFFFFFL);
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof INT64Const)) return false;
        INT64Const that = (INT64Const) o;
        return h == that.h && l == that.l;
    }

    @Override
    public int hashCode() {
        return Objects.hash(h, l);
    }

    @Override
    public String toString() {
        return "INT64Const(" + getValue() + ")";
    }

    /**
     * Returns the string representation of the 64-bit value for the decompiler.
     * * @param context The runtime context for the Unreal engine.
     * @return The 64-bit value as a string.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return String.valueOf(getValue());
    }
}