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
					return instantiate(arg0);
				} catch (UncheckedIOException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return arg0;
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

    private static void register(Class<? extends Token> clazz, int opcode, boolean conversion) {
        if (clazz == null) {
            System.err.println("AVISO: Classe nula para opcode 0x" + Integer.toHexString(opcode));
            return;
        }
        Map<Integer, Class<? extends Token>> table = conversion ? conversionTokenTable : mainTokenTable;
        table.put(opcode, clazz);
    }

    static {
        try {
            // Registra Main
            for (UnrealOpcode.Main op : UnrealOpcode.Main.values()) {
                if (op == UnrealOpcode.Main.CONVERSION_TABLE) continue;
                register(op.getBytecode(), op.getOpcode(), false);
            }

            // Registra Conversion
            for (UnrealOpcode.Conversion op : UnrealOpcode.Conversion.values()) {
                register(op.getBytecode(), op.getOpcode(), true);
            }

            Class<? extends Token> ctClass = UnrealOpcode.Main.CONVERSION_TABLE.getBytecode();
            int ctOpcode = UnrealOpcode.Main.CONVERSION_TABLE.getOpcode();
            register(ctClass, ctOpcode, false);
            register(ctClass, ctOpcode, true);
            
            System.out.println("DEBUG: Tokens registrados! Main: " + mainTokenTable.size() + " | Conversion: " + conversionTokenTable.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("deprecation")
    private Token instantiate(ObjectInput<BytecodeContext> input) throws IOException {
        int opcode = input.readUnsignedByte();
        
        // 1. TRATAMENTO PRÉ-BUSCA: Se for o sinalizador 0x39, mude para conversão AGORA
        if (opcode == 0x39 && !input.getContext().isConversion()) {
            input.getContext().changeConversion();
        }

        // 2. Determina o modo atual (pode ter mudado acima)
        boolean isConversionMode = input.getContext().isConversion();

        // 3. Nativos (Apenas se não for conversão e não for o 0x39)
        if (!isConversionMode && opcode >= EX_ExtendedNative) {
            return readNativeCall(input, opcode);
        }

        // 4. Busca a classe no mapa correto
        Class<? extends Token> tokenClass = isConversionMode ? 
            conversionTokenTable.get(opcode) : mainTokenTable.get(opcode);

        if (tokenClass == null) {
            String tableName = isConversionMode ? "Conversion" : "Main";
            throw new IOException(String.format("Unknown token: %02x, table: %s", opcode, tableName));
        }

        // 5. Instanciação
        Token token;
        try {
            token = tokenClass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new SerializerException(e);
        }

        // 6. TRATAMENTO PÓS-INSTÂNCIA: Se o token lido for de conversão (e não o 0x39), volte para Main
        // Isso garante que o próximo token lido após o VectorToRotator seja da Main
        if (isConversionMode && opcode != 0x39 && token.getClass().isAnnotationPresent(ConversionToken.class)) {
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