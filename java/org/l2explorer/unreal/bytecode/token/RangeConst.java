package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.ObjectRef;

import java.util.Objects;

/**
 * Represents a structure inequality comparison (Opcode 0x33).
 * <p>In UnrealScript, this token is used to compare two structs of the same type 
 * using the '!=' operator. It requires a reference to the struct definition 
 * to perform a member-by-member comparison.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class RangeConst extends Token {
    /**
     * The bytecode opcode for Struct inequality comparison.
     */
    public static final int OPCODE = 0x35;

    /**
     * Reference to the Struct object defining the layout.
     */
    @Compact
    @ObjectRef
    private int structRef;

    /**
     * The left-hand side expression of the comparison.
     */
    private Token left;

    /**
     * The right-hand side expression of the comparison.
     */
    private Token right;

    /**
     * Default constructor for serialization and reflection.
     */
    public RangeConst() {
    }

    /**
     * Constructs a StructCmpNe token with the specified structure reference and operands.
     *
     * @param structRef Reference to the struct type.
     * @param left The left operand.
     * @param right The right operand.
     */
    public RangeConst(int structRef, Token left, Token right) {
        this.structRef = structRef;
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    /**
     * Gets the struct reference index.
     *
     * @return The object reference index.
     */
    public int getStructRef() {
        return structRef;
    }

    /**
     * Sets the struct reference index.
     *
     * @param structRef The object reference index to set.
     */
    public void setStructRef(int structRef) {
        this.structRef = structRef;
    }

    /**
     * Gets the left-hand side token.
     *
     * @return The left operand.
     */
    public Token getLeft() {
        return left;
    }

    /**
     * Sets the left-hand side token.
     *
     * @param left The left operand to set.
     */
    public void setLeft(Token left) {
        this.left = left;
    }

    /**
     * Gets the right-hand side token.
     *
     * @return The right operand.
     */
    public Token getRight() {
        return right;
    }

    /**
     * Sets the right-hand side token.
     *
     * @param right The right operand to set.
     */
    public void setRight(Token right) {
        this.right = right;
    }

    /**
     * Returns a string representation of the token for debugging purposes.
     *
     * @return String formatted as StructCmpNe(structRef, left, right).
     */
    @Override
    public String toString() {
        return "StructCmpNe(" + structRef + ", " + left + ", " + right + ")";
    }

    /**
     * Returns the UnrealScript representation of this comparison.
     *
     * @param context The runtime context for deparsing.
     * @return The formatted comparison string, e.g., "LeftStruct != RightStruct".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String leftStr = (left != null) ? left.toString(context) : "None";
        String rightStr = (right != null) ? right.toString(context) : "None";
        return leftStr + " != " + rightStr;
    }

    /**
     * Compares this token to the specified object.
     *
     * @param o The object to compare with.
     * @return true if the struct references and operands are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RangeConst that = (RangeConst) o;
        return structRef == that.structRef &&
                Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    /**
     * Returns the hash code for this token.
     *
     * @return The hash code value based on structRef, left, and right.
     */
    @Override
    public int hashCode() {
        return Objects.hash(structRef, left, right);
    }
}