package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.ObjectRef;

/**
 * Represents a reference to a property passed as a parameter to a native function (Opcode 0x29).
 * <p>Used primarily for 'out' parameters where the native code needs 
 * the actual property reference to modify its value directly in memory.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class NativeParam extends Token {
    /**
     * The bytecode operation code for NativeParam.
     */
    public static final int OPCODE = 0x29;

    @Compact
    @ObjectRef
    private int objRef;

    /**
     * Default constructor for NativeParam.
     */
    public NativeParam() {
    }

    /**
     * Constructs a NativeParam with a specific object reference.
     *
     * @param objRef The index in the object table for the property.
     */
    public NativeParam(int objRef) {
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
        if (!(o instanceof NativeParam)) return false;
        NativeParam that = (NativeParam) o;
        return objRef == that.objRef;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objRef);
    }

    @Override
    public String toString() {
        return "NativeParam(" + objRef + ")";
    }

    /**
     * Resolves the property name from the object table.
     *
     * @param context The runtime context for the Unreal engine.
     * @return The name of the property being passed.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return safeGetObjectName(context, objRef);
    }
}