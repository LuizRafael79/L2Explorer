package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a boolean assignment operation (Opcode 0x14).
 * <p>A specialized version of the assignment operator used specifically 
 * for boolean expressions and variables within the Unreal VM.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class LetBool extends Token {
    /**
     * The bytecode operation code for LetBool.
     */
    public static final int OPCODE = 0x14;

    private Token left;
    private Token right;

    /**
     * Default constructor for LetBool.
     */
    public LetBool() {
    }

    /**
     * Constructs a LetBool token with the specified left and right boolean expressions.
     *
     * @param left The destination boolean expression (variable, property).
     * @param right The boolean value or expression to be assigned.
     */
    public LetBool(Token left, Token right) {
        this.left = left;
        this.right = right;
    }

    public Token getLeft() {
        return left;
    }

    public void setLeft(Token left) {
        this.left = left;
    }

    public Token getRight() {
        return right;
    }

    public void setRight(Token right) {
        this.right = right;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LetBool)) return false;
        LetBool letBool = (LetBool) o;
        return Objects.equals(left, letBool.left) && 
               Objects.equals(right, letBool.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "LetBool(" + left + ", " + right + ")";
    }

    /**
     * Returns the decompiler representation as a standard boolean assignment.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string "left = right".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String lStr = (left == null) ? "null" : left.toString(context);
        String rStr = (right == null) ? "null" : right.toString(context);
        return lStr + " = " + rStr;
    }
}