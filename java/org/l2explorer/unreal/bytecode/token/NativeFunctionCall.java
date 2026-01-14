package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.io.L2DataOutput;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.token.annotation.FunctionParams;
import org.l2explorer.unreal.core.Function;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a call to a native function.
 * <p>In Unreal Engine 2.5 (Lineage II), native indices above 255 use an 
 * extended 2-byte encoding with a prefix in the 0x60-0x6F range.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class NativeFunctionCall extends Token {
    /** * Native index is transient as it's handled manually by writeOpcode 
     * or by the specific buffer reader.
     */
    private transient int nativeIndex;
    
    @FunctionParams
    private Token[] params;

    public NativeFunctionCall() {
    }

    public NativeFunctionCall(int nativeIndex, Token... params) {
        this.nativeIndex = nativeIndex;
        this.params = params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getOpcode() {
        return nativeIndex;
    }

    /**
     * Handles the specific Lineage II / UE2.5 extended opcode logic.
     * <p>If the opcode > 255, it writes a prefix byte (0x60 + high nibble) 
     * followed by the low byte.</p>
     *
     * @param output The data output stream.
     * @param opcode The native index to write.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void writeOpcode(L2DataOutput output, int opcode) throws IOException {
        if (opcode > 0xFF) {
            // Extended opcode: prefix 0x60..0x6F
            output.writeByte(0x60 + ((opcode >> 8) & 0x0F));
            output.writeByte(opcode & 0xFF);
        } else {
            output.writeByte(opcode);
        }
    }

    /**
     * Custom sizer to account for 1 or 2 byte native indices.
     */
    @Override
    protected Sizer<Token> getSizer() {
        return (token, context) -> {
            NativeFunctionCall nfc = (NativeFunctionCall) token;
            int baseSize = (nfc.getNativeIndex() > 0xFF) ? 2 : 1;
            
            int paramsSize = Stream.concat(
                    Arrays.stream(nfc.getParams()), 
                    Stream.of(new EndFunctionParams())
            ).mapToInt(t -> t.getSize(context)).sum();
            
            return baseSize + paramsSize;
        };
    }

    public int getNativeIndex() {
        return nativeIndex;
    }

    public void setNativeIndex(int nativeIndex) {
        this.nativeIndex = nativeIndex;
    }

    public Token[] getParams() {
        return params;
    }

    public void setParams(Token[] params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NativeFunctionCall that)) return false;
        return nativeIndex == that.nativeIndex && Arrays.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(nativeIndex);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }

    @Override
    public String toString() {
        String pStr = (params == null || params.length == 0) ? "" 
                : Arrays.stream(params).map(Objects::toString).collect(Collectors.joining(", ", ", ", ""));
        return "NativeFunctionCall(" + nativeIndex + pStr + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        if (context.getSerializer() == null) {
            return "native" + nativeIndex + renderParams(context);
        }

        Optional<Function> funcOpt = context.getSerializer().getNativeFunction(nativeIndex);
        if (funcOpt.isEmpty()) {
            return "native" + nativeIndex + renderParams(context);
        }

        Function f = funcOpt.get();
        Collection<Function.Flag> flags = Function.Flag.getFlags(f.getFunctionFlags());

        // Pre-operator logic (e.g., !Condition)
        if (flags.contains(Function.Flag.PRE_OPERATOR)) {
            String b = params[0].toString(context);
            if (params[0] instanceof NativeFunctionCall nfc && isOperator(nfc, context)) {
                b = "(" + b + ")";
            }
            return f.getFriendlyName() + b;
        }

        // Operator logic (e.g., A + B)
        if (flags.contains(Function.Flag.OPERATOR)) {
            if (f.getOperatorPrecedence() > 0) {
                Token left = params[0];
                Token right = params[params.length - 1];
                
                boolean needLeftBrackets = checkBrackets(left, context, p -> p > f.getOperatorPrecedence());
                boolean needRightBrackets = checkBrackets(right, context, p -> p >= f.getOperatorPrecedence());
                
                String lStr = (needLeftBrackets ? "(" : "") + left.toString(context) + (needLeftBrackets ? ")" : "");
                String rStr = (needRightBrackets ? "(" : "") + right.toString(context) + (needRightBrackets ? ")" : "");
                
                return lStr + " " + f.getFriendlyName() + " " + rStr;
            }
            return params[0].toString(context) + f.getFriendlyName();
        }

        return f.getFriendlyName() + renderParams(context);
    }

    private String renderParams(UnrealRuntimeContext context) {
        return Arrays.stream(this.params)
                .map(p -> p.toString(context))
                .collect(Collectors.joining(", ", "(", ")"));
    }

    private boolean isOperator(Token token, UnrealRuntimeContext context) {
        if (token instanceof NativeFunctionCall nfc) {
            return context.getSerializer()
                    .getNativeFunction(nfc.getNativeIndex())
                    .map((org.l2explorer.unreal.core.Function f) -> 
                        // Usamos Function.Flag (singular) e OPERATOR (maiúsculo)
                        org.l2explorer.unreal.core.Function.Flag.getFlags(f.getFunctionFlags())
                            .contains(org.l2explorer.unreal.core.Function.Flag.OPERATOR))
                    .orElse(false);
        }
        return false;
    }

    private boolean checkBrackets(Token token, UnrealRuntimeContext context, IntPredicate predicate) {
        if (token instanceof NativeFunctionCall nfc) {
            return context.getSerializer()
                    .getNativeFunction(nfc.getNativeIndex())
                    .map((org.l2explorer.unreal.core.Function f) -> predicate.test(f.getOperatorPrecedence()))
                    .orElse(true);
        }
        return false;
    }
}