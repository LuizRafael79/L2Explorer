package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;
import java.util.Objects;

@ConversionToken
public class BoolToFloat extends Token {
    public static final int OPCODE = 0x42;

    private Token value;

    public BoolToFloat() {
    }

    public BoolToFloat(Token value) {
        this.value = value;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoolToFloat)) return false;
        BoolToFloat that = (BoolToFloat) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BoolToFloat(" + value + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        return "float(" + (value == null ? "null" : value.toString(context)) + ")";
    }
}