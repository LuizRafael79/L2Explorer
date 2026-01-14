package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Token de Elemento de Array Dinâmico (Opcode 0x10).
 * Representa o acesso a um índice em um array dinâmico: array[index].
 */
public class DynArrayElement extends Token {
    public static final int OPCODE = 0x10;

    private Token index;
    private Token array;

    public DynArrayElement() {
    }

    public DynArrayElement(Token index, Token array) {
        this.index = index;
        this.array = array;
    }

    public Token getIndex() {
        return index;
    }

    public void setIndex(Token index) {
        this.index = index;
    }

    public Token getArray() {
        return array;
    }

    public void setArray(Token array) {
        this.array = array;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DynArrayElement)) return false;
        DynArrayElement that = (DynArrayElement) o;
        return Objects.equals(index, that.index) && 
               Objects.equals(array, that.array);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, array);
    }

    @Override
    public String toString() {
        return "DynArrayElement(" + index + ", " + array + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        // Prevenção de NullPointerException para garantir que o log de descompilação não quebre
        String arrayStr = (array == null) ? "null" : array.toString(context);
        String indexStr = (index == null) ? "null" : index.toString(context);
        
        return arrayStr + "[" + indexStr + "]";
    }
}