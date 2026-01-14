package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.ObjectRef;

/**
 * Represents a metaclass cast operation (Opcode 0x13).
 * <p>Used to cast a class reference to a specific metaclass type. 
 * Formatted in UnrealScript as: class&lt;ClassName&gt;(value).</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class Metacast extends Token {
    /**
     * The bytecode operation code for Metacast.
     */
    public static final int OPCODE = 0x13;

    @Compact
    @ObjectRef
    private int classRef;

    private Token value;

    /**
     * Default constructor for Metacast.
     */
    public Metacast() {
    }

    /**
     * Constructs a Metacast with the target class reference and the expression to cast.
     *
     * @param classRef The index in the object table for the target class.
     * @param value The expression/token being cast.
     */
    public Metacast(int classRef, Token value) {
        this.classRef = classRef;
        this.value = value;
    }

    public int getClassRef() {
        return classRef;
    }

    public void setClassRef(int classRef) {
        this.classRef = classRef;
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
        if (!(o instanceof Metacast)) return false;
        Metacast metacast = (Metacast) o;
        return classRef == metacast.classRef && 
               Objects.equals(value, metacast.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classRef, value);
    }

    @Override
    public String toString() {
        return "Metacast(" + classRef + ", " + value + ")";
    }

    /**
     * Returns the decompiler representation as a metaclass cast.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string "class&lt;ClassName&gt;(value)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String className = context.getUnrealPackage().objectReference(classRef).getObjectName().getName();
        String valStr = (value == null) ? "null" : value.toString(context);
        return "class<" + className + ">(" + valStr + ")";
    }
}