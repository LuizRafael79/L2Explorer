package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents an assignment operation (Opcode 0x0f).
 * <p>This token is used for the '=' operator in UnrealScript, connecting 
 * a left-hand side expression (target) with a right-hand side expression (value).</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class Let extends Token {
    /**
     * The bytecode operation code for Let.
     */
    public static final int OPCODE = 0x0f;

    private Token left;
    private Token right;

    /**
     * Default constructor for Let.
     */
    public Let() {
    }

    /**
     * Constructs a Let token with the specified left and right expressions.
     *
     * @param left The destination expression (variable, property).
     * @param right The value expression to be assigned.
     */
    public Let(Token left, Token right) {
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
        if (!(o instanceof Let)) return false;
        Let let = (Let) o;
        return Objects.equals(left, let.left) && 
               Objects.equals(right, let.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "Let(" + left + ", " + right + ")";
    }

    /**
     * Returns the decompiler representation as a standard assignment.
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