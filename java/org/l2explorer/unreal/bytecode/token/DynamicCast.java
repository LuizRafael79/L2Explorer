package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.ObjectRef;

/**
 * Token de Cast Dinâmico (Opcode 0x2e).
 * Tenta converter um objeto para uma classe específica: ClassName(Value).
 */
public class DynamicCast extends Token {
    public static final int OPCODE = 0x2e;

    @Compact
    @ObjectRef
    private int classRef;
    
    private Token value;

    public DynamicCast() {
    }

    public DynamicCast(int classRef, Token value) {
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
        if (!(o instanceof DynamicCast)) return false;
        DynamicCast that = (DynamicCast) o;
        return classRef == that.classRef && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classRef, value);
    }

    @Override
    public String toString() {
        return "DynamicCast(" + classRef + ", " + value + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        try {
            String className = context.getUnrealPackage()
                    .objectReference(classRef)
                    .getObjectName()
                    .getName();
            
            String valStr = (value == null) ? "null" : value.toString(context);
            
            return className + "(" + valStr + ")";
        } catch (Exception e) {
            return "UnknownClass_" + classRef + "(" + (value == null ? "null" : value.toString(context)) + ")";
        }
    }
}