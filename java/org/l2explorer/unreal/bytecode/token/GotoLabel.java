package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents a goto label jump instruction (Opcode 0x0d).
 * <p>This token directs the Unreal Virtual Machine to jump to a specific 
 * label or destination within the current execution scope.</p>
 * * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Pro (Clean Room Reimplementation)
 * * @since 12-01-2026
 */
public class GotoLabel extends Token {
    /**
     * The bytecode operation code for GotoLabel.
     */
    public static final int OPCODE = 0x0d;

    private Token label;

    /**
     * Default constructor for GotoLabel.
     */
    public GotoLabel() {
    }

    /**
     * Constructs a GotoLabel with a target label token.
     * * @param label The token representing the target label or offset.
     */
    public GotoLabel(Token label) {
        this.label = label;
    }

    public Token getLabel() {
        return label;
    }

    public void setLabel(Token label) {
        this.label = label;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GotoLabel)) return false;
        GotoLabel that = (GotoLabel) o;
        return Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public String toString() {
        return "GotoLabel(" + label + ")";
    }

    /**
     * Returns the decompiler representation of the goto instruction.
     * * @param context The runtime context for the Unreal engine.
     * @return The formatted string "Goto(label)".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        String labelStr = (label == null) ? "null" : label.toString(context);
        return "Goto(" + labelStr + ")";
    }
}