package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.NameRef;

/**
 * Token de Nome de Delegado (Opcode 0x44).
 * Referencia um nome no NameTable para uso em delegados.
 */
public class DelegateFunction extends Token {
    public static final int OPCODE = 0x2b;

    @Compact
    @NameRef
    private int nameRef;

    public DelegateFunction() {
    }

    public DelegateFunction(int nameRef) {
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
        if (!(o instanceof DelegateFunction)) return false;
        DelegateFunction that = (DelegateFunction) o;
        return nameRef == that.nameRef;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameRef);
    }

    @Override
    public String toString() {
        return "DelegateName(" + nameRef + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        try {
            // Busca a String literal no pool de nomes do pacote através do índice
            return context.getUnrealPackage().nameReference(nameRef);
        } catch (Exception e) {
            return "UnknownName_" + nameRef;
        }
    }
}