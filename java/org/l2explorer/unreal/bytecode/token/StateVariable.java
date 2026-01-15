package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.ObjectRef;

/**
 * Representa uma referência de variável de objeto (Opcode 0x03).
 * Este token é essencial para o alinhamento do bytecode.
 */
public class StateVariable extends Token {
    public static final int OPCODE = 0x03;

    @ObjectRef
    private int objectIndex;

    public StateVariable() {
    }

    public int getObjectIndex() {
        return objectIndex;
    }

    public void setObjectIndex(int objectIndex) {
        this.objectIndex = objectIndex;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        if (objectIndex == 0) {
            return "None";
        }
        
        try {
            Object entry = context.getUnrealPackage().objectReference(objectIndex);
            
            if (entry == null) {
                return "None";
            }

            return String.valueOf(entry);
        } catch (Exception e) {
            return "UnknownObject_" + objectIndex;
        }
    }
}