package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a constant Rotator literal (Opcode 0x22).
 * <p>In UnrealScript, a Rotator is a struct used to store rotation 
 * information using integer units where 65536 equals 360 degrees.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class RotatorConst extends Token {
    /**
     * The bytecode operation code for RotatorConst.
     */
    public static final int OPCODE = 0x22;

    private int pitch;
    private int yaw;
    private int roll;

    /**
     * Default constructor for RotatorConst.
     */
    public RotatorConst() {
    }

    /**
     * Constructs a RotatorConst with specific Pitch, Yaw, and Roll values.
     *
     * @param pitch The rotation around the Y-axis.
     * @param yaw   The rotation around the Z-axis.
     * @param roll  The rotation around the X-axis.
     */
    public RotatorConst(int pitch, int yaw, int roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public int getYaw() {
        return yaw;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RotatorConst)) return false;
        RotatorConst that = (RotatorConst) o;
        return pitch == that.pitch && yaw == that.yaw && roll == that.roll;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pitch, yaw, roll);
    }

    @Override
    public String toString() {
        return String.format("RotatorConst(%d, %d, %d)", pitch, yaw, roll);
    }

    /**
     * Returns the decompiler representation of the rotator literal.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string "rot(pitch, yaw, roll)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return String.format("rot(%d, %d, %d)", pitch, yaw, roll);
    }
}