package org.l2explorer.unreal.bytecode.token;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.NameRef;
import org.l2explorer.unreal.bytecode.token.annotation.FunctionParams;

/**
 * Represents a call to a global function (Opcode 0x38).
 * <p>This token is used to call the non-state version of a function, 
 * bypassing any state-specific overrides. In UnrealScript, this is 
 * represented by the {@code Global.} prefix.</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Pro (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
public class GlobalFunction extends Token {
    /**
     * The bytecode operation code for GlobalFunction.
     */
    public static final int OPCODE = 0x38;

    @Compact
    @NameRef
    private int nameRef;

    @FunctionParams
    private Token[] params;

    /**
     * Default constructor for GlobalFunction.
     */
    public GlobalFunction() {
    }

    /**
     * Constructs a GlobalFunction with a name reference and parameters.
     * * @param nameRef The index in the NameTable for the function name.
     * @param params The array of tokens representing the function arguments.
     */
    public GlobalFunction(int nameRef, Token... params) {
        this.nameRef = nameRef;
        this.params = params;
    }

    public int getNameRef() {
        return nameRef;
    }

    public void setNameRef(int nameRef) {
        this.nameRef = nameRef;
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
        if (!(o instanceof GlobalFunction)) return false;
        GlobalFunction that = (GlobalFunction) o;
        return nameRef == that.nameRef && Arrays.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(nameRef);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }

    @Override
    public String toString() {
        String paramsStr = (params == null || params.length == 0) ? "" : 
            ", " + Arrays.stream(params).map(Objects::toString).collect(Collectors.joining(", "));
        return "GlobalFunction(" + nameRef + paramsStr + ")";
    }

    /**
     * Returns the decompiler representation of the global function call.
     * <p>Formatted as "Global.FunctionName(param1, param2, ...)"</p>
     * * @param context The runtime context for the Unreal engine.
     * @return The formatted string for the global function call.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String funcName = context.getUnrealPackage().nameReference(nameRef);
        String paramsStr = (params == null) ? "" : 
            Arrays.stream(params).map(p -> p.toString(context)).collect(Collectors.joining(", "));
        
        return "Global." + funcName + "(" + paramsStr + ")";
    }
}