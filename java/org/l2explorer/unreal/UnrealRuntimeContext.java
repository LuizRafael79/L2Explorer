package org.l2explorer.unreal;

import org.l2explorer.io.UnrealPackage;
import java.util.Objects;

/**
 * Contexto de execução para objetos da Unreal Engine.
 * Removida dependência do Lombok para compatibilidade pura com Java/Ant.
 */
public class UnrealRuntimeContext extends UnrealPackageContext {
    private final UnrealPackage.ExportEntry entry;
    private final UnrealSerializerFactory serializer;

    public UnrealRuntimeContext(UnrealPackage.ExportEntry entry, UnrealSerializerFactory serializer) {
        // Passamos o pacote do objeto para a classe pai
        super(Objects.requireNonNull(entry, "Entry cannot be null").getUnrealPackage());
        this.entry = entry;
        this.serializer = serializer;
    }

    public UnrealPackage.ExportEntry getEntry() {
        return entry;
    }

    public UnrealSerializerFactory getSerializer() {
        return serializer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnrealRuntimeContext)) return false;
        if (!super.equals(o)) return false;
        UnrealRuntimeContext that = (UnrealRuntimeContext) o;
        return Objects.equals(entry, that.entry) && 
               Objects.equals(serializer, that.serializer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entry, serializer);
    }

    @Override
    public String toString() {
        return "UnrealRuntimeContext(" +
                "unrealPackage=" + getUnrealPackage() +
                ", entry=" + entry +
                ", serializer=" + serializer +
                ')';
    }
}