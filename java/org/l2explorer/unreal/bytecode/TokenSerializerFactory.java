/*
 * Copyright (c) 2021 acmi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.l2explorer.unreal.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.l2explorer.io.L2DataInput;
import org.l2explorer.io.ObjectInput;
import org.l2explorer.io.ObjectOutput;
import org.l2explorer.io.ReflectionSerializerFactory;
import org.l2explorer.io.SerializerException;
import org.l2explorer.unreal.UnrealException;
import org.l2explorer.unreal.bytecode.token.ConversionTable;
import org.l2explorer.unreal.bytecode.token.EndFunctionParams;
import org.l2explorer.unreal.bytecode.token.NativeFunctionCall;
import org.l2explorer.unreal.bytecode.token.Token;
import org.l2explorer.unreal.bytecode.token.annotation.ConversionToken;
import org.l2explorer.unreal.bytecode.token.annotation.FunctionParams;
import org.l2explorer.utils.enums.UnrealOpcode;

public class TokenSerializerFactory extends ReflectionSerializerFactory<BytecodeContext> {
    private static final Logger log = Logger.getLogger(TokenSerializerFactory.class.getName());

    private static final int EX_ExtendedNative = 0x60;
    private static final int EX_FirstNative = 0x70;

    private static final Map<Integer, Class<? extends Token>> mainTokenTable = new HashMap<>();
    private static final Map<Integer, Class<? extends Token>> conversionTokenTable = new HashMap<>();

    @Override
    protected Function<ObjectInput<BytecodeContext>, Object> createInstantiator(Class<?> clazz) {
        if (Token.class.isAssignableFrom(clazz)) {
            return arg0 -> {
                try {
                    // Chama o seu método instantiate(arg0)
                    return instantiate(arg0);
                } catch (IOException e) {
                    // IMPORTANTE: Lançar a exceção impede que o Java tente 
                    // fazer o cast de UnrealObjectInput para Token
                    throw new SerializerException(e);
                }
            };
        }
        return super.createInstantiator(clazz);
    }

    private Token readNativeCall(ObjectInput<BytecodeContext> input, int b) throws UncheckedIOException, IOException {
        int nativeIndex = (b & 0xF0) == EX_ExtendedNative ?
                ((b - EX_ExtendedNative) << 8) + input.readUnsignedByte() : b;

        if (nativeIndex < EX_FirstNative) {
            throw new UnrealException("Invalid native index: " + nativeIndex);
        }

        return new NativeFunctionCall(nativeIndex);
    }

    @Override
    protected <T> void serializer(@SuppressWarnings("rawtypes") Class type, Function<T, Object> getter, @SuppressWarnings("rawtypes") BiConsumer<T, Supplier> setter, Function<Class<? extends Annotation>, Annotation> getAnnotation, List<BiConsumer<T, ObjectInput<BytecodeContext>>> read, List<BiConsumer<T, ObjectOutput<BytecodeContext>>> write) {
        if (type == String.class) {
            read.add((object, dataInput) -> setter.accept(object, () -> {
				try {
					return readString(dataInput);
				} catch (UncheckedIOException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return object;
			}));
            write.add((object, dataOutput) -> {
				try {
					writeString(dataOutput, (String) getter.apply(object));
				} catch (UncheckedIOException | IOException e) {
					throw new RuntimeException("Critical error: " + e.getMessage(), e);
					}
			});
        } else if (getAnnotation.apply(FunctionParams.class) != null) {
            read.add((object, dataInput) -> setter.accept(object, () -> {
				try {
					return readFunctionParams(dataInput);
				} catch (UncheckedIOException | IOException e) {
					throw new RuntimeException("Erro crítico no parser: " + e.getMessage(), e);
				}
			}));
            write.add((object, dataOutput) -> {
				try {
					writeFunctionParams(dataOutput, (Token[]) getter.apply(object));
				} catch (UncheckedIOException | IOException e) {
					throw new RuntimeException("Erro crítico no parser: " + e.getMessage(), e);
				}
			});
        } else {
            super.serializer(type, getter, setter, getAnnotation, read, write);
        }
    }

    private static String readString(L2DataInput input) throws UncheckedIOException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = input.readUnsignedByte()) != 0) {
            baos.write(b);
        }
        return new String(baos.toByteArray(), input.getCharset());
    }

    private static void writeString(ObjectOutput<BytecodeContext> output, String string) throws UncheckedIOException, IOException {
        try {
			output.writeBytes((string + '\0').getBytes(output.getCharset()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private static Token readToken(ObjectInput<BytecodeContext> input) throws UncheckedIOException, IOException {
        return input.readObject(Token.class);
    }

    private static Token[] readFunctionParams(ObjectInput<BytecodeContext> input) throws UncheckedIOException, IOException {
        List<Token> tokens = new ArrayList<>();
        Token tmp;
        do {
            tmp = readToken(input);
            if (tmp instanceof EndFunctionParams) {
                break;
            }
            tokens.add(tmp);
        } while (true);
        return tokens.toArray(new Token[0]);
    }

    private static void writeFunctionParams(ObjectOutput<BytecodeContext> output, Token[] params) throws UncheckedIOException, IOException {
        for (Token token : params) {
            output.write(token);
        }
        output.write(new EndFunctionParams());
    }

    public static int getNoneInd(BytecodeContext context) {
        return context.getUnrealPackage().nameReference("None");
    }

    private static void register(Class<? extends Token> clazz) {
        if (clazz == null) return;

        try {
            // Pega o campo OPCODE da classe (Ex: public static final int OPCODE = 0x3E)
            java.lang.reflect.Field field = clazz.getDeclaredField("OPCODE");
            field.setAccessible(true);
            int opcode = field.getInt(null);

            // Decide a tabela pela anotação
            boolean isConv = clazz.isAnnotationPresent(ConversionToken.class);
            Map<Integer, Class<? extends Token>> table = isConv ? conversionTokenTable : mainTokenTable;

            table.put(opcode, clazz);
        } catch (Exception e) {
            // Ignora classes que não possuem o campo OPCODE
        }
    }

    static {
        // Registra os tokens percorrendo os seus Enums
        for (UnrealOpcode.Main op : UnrealOpcode.Main.values()) register(op.getBytecode());
        for (UnrealOpcode.Conversion op : UnrealOpcode.Conversion.values()) register(op.getBytecode());
        
        // Registro explícito do wrapper 0x39
        register(ConversionTable.class);
    }
    
    private Token instantiate(ObjectInput<BytecodeContext> input) throws IOException {
        int opcode = input.readUnsignedByte();
        
        // Pega o estado do contexto ANTES de decidir a tabela
        boolean isConversionMode = input.getContext().isConversion();

        // Nativos (Apenas na Main)
        if (!isConversionMode && opcode >= EX_ExtendedNative) {
            return readNativeCall(input, opcode);
        }

        // Busca a classe no mapa baseado no modo ATUAL
        Class<? extends Token> tokenClass = isConversionMode ? 
            conversionTokenTable.get(opcode) : mainTokenTable.get(opcode);

        if (tokenClass == null) {
            String table = isConversionMode ? "Conversion" : "Main";
            throw new IOException(String.format("Unknown token: %02x, table: %s", opcode, table));
        }

        Token token;
        try {
            token = tokenClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new SerializerException(e);
        }

        // LÓGICA DE TRANSIÇÃO (Igual ao original)
        if (opcode == 0x39) {
            input.getContext().changeConversion();
        } else if (isConversionMode && token.getClass().isAnnotationPresent(ConversionToken.class)) {
            input.getContext().changeConversion();
        }

        return token;
    }

	/**
	 * @return the log
	 */
	public static Logger getLog() {
		return log;
	}
}