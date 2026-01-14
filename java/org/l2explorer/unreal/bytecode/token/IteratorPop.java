package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents the cleanup instruction for an iterator loop (Opcode 0x30).
 * <p>This token is responsible for popping the iterator state off the execution 
 * stack once the loop finishes or is exited. It ensures the VM state remains 
 * consistent for subsequent operations.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 12-01-2026
 */
public class IteratorPop extends Token {
    /**
     * The bytecode operation code for IteratorPop.
     */
    public static final int OPCODE = 0x30;

    /**
     * Default constructor for IteratorPop.
     */
    public IteratorPop() {
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IteratorPop;
    }

    @Override
    public int hashCode() {
        return OPCODE;
    }

    @Override
    public String toString() {
        return "IteratorPop()";
    }

    /**
     * Returns a comment representation as this token has no direct 
     * source code equivalent other than closing the loop block.
     *
     * @param context The runtime context for the Unreal engine.
     * @return A string comment indicating the pop operation.
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        return "/* IteratorPop */";
    }
}