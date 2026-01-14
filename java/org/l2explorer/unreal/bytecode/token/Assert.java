package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.io.annotation.UShort;
import org.l2explorer.unreal.UnrealRuntimeContext;
import java.util.Objects;

/**
 * Token de Assert (Opcode 0x09).
 * Representa uma validação de linha e expressão no bytecode da Unreal.
 */
public class Assert extends Token {
    public static final int OPCODE = 0x09;

    @UShort
    private int lineNumber;
    private Token expression;

    // Construtor padrão (NoArgsConstructor)
    public Assert() {
    }

    // Construtor completo (AllArgsConstructor)
    public Assert(int lineNumber, Token expression) {
        this.lineNumber = lineNumber;
        this.expression = expression;
    }

    // Getters e Setters
    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Token getExpression() {
        return expression;
    }

    public void setExpression(Token expression) {
        this.expression = expression;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assert)) return false;
        Assert that = (Assert) o;
        return lineNumber == that.lineNumber && Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, expression);
    }

    @Override
    public String toString() {
        return "Assert(" + lineNumber + ", " + expression + ')';
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        String exprStr = (expression == null) ? "null" : expression.toString(context);
        return "assert(" + exprStr + ")";
    }
}