package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;

import java.util.Objects;

@ConversionToken
public class ByteToFloat extends Token {
    public static final int OPCODE = 0x3c;

    private Token value;

    public ByteToFloat() {
    }

    public ByteToFloat(Token value) {
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
        if (!(o instanceof ByteToFloat)) return false;
        ByteToFloat that = (ByteToFloat) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ByteToFloat(" + value + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        // Conversão implícita: no código original do ACMI para ByteToFloat, 
        // ele apenas retorna a representação do valor.
        return value == null ? "null" : value.toString(context);
    }
}