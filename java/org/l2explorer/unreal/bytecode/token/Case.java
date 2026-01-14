package org.l2explorer.unreal.bytecode.token;

import java.io.IOException;
import java.util.Objects;

import org.l2explorer.io.ObjectInput;
import org.l2explorer.io.ObjectOutput;
import org.l2explorer.io.annotation.ReadMethod;
import org.l2explorer.io.annotation.UShort;
import org.l2explorer.io.annotation.WriteMethod;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.Offset;
import org.l2explorer.unreal.bytecode.BytecodeContext;

/**
 * Token de Case/Default (Opcode 0x0a).
 * Gerencia o desvio de fluxo dentro de um bloco switch.
 */
public class Case extends Token {
    public static final int OPCODE = 0x0a;
    public static final int DEFAULT = 0xffff;

    @UShort
    @Offset
    private int nextOffset;
    private Token condition;

    public Case() {
    }

    public Case(int nextOffset, Token condition) {
        this.nextOffset = nextOffset;
        this.condition = condition;
    }

    public int getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(int nextOffset) {
        this.nextOffset = nextOffset;
    }

    public Token getCondition() {
        return condition;
    }

    public void setCondition(Token condition) {
        this.condition = condition;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @ReadMethod
    public void readFrom(ObjectInput<BytecodeContext> input) throws IOException {
        this.nextOffset = input.readUnsignedShort();
        if (this.nextOffset != DEFAULT) {
            this.condition = input.readObject(Token.class);
        }
    }

    @WriteMethod
    public void writeCase(ObjectOutput<BytecodeContext> output) throws IOException {
        output.writeShort(this.nextOffset);
        if (this.nextOffset != DEFAULT) {
            output.write(this.condition);
        }
    }

    @Override
    public int getSize(BytecodeContext context) {
        int size = 1; // opcode
        size += 2;    // nextOffset (UShort)
        if (condition != null) {
            size += condition.getSize(context);
        }
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Case)) return false;
        Case aCase = (Case) o;
        return nextOffset == aCase.nextOffset && Objects.equals(condition, aCase.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nextOffset, condition);
    }

    @Override
    public String toString() {
        return "Case(" + String.format("0x%04x", nextOffset) + ", " + condition + ')';
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        if (nextOffset == DEFAULT) {
            return "default:";
        }
        String condStr = (condition == null) ? "null" : condition.toString(context);
        return "case " + condStr + ":";
    }
}