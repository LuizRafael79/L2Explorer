package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.UShort;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.Offset;

/**
 * Represents an iterator start (Opcode 0x2f), used by the 'ForEach' command.
 * <p>This token manages the beginning of a loop that iterates over a collection 
 * or a set of actors. It contains an expression to evaluate and an offset 
 * to jump to when the loop terminates.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class Iterator extends Token {
    /**
     * The bytecode operation code for Iterator.
     */
    public static final int OPCODE = 0x2f;

    private Token expression;

    @UShort
    @Offset
    private int endOfLoopOffset;

    /**
     * Default constructor for Iterator.
     */
    public Iterator() {
    }

    /**
     * Constructs an Iterator with the given expression and loop termination offset.
     *
     * @param expression The iterator function call or expression.
     * @param endOfLoopOffset The offset in the bytecode to jump to after the loop.
     */
    public Iterator(Token expression, int endOfLoopOffset) {
        this.expression = expression;
        this.endOfLoopOffset = endOfLoopOffset;
    }

    public Token getExpression() {
        return expression;
    }

    public void setExpression(Token expression) {
        this.expression = expression;
    }

    public int getEndOfLoopOffset() {
        return endOfLoopOffset;
    }

    public void setEndOfLoopOffset(int endOfLoopOffset) {
        this.endOfLoopOffset = endOfLoopOffset;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Iterator)) return false;
        Iterator iterator = (Iterator) o;
        return endOfLoopOffset == iterator.endOfLoopOffset && 
               Objects.equals(expression, iterator.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, endOfLoopOffset);
    }

    @Override
    public String toString() {
        return String.format("Iterator(%s, 0x%04x)", expression, endOfLoopOffset);
    }

    /**
     * Returns the decompiler representation of the 'ForEach' statement.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The string "ForEach " followed by the iterator expression.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String exprStr = (expression == null) ? "null" : expression.toString(context);
        return "ForEach " + exprStr;
    }
}