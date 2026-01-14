package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.NameRef;
import org.l2explorer.unreal.bytecode.token.annotation.FunctionParams;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a virtual function call (Opcode 0x1B).
 * <p>In UnrealScript, virtual functions support polymorphism, allowing 
 * subclasses to override behavior. This token stores a reference to the 
 * function name and an array of expression tokens as parameters.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class VirtualFunction extends Token {
    /**
     * The bytecode opcode for a virtual function call.
     */
    public static final int OPCODE = 0x1B;

    /**
     * Reference to the name of the function in the NameTable.
     */
    @Compact
    @NameRef
    private int nameRef;

    /**
     * Array of tokens representing the arguments passed to the function.
     */
    @FunctionParams
    private Token[] params;

    /**
     * Default constructor for serialization and reflection.
     */
    public VirtualFunction() {
    }

    /**
     * Constructs a VirtualFunction token with a name reference and parameters.
     *
     * @param nameRef The index in the name table for the function name.
     * @param params Variadic list of tokens representing function arguments.
     */
    public VirtualFunction(int nameRef, Token... params) {
        this.nameRef = nameRef;
        this.params = params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getOpcode() {
        return OPCODE;
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

    /**
     * Returns a string representation of the token for debugging purposes.
     */
    @Override
    public String toString() {
        String paramsStr = (params == null || params.length == 0) 
                ? "" 
                : Arrays.stream(params)
                        .map(Objects::toString)
                        .collect(Collectors.joining(", ", ", ", ""));
        
        return "VirtualFunction(" + nameRef + paramsStr + ")";
    }

    /**
     * Returns the UnrealScript representation of this function call.
     * <p>Revised to safely handle the function name resolution from the context.</p>
     *
     * @param context The runtime context for deparsing.
     * @return The formatted function call, e.g., "FunctionName(arg1, arg2)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        // We use String.valueOf to avoid the ".getName()" issue if nameReference returns a String.
        // This makes the code resilient to different implementations of the name table.
        Object resolvedName = context.getUnrealPackage().nameReference(nameRef);
        String functionName = String.valueOf(resolvedName);
        
        String args = (params == null) 
                ? "" 
                : Arrays.stream(params)
                        .map(p -> p.toString(context))
                        .collect(Collectors.joining(", "));
        
        return functionName + "(" + args + ")";
    }

    /**
     * Compares this token to the specified object for equality.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Modern Pattern Matching for instanceof (Java 16+)
        if (!(o instanceof VirtualFunction that)) return false;
        return nameRef == that.nameRef && Arrays.equals(params, that.params);
    }

    /**
     * Returns the hash code for this token.
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(nameRef);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }
}