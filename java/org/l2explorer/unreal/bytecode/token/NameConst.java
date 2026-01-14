package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.NameRef;

/**
 * Represents a constant Name literal (Opcode 0x21).
 * <p>Names in UnrealScript are stored as indices in the Name Table. 
 * They are case-insensitive and used for property names, states, and identifiers.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class NameConst extends Token {
    /**
     * The bytecode operation code for NameConst.
     */
    public static final int OPCODE = 0x21;

    @Compact
    @NameRef
    private int nameRef;

    /**
     * Default constructor for NameConst.
     */
    public NameConst() {
    }

    /**
     * Constructs a NameConst with a specific reference to the Name Table.
     *
     * @param nameRef The index in the name table.
     */
    public NameConst(int nameRef) {
        this.nameRef = nameRef;
    }

    public int getNameRef() {
        return nameRef;
    }

    public void setNameRef(int nameRef) {
        this.nameRef = nameRef;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NameConst)) return false;
        NameConst nameConst = (NameConst) o;
        return nameRef == nameConst.nameRef;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameRef);
    }

    @Override
    public String toString() {
        return "NameConst(" + nameRef + ")";
    }

    /**
     * Returns the decompiler representation of the name literal.
     * <p>Empty names or 'None' are returned as double single quotes (''). 
     * Otherwise, returns the name wrapped in single quotes.</p>
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string 'Name'.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String name = context.getUnrealPackage().nameReference(nameRef);
        
        if (name == null || "None".equalsIgnoreCase(name)) {
            return "''";
        }
        
        return "'" + name + "'";
    }
}