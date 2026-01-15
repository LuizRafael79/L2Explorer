package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.ObjectRef;

import java.util.Objects;

/**
 * Represents an access to a member variable of a structure (Opcode 0x36).
 * <p>In UnrealScript, this is used for the dot-notation access to struct fields,
 * such as 'Vector.X'. It contains a reference to the property object and 
 * the base struct expression.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class StructMember extends Token {
    /**
     * The bytecode opcode for Struct member access.
     */
    public static final int OPCODE = 0x36;

    /**
     * Reference to the property object representing the struct member.
     */
    @Compact
    @ObjectRef
    private int objRef;

    /**
     * The base structure expression being accessed.
     */
    private Token struct;

    /**
     * Default constructor for serialization and reflection.
     */
    public StructMember() {
    }

    /**
     * Constructs a StructMember token with the specified member reference and base struct.
     *
     * @param objRef The object reference to the member property.
     * @param struct The base struct token.
     */
    public StructMember(int objRef, Token struct) {
        this.objRef = objRef;
        this.struct = struct;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOpcode() {
        return OPCODE;
    }

    /**
     * Gets the object reference to the member.
     *
     * @return The object reference index.
     */
    public int getObjRef() {
        return objRef;
    }

    /**
     * Sets the object reference to the member.
     *
     * @param objRef The object reference index to set.
     */
    public void setObjRef(int objRef) {
        this.objRef = objRef;
    }

    /**
     * Gets the base structure expression.
     *
     * @return The struct token.
     */
    public Token getStruct() {
        return struct;
    }

    /**
     * Sets the base structure expression.
     *
     * @param struct The struct token to set.
     */
    public void setStruct(Token struct) {
        this.struct = struct;
    }

    /**
     * Returns a string representation of the token for debugging purposes.
     *
     * @return String formatted as StructMember(objRef, struct).
     */
    @Override
    public String toString() {
        return "StructMember(" + objRef + ", " + struct + ")";
    }

    /**
     * Returns the UnrealScript representation of this member access.
     *
     * @param context The runtime context for deparsing.
     * @return The formatted access string, e.g., "StructName.MemberName".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String base = (struct != null) ? struct.toString(context) : "None";
        String memberName;

        try {
            // Busca a referência do membro (variável dentro da struct)
            Object refObj = context.getUnrealPackage().objectReference(objRef);
            
            if (refObj instanceof UnrealPackage.Entry<?>) {
                memberName = ((UnrealPackage.Entry<?>) refObj).getObjectName().getName();
            } else {
                memberName = objRef == 0 ? "None" : "UnknownMember_" + objRef;
            }
        } catch (Exception e) {
            // Previne falha se o índice apontar para fora da tabela
            memberName = "ErrorMember_" + objRef;
        }
        
        return base + "." + memberName;
    }

    /**
     * Compares this token to the specified object.
     *
     * @param o The object to compare with.
     * @return true if the member references and base structs are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StructMember that = (StructMember) o;
        return objRef == that.objRef && Objects.equals(struct, that.struct);
    }

    /**
     * Returns the hash code for this token.
     *
     * @return The hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(objRef, struct);
    }
}