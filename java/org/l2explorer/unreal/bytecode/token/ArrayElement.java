package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import java.util.Objects;

/**
 * Token de Elemento de Array (Opcode 0x1a).
 * Representa o acesso a um índice: array[index].
 */
public class ArrayElement extends Token {
    public static final int OPCODE = 0x1a;

    private Token index;
    private Token array;

    // Construtor sem argumentos
    public ArrayElement() {
    }

    // Construtor com argumentos (Substituindo AllArgsConstructor)
    public ArrayElement(Token index, Token array) {
        this.index = index;
        this.array = array;
    }

    // Getters e Setters manuais
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
        if (!(o instanceof ArrayElement)) return false;
        ArrayElement that = (ArrayElement) o;
        return Objects.equals(index, that.index) && 
               Objects.equals(array, that.array);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, array);
    }

    @Override
    public String toString() {
        return "ArrayElement(" + index + ", " + array + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        String arrayStr = (array == null) ? "null" : array.toString(context);
        String indexStr = (index == null) ? "null" : index.toString(context);
        
        return arrayStr + "[" + indexStr + "]";
    }
}