package org.l2explorer.unreal.bytecode;

import org.l2explorer.io.UnrealPackage;
import org.l2explorer.unreal.UnrealPackageContext; 
import java.util.Objects;

/**
 * Contexto específico para o interpretador de Bytecode da Unreal Engine.
 * Controla estados de conversão durante a leitura de tokens.
 */
public class BytecodeContext extends UnrealPackageContext {
    private boolean conversion;

    public BytecodeContext(UnrealPackage unrealPackage) {
        super(Objects.requireNonNull(unrealPackage, "UnrealPackage cannot be null"));
    }

    public BytecodeContext(UnrealPackageContext context) {
        super(Objects.requireNonNull(context, "Context cannot be null").getUnrealPackage());
    }

    public boolean isConversion() {
        return conversion;
    }

    public void setConversion(boolean conversion) {
        this.conversion = conversion;
    }

    public void changeConversion() {
        this.conversion = !this.conversion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BytecodeContext)) return false;
        if (!super.equals(o)) return false;
        // Seguindo a lógica original: ignora o estado de 'conversion' na comparação
        return true; 
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "BytecodeContext(" +
                "unrealPackage=" + getUnrealPackage() +
                ", conversion=" + conversion +
                ')';
    }
}