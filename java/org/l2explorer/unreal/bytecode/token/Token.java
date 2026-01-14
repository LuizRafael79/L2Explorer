package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.io.L2DataOutput;
import org.l2explorer.io.ObjectOutput;
import org.l2explorer.io.annotation.UByte;
import org.l2explorer.io.annotation.UShort;
import org.l2explorer.io.annotation.WriteMethod;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.BytecodeContext;
import org.l2explorer.unreal.bytecode.token.annotation.FunctionParams;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static org.l2explorer.io.ReflectionUtil.fieldGet;

/**
 * Base class for all UnrealScript bytecode tokens.
 * <p>Handles opcode identification, serialization, and dynamic size calculation 
 * through reflection-based sizers.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public abstract class Token {
    /**
     * Cache for sizers to avoid redundant reflection calls.
     * ConcurrentHashMap is preferred for thread-safety in modern Java.
     */
    private static final Map<Class<? extends Token>, Sizer<? extends Token>> SIZERS = new ConcurrentHashMap<>();

    /**
     * Returns the unique opcode associated with this token.
     *
     * @return The integer opcode.
     */
    protected abstract int getOpcode();

    /**
     * Writes the opcode byte to the output stream.
     *
     * @param output The data output to write to.
     * @param opcode The opcode value.
     * @throws IOException If an I/O error occurs.
     */
    protected void writeOpcode(L2DataOutput output, int opcode) throws IOException {
        output.writeByte(opcode);
    }

    /**
     * Serializes the token to the bytecode output.
     *
     * @param output The object output context.
     * @throws IOException If an I/O error occurs.
     */
    @WriteMethod
    public void writeToken(ObjectOutput<BytecodeContext> output) throws IOException {
        writeOpcode(output, getOpcode());
    }

    /**
     * Returns the UnrealScript textual representation of the token.
     *
     * @param context The runtime context for deparsing.
     * @return A string representing the UnrealScript code.
     */
    public abstract String toString(UnrealRuntimeContext context);

    /**
     * Retrieves or creates a sizer for the current token class.
     *
     * @return The sizer instance.
     */
    @SuppressWarnings("unchecked")
    protected Sizer<Token> getSizer() {
        return (Sizer<Token>) SIZERS.computeIfAbsent(getClass(), Token::createSizer);
    }

    /**
     * Creates a sizer that calculates the byte size of a token based on its fields.
     *
     * @param clazz The token class.
     * @return A sizer function.
     */
    private static Sizer<? extends Token> createSizer(Class<? extends Token> clazz) {
        return (token, context) -> 1 + // 1 byte for Opcode
                Arrays.stream(clazz.getDeclaredFields())
                        .filter(f -> !Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers()))
                        .map(Token::fieldSizer)
                        .mapToInt(f -> f.apply(token, context))
                        .sum();
    }

    /**
     * Determines the size-calculating function for a specific field.
     *
     * @param f The field to analyze.
     * @return A function that returns the size of the field in bytes.
     */
    @SuppressWarnings("unused")
	private static BiFunction<Token, BytecodeContext, Integer> fieldSizer(Field f) {
        Class<?> type = f.getType();
        
        if (type == byte.class || f.isAnnotationPresent(UByte.class)) {
            return (token, context) -> 1;
        } else if (type == short.class || f.isAnnotationPresent(UShort.class)) {
            return (token, context) -> 2;
        } else if (type == int.class || type == float.class) {
            return (token, context) -> 4;
        } else if (type == String.class) {
            return (token, context) -> {
                String s = (String) fieldGet(f, token);
                byte[] bytes = s.getBytes(context.getUnrealPackage().getFile().getCharset());
                return bytes.length + 1; // Length + null terminator
            };
        } else if (Token.class.isAssignableFrom(type)) {
            return (token, context) -> {
                Token t = (Token) fieldGet(f, token);
                return t != null ? t.getSize(context) : 0;
            };
        } else if (f.isAnnotationPresent(FunctionParams.class)) {
            return (token, context) -> {
                Token[] params = (Token[]) fieldGet(f, token);
                int paramsSize = Arrays.stream(params).mapToInt(t -> t.getSize(context)).sum();
                return paramsSize + new EndFunctionParams().getSize(context);
            };
        } else {
            throw new IllegalStateException("Unsupported field type for serialization: " + type);
        }
    }

    /**
     * Calculates the total size of this token in the bytecode.
     *
     * @param context The bytecode context.
     * @return The size in bytes.
     */
    public int getSize(BytecodeContext context) {
        return getSizer().getSize(this, context);
    }

    /**
     * Functional interface for token size calculation.
     */
    @FunctionalInterface
    interface Sizer<T extends Token> {
        int getSize(T token, BytecodeContext context);
    }
}