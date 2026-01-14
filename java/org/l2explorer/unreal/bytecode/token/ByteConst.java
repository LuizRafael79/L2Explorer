package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.io.annotation.UByte;
import org.l2explorer.unreal.UnrealRuntimeContext;
import java.util.Objects;

/**
 * Token de Constante de Byte (Opcode 0x24).
 * Representa um valor numérico literal de 1 byte no bytecode.
 */
public class ByteConst extends Token {
    public static final int OPCODE = 0x24;

    @UByte
    private int value;

    // Construtor sem argumentos
    public ByteConst() {
    }

    // Construtor com argumentos
    public ByteConst(int value) {
        this.value = value;
    }

    // Getters e Setters
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteConst)) return false;
        ByteConst byteConst = (ByteConst) o;
        return value == byteConst.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ByteConst(" + value + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        return String.valueOf(value);
    }
}