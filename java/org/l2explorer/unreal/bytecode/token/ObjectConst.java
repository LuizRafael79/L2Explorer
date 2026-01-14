package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.ObjectRef;

/**
 * Represents a constant object or class reference (Opcode 0x20).
 * <p>This token stores a reference to an entry in the object table. 
 * Depending on the type of the referenced object (e.g., a Class), 
 * it may be rendered as a literal like class'Actor'.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class ObjectConst extends Token {
    /**
     * The bytecode operation code for ObjectConst.
     */
    public static final int OPCODE = 0x20;

    @Compact
    @ObjectRef
    private int objRef;

    /**
     * Default constructor for ObjectConst.
     */
    public ObjectConst() {
    }

    /**
     * Constructs an ObjectConst with the specified object reference index.
     *
     * @param objRef The index in the object table.
     */
    public ObjectConst(int objRef) {
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
        if (!(o instanceof ObjectConst)) return false;
        ObjectConst that = (ObjectConst) o;
        return objRef == that.objRef;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objRef);
    }

    @Override
    public String toString() {
        return "ObjectConst(" + objRef + ")";
    }

    /**
     * Returns the decompiler representation of the object reference.
     * <p>If the reference is a Class, it returns class'ClassName'. 
     * Otherwise, returns the object name directly.</p>
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string representation of the object reference.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        UnrealPackage.Entry<?> reference = context.getUnrealPackage().objectReference(objRef);
        String name = reference.getObjectName().getName();
        
        // Handle class literal formatting
        if ("Core.Class".equalsIgnoreCase(reference.getFullClassName())) {
            return "class'" + name + "'";
        }
        
        return name;
    }
}