package org.l2explorer.utils;

import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.bytecode.BytecodeContext;
import org.l2explorer.io.L2DataOutput;
import org.l2explorer.io.ObjectOutput;

public abstract class Token {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends Token>, Sizer> SIZERS = new HashMap<>();

    protected abstract int getOpcode();

    protected void writeOpcode(L2DataOutput output, int opcode) throws UncheckedIOException {
        try {
            output.writeByte(opcode);
        } catch (java.io.IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void writeToken(ObjectOutput<BytecodeContext> output) throws UncheckedIOException {
        writeOpcode(output, getOpcode());
        // Cada subclasse (como ArrayBytecode) deverá sobrescrever isso 
        // para escrever seus próprios campos após o opcode.
    }

    public abstract String toString(UnrealRuntimeContext context);

    public int getSize(BytecodeContext context) {
        return getSizer().getSize(this, context);
    }

    @SuppressWarnings({ "unchecked", "unused" })
    protected Sizer<Token> getSizer() {
        return SIZERS.computeIfAbsent(getClass(), k -> createSizer(getClass()));
    }

    private static Sizer<Token> createSizer(Class<? extends Token> clazz) {
        return (token, context) -> 1 + // opcode (1 byte)
                Arrays.stream(clazz.getDeclaredFields())
                        .filter(f -> !Modifier.isStatic(f.getModifiers()))
                        .filter(f -> !Modifier.isTransient(f.getModifiers()))
                        .map(Token::fieldSizer)
                        .mapToInt(f -> f.apply(token, context))
                        .sum();
    }

    @SuppressWarnings("unused")
	private static BiFunction<Token, BytecodeContext, Integer> fieldSizer(Field f) {
        Class<?> type = f.getType();
        f.setAccessible(true); // Garante acesso aos campos index e array

        // Substituindo verificações de anotação por lógica de tipo ou nomes
        if (type == Byte.TYPE) {
            return (token, context) -> 1;
        } else if (type == Short.TYPE) {
            return (token, context) -> 2;
        } else if (type == Integer.TYPE) {
            return (token, context) -> 4;
        } else if (type == Float.TYPE) {
            return (token, context) -> 4;
        } else if (type == String.class) {
            return (token, context) -> {
                try {
                    String s = (String) f.get(token);
                    return s == null ? 1 : s.getBytes(context.getUnrealPackage().getCharset()).length + 1;
                } catch (IllegalAccessException e) {
                    return 1;
                }
            };
        } else if (Token.class.isAssignableFrom(type)) {
            return (token, context) -> {
                try {
                    Token t = (Token) f.get(token);
                    return t == null ? 0 : t.getSize(context);
                } catch (IllegalAccessException e) {
                    return 0;
                }
            };
        }
        // Se encontrar campos de Array (como parâmetros de função), trata aqui
        else if (type.isArray() && Token.class.isAssignableFrom(type.getComponentType())) {
             return (token, context) -> {
                 try {
                     Token[] tokens = (Token[]) f.get(token);
                     return Arrays.stream(tokens).mapToInt(t -> t.getSize(context)).sum() + 1; // +1 para o marcador de fim
                 } catch (IllegalAccessException e) {
                     return 0;
                 }
             };
        }
        
        return (token, context) -> 0;
    }

    interface Sizer<T extends Token> {
        int getSize(T token, BytecodeContext context);
    }
}