package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import java.util.Objects;

/**
 * Represents a constant Vector literal (Opcode 0x23).
 * <p>In UnrealScript, a Vector is a struct containing three floating-point 
 * components (X, Y, Z), typically used for positions or directions in 3D space.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class VectorConst extends Token {
    /**
     * The bytecode opcode for a Vector constant.
     */
    public static final int OPCODE = 0x23;

    /** The X component of the vector. */
    private float x;
    /** The Y component of the vector. */
    private float y;
    /** The Z component of the vector. */
    private float z;

    /**
     * Default constructor for serialization and reflection.
     */
    public VectorConst() {
    }

    /**
     * Constructs a VectorConst with specified coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     */
    public VectorConst(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    /**
     * Returns a string representation of the token for debugging purposes.
     *
     * @return String formatted as VectorConst(x, y, z).
     */
    @Override
    public String toString() {
        return "VectorConst(" + x + ", " + y + ", " + z + ")";
    }

    /**
     * Returns the UnrealScript representation of this vector.
     *
     * @param context The runtime context for deparsing.
     * @return The formatted string "vect(x, y, z)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "vect(" + x + ", " + y + ", " + z + ")";
    }

    /**
     * Compares this token to the specified object.
     *
     * @param o The object to compare with.
     * @return true if all components are identical; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VectorConst that = (VectorConst) o;
        return Float.compare(that.x, x) == 0 && 
               Float.compare(that.y, y) == 0 && 
               Float.compare(that.z, z) == 0;
    }

    /**
     * Returns the hash code for this token.
     *
     * @return The hash code value based on X, Y, and Z.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}