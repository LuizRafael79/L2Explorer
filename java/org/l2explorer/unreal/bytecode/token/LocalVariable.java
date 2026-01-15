package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.ObjectRef;

/**
 * Represents a reference to a local variable or function parameter (Opcode 0x00).
 * <p>This token is used to access variables defined within the scope of a function.
 * It refers to an entry in the object table that describes the local property.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class LocalVariable extends Token {
    /**
     * The bytecode operation code for LocalVariable.
     */
    public static final int OPCODE = 0x00;

    @Compact
    @ObjectRef
    private int objRef;

    /**
     * Default constructor for LocalVariable.
     */
    public LocalVariable() {
    }

    /**
     * Constructs a LocalVariable with a specific object reference.
     *
     * @param objRef The index in the object table for the local variable.
     */
    public LocalVariable(int objRef) {
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
        if (!(o instanceof LocalVariable)) return false;
        LocalVariable that = (LocalVariable) o;
        return objRef == that.objRef;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objRef);
    }

    @Override
    public String toString() {
        return "LocalVariable(" + objRef + ")";
    }

    /**
     * Resolves the local variable name from the object table using the runtime context.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The name of the local variable.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        try {
            var entry = context.getUnrealPackage().objectReference(objRef);
            if (entry == null) {
                return "LocalVar_Ref" + objRef;
            }
            var name = entry.getObjectName();
            if (name == null) {
                return "LocalVar_NoName" + objRef;
            }
            return name.getName();
        } catch (Exception e) {
            return "LocalVar_Error" + objRef;
        }
    }
}