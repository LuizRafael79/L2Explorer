package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.ObjectRef;

/**
 * Represents a reference to an instance variable (Opcode 0x01).
 * <p>This token is used to access fields or properties of an object instance.
 * It points to an entry in the object table that defines the property.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
public class InstanceVariable extends Token {
    /**
     * The bytecode operation code for InstanceVariable.
     */
    public static final int OPCODE = 0x01;

    @Compact
    @ObjectRef
    private int objRef;

    /**
     * Default constructor for InstanceVariable.
     */
    public InstanceVariable() {
    }

    /**
     * Constructs an InstanceVariable with a specific object reference.
     * * @param objRef The index in the object table for the variable.
     */
    public InstanceVariable(int objRef) {
        this.objRef = objRef;
    }

    public int getObjRef() {
        return objRef;
    }

    public void setObjRef(int objRef) {
        this.objRef = objRef;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstanceVariable)) return false;
        InstanceVariable that = (InstanceVariable) o;
        return objRef == that.objRef;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objRef);
    }

    @Override
    public String toString() {
        return "InstanceVariable(" + objRef + ")";
    }

    /**
     * Returns the name of the variable from the object table.
     * * @param context The runtime context for the Unreal engine.
     * @return The name of the property as a string.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return safeGetObjectName(context, objRef);
    }
}