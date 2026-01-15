package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import java.util.Objects;

public class MByteToINT64 extends Token {
    public static final int OPCODE = 0x5a;

    private Token value;

    public MByteToINT64() {
    }

    public MByteToINT64(Token value) {
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
        if (!(o instanceof MByteToINT64)) return false;
        MByteToINT64 that = (MByteToINT64) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ByteToINT64(" + value + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        // Mantendo o padrão int64 para o script descompilado
        return "int64(" + (value == null ? "null" : value.toString(context)) + ")";
    }
}