package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;
import java.util.Objects;

/**
 * Token de conversão de Byte para String (Opcode 0x52).
 */
@ConversionToken
public class ByteToString extends Token {
    public static final int OPCODE = 0x52;

    private Token value;

    public ByteToString() {
    }

    public ByteToString(Token value) {
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
        if (!(o instanceof ByteToString)) return false;
        ByteToString that = (ByteToString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ByteToString(" + value + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        // Conversão implícita para string em expressões de concatenação
        return value == null ? "null" : value.toString(context);
    }
}