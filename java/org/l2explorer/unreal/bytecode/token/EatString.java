package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Token EatString (Opcode 0x0e).
 * Consome uma string da pilha de execução.
 */
public class EatString extends Token {
    public static final int OPCODE = 0x0e;

    private Token value;

    public EatString() {
    }

    public EatString(Token value) {
        this.value = value;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EatString)) return false;
        EatString eatString = (EatString) o;
        return Objects.equals(value, eatString.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "EatString(" + value + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        // Assim como nos casos de conversão implícita, apenas retornamos o conteúdo
        return value == null ? "null" : value.toString(context);
    }
}