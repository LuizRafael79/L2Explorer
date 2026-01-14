package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Token de Tabela de Conversão (Opcode 0x39).
 * Atua como um wrapper para operações de conversão de tipos.
 */
public class ConversionTable extends Token {
    public static final int OPCODE = 0x39;

    private Token inner;

    public ConversionTable() {
    }

    public ConversionTable(Token inner) {
        this.inner = inner;
    }

    public Token getInner() {
        return inner;
    }

    public void setInner(Token inner) {
        this.inner = inner;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversionTable)) return false;
        ConversionTable that = (ConversionTable) o;
        return Objects.equals(inner, that.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inner);
    }

    @Override
    public String toString() {
        return "ConversionTable(" + inner + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        return inner == null ? "null" : inner.toString(context);
    }
}