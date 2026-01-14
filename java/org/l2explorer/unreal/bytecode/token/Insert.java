package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a dynamic array insert operation (Opcode 0x40).
 * <p>This token is used to insert one or more elements into a dynamic array 
 * at a specific index. Syntax in UnrealScript: array.Insert(index, length).</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Pro (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
public class Insert extends Token {
    /**
     * The bytecode operation code for Insert.
     */
    public static final int OPCODE = 0x40;

    private Token value;
    private Token index;
    private Token length;

    /**
     * Default constructor for Insert.
     */
    public Insert() {
    }

    /**
     * Constructs an Insert token with array, index, and number of elements.
     * * @param value  The array token.
     * @param index  The starting index for insertion.
     * @param length The number of elements to insert.
     */
    public Insert(Token value, Token index, Token length) {
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
        if (!(o instanceof Insert)) return false;
        Insert insert = (Insert) o;
        return Objects.equals(value, insert.value) && 
               Objects.equals(index, insert.index) && 
               Objects.equals(length, insert.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, index, length);
    }

    @Override
    public String toString() {
        return "Insert(" + value + ", " + index + ", " + length + ")";
    }

    /**
     * Returns the decompiler representation of the array insert operation.
     * * @param context The runtime context for the Unreal engine.
     * @return Formatted string as "array.Insert(index, length)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String valStr = (value == null) ? "null" : value.toString(context);
        String idxStr = (index == null) ? "0" : index.toString(context);
        String lenStr = (length == null) ? "0" : length.toString(context);
        
        return valStr + ".Insert(" + idxStr + ", " + lenStr + ")";
    }
}