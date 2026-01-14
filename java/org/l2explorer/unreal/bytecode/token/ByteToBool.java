package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;
import java.util.Objects;

/**
 * Token de conversão de Byte para Booleano (Opcode 0x3B).
 */
@ConversionToken
public class ByteToBool extends Token {
    public static final int OPCODE = 0x3b;

    private Token value;

    public ByteToBool() {
    }

    public ByteToBool(Token value) {
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
        if (!(o instanceof ByteToBool)) return false;
        ByteToBool that = (ByteToBool) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ByteToBool(" + value + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        return "bool(" + (value == null ? "null" : value.toString(context)) + ")";
    }
}