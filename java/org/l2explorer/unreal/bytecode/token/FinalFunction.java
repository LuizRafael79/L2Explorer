package org.l2explorer.unreal.bytecode.token;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.ObjectRef;
import org.l2explorer.unreal.bytecode.token.annotation.FunctionParams;

/**
 * Represents a call to a non-virtual function (Opcode 0x1c).
 * <p>Final functions are resolved at compile time and cannot be overridden by subclasses.
 * This token also handles the logic for {@code Super} calls by comparing the 
 * function's owner package with the current context.</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Pro (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
public class FinalFunction extends Token {
    /**
     * The bytecode operation code for FinalFunction.
     */
    public static final int OPCODE = 0x1c;

    @Compact
    @ObjectRef
    private int funcRef;

    @FunctionParams
    private Token[] params;

    /**
     * Default constructor for FinalFunction.
     */
    public FinalFunction() {
    }

    /**
     * Constructs a FinalFunction with a reference and parameters.
     * * @param funcRef The object reference index to the function.
     * @param params The array of tokens representing the function arguments.
     */
    public FinalFunction(int funcRef, Token... params) {
        this.funcRef = funcRef;
        this.params = params;
    }

    public int getFuncRef() {
        return funcRef;
    }

    public void setFuncRef(int funcRef) {
        this.funcRef = funcRef;
    }

    public Token[] getParams() {
        return params;
    }

    public void setParams(Token[] params) {
        this.params = params;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FinalFunction)) return false;
        FinalFunction that = (FinalFunction) o;
        return funcRef == that.funcRef && Arrays.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(funcRef);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }

    @Override
    public String toString() {
        String paramsStr = (params == null || params.length == 0) ? "" : 
            ", " + Arrays.stream(params).map(Objects::toString).collect(Collectors.joining(", "));
        return "FinalFunction(" + funcRef + paramsStr + ")";
    }

    /**
     * Decompiles the non-virtual function call.
     * <p>Includes logic to determine if the function call belongs to a parent class,
     * prefixing it with 'Super.' when necessary.</p>
     * * @param context The runtime context for the Unreal engine.
     * @return The formatted function call (e.g., "Super.Notify()" or "Calculate()").
     */
    
    @Override
    public String toString(UnrealRuntimeContext context) {
        // 1. Busca a referência da função com proteção contra nulo
        Object ref = context.getUnrealPackage().objectReference(funcRef);
        if (!(ref instanceof UnrealPackage.Entry<?>)) {
            return "/* UnknownFunc_" + funcRef + " */" + formatParams(context);
        }

        UnrealPackage.Entry<?> func = (UnrealPackage.Entry<?>) ref;
        String prefix = "";

        // 2. Lógica de detecção de 'Super.' (Protegida contra NullPointerException)
        try {
            if (context.getSerializer() != null && context.getEntry() != null) {
                UnrealPackage.Entry<?> entryHolder = context.getEntry().getObjectPackage();
                UnrealPackage.Entry<?> funcHolder = func.getObjectPackage();
                
                // Só entra na lógica se ambos os holders existirem e forem diferentes
                if (entryHolder != null && funcHolder != null &&
                    !Objects.equals(entryHolder.getObjectFullName(), funcHolder.getObjectFullName())) {
                    
                    if (context.getSerializer().isSubclass(funcHolder.getObjectFullName(), entryHolder.getObjectFullName())) {
                        prefix = "Super.";
                    }
                }
            }
        } catch (Exception ignore) {
            // Se a verificação de subclasse falhar, apenas ignoramos o prefixo Super.
        }

        // 3. Retorna o nome da função + parâmetros
        String funcName = func.getObjectName().getName();
        return prefix + funcName + formatParams(context);
    }

    /**
     * Helper para formatar os parâmetros sem poluir o método principal.
     */
    private String formatParams(UnrealRuntimeContext context) {
        if (params == null || params.length == 0) {
            return "()";
        }
        return "(" + Arrays.stream(params)
                .map(p -> p == null ? "null" : p.toString(context))
                .collect(Collectors.joining(", ")) + ")";
    }
}