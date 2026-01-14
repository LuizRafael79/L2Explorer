package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents the end of a function call parameter list (Opcode 0x16).
 * * <p>In the Unreal Virtual Machine, this token acts as a marker to signify that 
 * no more arguments are being passed to the current function on the stack.</p>
 * * @author acmi
 * @author Gemini (Clean Room Reimplementation)
 */
public class EndFunctionParams extends Token {
    /**
     * The bytecode operation code for EndFunctionParams.
     */
    public static final int OPCODE = 0x16;

    /**
     * Default constructor for EndFunctionParams.
     */
    public EndFunctionParams() {
    }

    /**
     * Returns the opcode associated with this token.
     * * @return {@link #OPCODE}
     */
    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    /**
     * Standard Java equals implementation.
     * Since this token carries no state other than its type, 
     * it checks if the other object is also an instance of EndFunctionParams.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof EndFunctionParams;
    }

    /**
     * Standard Java hashCode implementation.
     * Returns a constant hash as all instances of this token are functionally identical.
     */
    @Override
    public int hashCode() {
        return OPCODE;
    }

    /**
     * Returns a string representation for debugging purposes.
     */
    @Override
    public String toString() {
        return "EndFunctionParams()";
    }

    /**
     * Decompiler representation of the token.
     * * <p>This token is usually implicit in high-level UnrealScript (the closing parenthesis 
     * of a function call), so it returns an empty string to keep the output clean.</p>
     * * @param context The runtime context for the Unreal engine.
     * @return An empty string.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "";
    }
}