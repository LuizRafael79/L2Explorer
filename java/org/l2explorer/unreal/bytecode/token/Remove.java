package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents the removal of elements from a dynamic array (Opcode 0x41).
 * <p>This token corresponds to the 'Array.Remove(Index, Count)' operation 
 * in UnrealScript, which removes a specified number of elements starting 
 * from a given index and collapses the remaining elements.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class Remove extends Token {
    /**
     * The bytecode operation code for Remove.
     */
    public static final int OPCODE = 0x41;

    private Token value;
    private Token index;
    private Token length;

    /**
     * Default constructor for Remove.
     */
    public Remove() {
    }

    /**
     * Constructs a Remove token with the target array, starting index, and count.
     *
     * @param value  The token representing the dynamic array.
     * @param index  The token representing the starting index.
     * @param length The token representing the number of elements to remove.
     */
    public Remove(Token value, Token index, Token length) {
        this.value = value;
        this.index = index;
        this.length = length;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }

    public Token getIndex() {
        return index;
    }

    public void setIndex(Token index) {
        this.index = index;
    }

    public Token getLength() {
        return length;
    }

    public void setLength(Token length) {
        this.length = length;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Remove)) return false;
        Remove remove = (Remove) o;
        return Objects.equals(value, remove.value) && 
               Objects.equals(index, remove.index) && 
               Objects.equals(length, remove.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, index, length);
    }

    @Override
    public String toString() {
        return String.format("Remove(%s, %s, %s)", value, index, length);
    }

    /**
     * Returns the decompiler representation as an array method call.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string "Array.Remove(Index, Length)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String arrayStr = (value == null) ? "null" : value.toString(context);
        String idxStr = (index == null) ? "0" : index.toString(context);
        String lenStr = (length == null) ? "0" : length.toString(context);
        
        return String.format("%s.Remove(%s, %s)", arrayStr, idxStr, lenStr);
    }
}