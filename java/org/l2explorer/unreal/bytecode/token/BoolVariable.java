package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import java.util.Objects;

/**
 * Token de Variável Booleana (Opcode 0x2d).
 */
public class BoolVariable extends Token {
    public static final int OPCODE = 0x2d;

    private Token value;

    public BoolVariable() {
    }

    public BoolVariable(Token value) {
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
        if (!(o instanceof BoolVariable)) return false;
        BoolVariable that = (BoolVariable) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BoolVariable(" + value + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        return value == null ? "null" : value.toString(context);
    }
}