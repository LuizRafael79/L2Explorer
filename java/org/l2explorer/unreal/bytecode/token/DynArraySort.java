package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Token de Ordenação de Array Dinâmico (Opcode 0x47).
 * Introduzido em versões mais recentes (ex: Grand Crusade).
 * Sintaxe: array.Sort(compareFunction).
 */
public class DynArraySort extends Token {
    public static final int OPCODE = 0x47;

    private Token array;
    private Token compareFunction;

    public DynArraySort() {
    }

    public DynArraySort(Token array, Token compareFunction) {
        this.array = array;
        this.compareFunction = compareFunction;
    }

    public Token getArray() {
        return array;
    }

    public void setArray(Token array) {
        this.array = array;
    }

    public Token getCompareFunction() {
        return compareFunction;
    }

    public void setCompareFunction(Token compareFunction) {
        this.compareFunction = compareFunction;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DynArraySort)) return false;
        DynArraySort that = (DynArraySort) o;
        return Objects.equals(array, that.array) && 
               Objects.equals(compareFunction, that.compareFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(array, compareFunction);
    }

    @Override
    public String toString() {
        return "DynArraySort(" + array + ", " + compareFunction + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        String arrayStr = (array == null) ? "null" : array.toString(context);
        String funcStr = (compareFunction == null) ? "null" : compareFunction.toString(context);
        
        return arrayStr + ".Sort(" + funcStr + ")";
    }
}