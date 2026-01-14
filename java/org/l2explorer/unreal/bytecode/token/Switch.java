package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.io.annotation.UByte;
import org.l2explorer.unreal.UnrealRuntimeContext;

import java.util.Objects;

/**
 * Represents a Switch control structure (Opcode 0x05).
 * <p>In UnrealScript, the switch statement evaluates an expression and 
 * branches execution to a corresponding 'case' or 'default' label.
 * This token includes the size of the expression and the expression itself.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class Switch extends Token {
    /**
     * The bytecode opcode for the Switch statement.
     */
    public static final int OPCODE = 0x05;

    /**
     * The size of the expression in bytes (e.g., 1 for byte, 4 for int, 8 for int64).
     */
    @UByte
    private int offset;

    /**
     * The expression to be evaluated by the switch statement.
     */
    private Token expression;

    /**
     * Default constructor for serialization and reflection.
     */
    public Switch() {
    }

    /**
     * Constructs a Switch token with the specified expression size and expression.
     *
     * @param offset The size of the expression in bytes.
     * @param expression The token representing the expression to switch on.
     */
    public Switch(int offset, Token expression) {
        this.offset = offset;
        this.expression = expression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    /**
     * Gets the expression size offset.
     *
     * @return The size of the expression in bytes.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets the expression size offset.
     *
     * @param offset The size of the expression to set.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Gets the switch expression.
     *
     * @return The expression token.
     */
    public Token getExpression() {
        return expression;
    }

    /**
     * Sets the switch expression.
     *
     * @param expression The expression token to set.
     */
    public void setExpression(Token expression) {
        this.expression = expression;
    }

    /**
     * Returns a string representation of the token for debugging purposes.
     *
     * @return String formatted as Switch(offset, expression).
     */
    @Override
    public String toString() {
        return "Switch(" + offset + ", " + expression + ")";
    }

    /**
     * Returns the UnrealScript representation of this switch statement.
     *
     * @param context The runtime context for deparsing.
     * @return The formatted switch string, e.g., "switch(Expression)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String exprStr = (expression != null) ? expression.toString(context) : "";
        return "switch(" + exprStr + ")";
    }

    /**
     * Compares this token to the specified object.
     *
     * @param o The object to compare with.
     * @return true if the offsets and expressions are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Switch aSwitch = (Switch) o;
        return offset == aSwitch.offset && Objects.equals(expression, aSwitch.expression);
    }

    /**
     * Returns the hash code for this token.
     *
     * @return The hash code value based on offset and expression.
     */
    @Override
    public int hashCode() {
        return Objects.hash(offset, expression);
    }
}