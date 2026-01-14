package org.l2explorer.unreal;

import org.l2explorer.io.Context;
import org.l2explorer.io.UnrealPackage;
import java.util.Objects;

/**
 * Contexto básico para operações em pacotes Unreal.
 * Removido Lombok e adaptado para Java puro.
 */
public class UnrealPackageContext implements Context {
    private final UnrealPackage unrealPackage;

    public UnrealPackageContext(UnrealPackage unrealPackage) {
        // Substituindo @NonNull do Lombok pelo padrão do Java
        this.unrealPackage = Objects.requireNonNull(unrealPackage, "unrealPackage cannot be null");
    }

    // Implementação manual do @Getter
    public UnrealPackage getUnrealPackage() {
        return unrealPackage;
    }

    // Implementação manual do @EqualsAndHashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnrealPackageContext that = (UnrealPackageContext) o;
        return Objects.equals(unrealPackage, that.unrealPackage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unrealPackage);
    }

    // Implementação manual do @ToString
    @Override
    public String toString() {
        return "UnrealPackageContext(" +
                "unrealPackage=" + unrealPackage +
                ')';
    }
}