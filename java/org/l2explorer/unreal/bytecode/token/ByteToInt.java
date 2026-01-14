package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;
import java.util.Objects;

@ConversionToken
public class ByteToInt extends Token {
    public static final int OPCODE = 0x3a;

    private Token value;

    public ByteToInt() {
    }

    public ByteToInt(Token value) {
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
        if (!(o instanceof ByteToInt)) return false;
        ByteToInt that = (ByteToInt) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ByteToInt(" + value + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        // Conversão implícita: retorna apenas o valor sem cast visual
        return value == null ? "null" : value.toString(context);
    }
}