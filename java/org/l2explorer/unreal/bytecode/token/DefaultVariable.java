package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.ObjectRef;

/**
 * Token de Variável Padrão (Opcode 0x02).
 * Representa o acesso ao valor inicial de uma propriedade (ex: default.PropertyName).
 */
public class DefaultVariable extends Token {
    public static final int OPCODE = 0x02;

    @Compact
    @ObjectRef
    private int objRef;

    public DefaultVariable() {
    }

    public DefaultVariable(int objRef) {
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
        if (!(o instanceof DefaultVariable)) return false;
        DefaultVariable that = (DefaultVariable) o;
        return objRef == that.objRef;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objRef);
    }

    @Override
    public String toString() {
        return "DefaultVariable(" + objRef + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        try {
            // Busca o nome do objeto (propriedade) através da referência no pacote
            return "default." + context.getUnrealPackage()
                    .objectReference(objRef)
                    .getObjectName()
                    .getName();
        } catch (Exception e) {
            // Caso a referência seja inválida ou o contexto esteja incompleto
            return "default.Unknown_" + objRef;
        }
    }
}