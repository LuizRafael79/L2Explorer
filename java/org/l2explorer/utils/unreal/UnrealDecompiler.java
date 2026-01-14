/*
 * Copyright (c) 2026 Galagard/L2Explorer
 */
package org.l2explorer.utils.unreal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.l2explorer.app.ExplorerPanel;
import org.l2explorer.io.L2DataInput;
import org.l2explorer.io.SerializerFactory;
import org.l2explorer.io.UnrealObjectInput;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.UnrealPackage.ExportEntry;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.UnrealSerializerFactory;
import org.l2explorer.unreal.bytecode.BytecodeContext;
import org.l2explorer.unreal.bytecode.TokenSerializerFactory;
import org.l2explorer.unreal.bytecode.token.Token;
import org.l2explorer.utils.bytecode.BytecodeDecompiler;

/**
 * Decompiler completo e integrado. 
 * Resolve herança, organiza membros por categoria e extrai Default Properties.
 */
public class UnrealDecompiler {
    private final UnrealPackage up = null;
    private ExplorerPanel.DebugConsole console;
	@SuppressWarnings("unused")
	private byte[] rawData;

	/**
	 * Realiza o decompile de uma entrada de exportação da Unreal.
	 * Baseado no fluxo de leitura de bytecode para InterfaceSamurai.u.
	 * * @param entry A entrada a ser processada.
	 * @return O código descompilado ou mensagem de erro.
	 * @throws IOException 
	 */
	/**
	 * Realiza o decompile de uma entrada de exportação da Unreal.
	 * Ajustado para alinhar o bytecode na crônica Samurai Crow (Classic).
	 */
	@SuppressWarnings("unchecked")
	public String decompile(UnrealPackage.ExportEntry entry) throws IOException {
	    if (entry == null) return "";
	    
	    byte[] rawData = entry.getObjectRawData();
	    // Funções muito pequenas geralmente são nativas ou vazias
	    if (rawData == null || rawData.length < 24) return "// NATIVE FUNCTION OR EMPTY BODY";

	    StringBuilder sb = new StringBuilder();
	    BytecodeContext bcContext = new BytecodeContext(entry.getUnrealPackage());
	    TokenSerializerFactory tokenFactory = new TokenSerializerFactory();
	    UnrealSerializerFactory unrealFactory = new UnrealSerializerFactory(null);
	    UnrealRuntimeContext runtimeCtx = new UnrealRuntimeContext(entry, unrealFactory);

	    try (ByteArrayInputStream bais = new ByteArrayInputStream(rawData)) {
	        // Utilizamos a sua fábrica estática da interface
	        L2DataInput di = L2DataInput.dataInput(bais, entry.getUnrealPackage().getFile().getCharset());

	        // --- ALINHAMENTO DO CABEÇALHO (SAMURAI CROW) ---
	        di.readCompactInt(); // NativeIndex
	        di.readCompactInt(); // NodeIndex (Next)
	        
	        // Na Samurai Crow, as flags de função ocupam 4 bytes fixos.
	        // Se o ScriptSize continuar vindo errado, aumente este skip para 8 ou 12.
	        di.skip(4); 
	        
	        // Lemos o ScriptSize real (4 bytes Little-Endian)
	        int scriptSize = di.readInt();

	        System.out.println("DEBUG: [" + entry.getObjectName().getName() + "] ScriptSize: " + scriptSize);
	            
	        // Validação: o tamanho do script não pode ser maior que o array de bytes
	        if (scriptSize > 0 && scriptSize < rawData.length) {
	        	// Fazemos um cast bruto para a interface genérica, ignorando os avisos do Java
	        	@SuppressWarnings({ "rawtypes" })
	        	SerializerFactory factory = (SerializerFactory) tokenFactory;

	        	// Agora passamos a factory para o UnrealObjectInput
	        	UnrealObjectInput<UnrealRuntimeContext> input = new UnrealObjectInput<UnrealRuntimeContext>(di, factory, runtimeCtx);
	            int readSize = 0;
	            // Loop de leitura de tokens até atingir o ScriptSize ou o fim dos bytes
	            while (readSize < scriptSize && bais.available() > 0) {
	                try {
	                    // Lemos o token (fazendo cast para Token)
	                    Token t = (Token) input.readObject(Token.class);
	                    
	                    if (t != null) {
	                        sb.append(t.toString(runtimeCtx)).append("\n");
	                        readSize += t.getSize(bcContext);
	                    }
	                } catch (Exception e) {
	                    sb.append("// Error at offset " + readSize + ": " + e.getMessage() + "\n");
	                    break;
	                }
	            }
	        } else {
	            sb.append("// Could not align bytecode. ScriptSize: ").append(scriptSize);
	        }

	    } catch (Exception e) {
	        sb.append("// Critical Error: ").append(e.getMessage());
	        e.printStackTrace();
	    }

	    return sb.toString();
	}
	
    /**
     * Monta a estrutura completa da classe (.uc)
     */
    @SuppressWarnings("unused")
	private String decompileClass(ExportEntry entry) {
        StringBuilder sb = new StringBuilder();
        String superName = "Object";

        try {
            Object superObj = entry.getObjectSuperClass();
            UnrealPackage.Entry<?> superEntry = null;

            if (superObj instanceof UnrealPackage.Entry) {
                superEntry = (UnrealPackage.Entry<?>) superObj;
                if (superEntry.getObjectName() != null) {
                    superName = superEntry.getObjectName().getName();
                }
            }
            
            if ((superName.equals("Index") || superName.equals("None")) && superEntry != null) {
                superName = superEntry.getObjectName().getName(); 
            }
        } catch (Exception e) {
            superName = "Object";
        }

        // Header da Classe
        sb.append("// Class: ").append(entry.getObjectFullName()).append("\n");
        sb.append("class ").append(entry.getObjectName().getName());
        sb.append(" extends ").append(superName).append(";\n\n");

        // Busca todos os membros da classe (filhos na ExportTable)
        int parentIndex = entry.getIndex() + 1;
        List<ExportEntry> children = up.getExportTable().stream()
                .filter(e -> e.getObjectPackage() != null && e.getObjectPackage().getIndex() + 1 == parentIndex)
                .collect(Collectors.toList());

        // 1. Enums
        for (ExportEntry e : children) {
            if (e.getFullClassName().contains("Enum")) {
                sb.append(decompileEnum(e)).append("\n");
            }
        }
        
        // 2. Structs
        for (ExportEntry s : children) {
            if (s.getFullClassName().contains("Struct")) {
                sb.append(decompileStruct(s)).append("\n");
            }
        }

        // 3. Variables (Properties)
        for (ExportEntry v : children) {
            if (v.getFullClassName().contains("Property")) {
                sb.append(decompileProperty(v)).append("\n");
            }
        }

        // 4. Functions
        sb.append("\n");
        for (ExportEntry f : children) {
            if (f.getFullClassName().contains("Function")) {
                sb.append(decompileFunction(f)).append("\n");
            }
        }

        // 5. Default Properties
        sb.append(decompileDefaultProperties(entry));

        return sb.toString();
    }

    /**
     * Extrai as variáveis padrão (incluindo Copyright da Samurai Crow)
     */
    private String decompileDefaultProperties(ExportEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("\ndefaultproperties\n{\n");

        try {
            byte[] rawData = entry.getObjectRawData();
            if (rawData == null || rawData.length < 10) return "";

            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(rawData);
            
            // --- O PONTO EXATO ---
            // 1. Ler o ScriptSize que está no cabeçalho do RawData da classe
            // No L2 Classic, o cabeçalho costuma ter: SuperIndex, NextIndex, e depois o ScriptSize
            org.l2explorer.utils.StreamsHelper.readCompactInt(bais); // Super
            org.l2explorer.utils.StreamsHelper.readCompactInt(bais); // Next
            
            // Os próximos 4 bytes (Int32) costumam ser o tamanho do Bytecode
            byte[] sizeBytes = new byte[4];
            bais.read(sizeBytes);
            int scriptSize = java.nio.ByteBuffer.wrap(sizeBytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();

            // 2. O PULO DO GATO: As propriedades começam após o Script e as Flags
            // Pulamos o ScriptSize + as Flags de Objeto (geralmente 4 a 8 bytes)
            if (scriptSize > 0 && scriptSize < rawData.length) {
                bais.skip(scriptSize + 4); 
            } else {
                // Se não houver script, pulamos o cabeçalho padrão de 28-32 bytes
                bais.skip(28);
            }

            // Agora o ponteiro está sincronizado com a PropertyTable
            while (bais.available() > 0) {
                int nameIdx = org.l2explorer.utils.StreamsHelper.readCompactInt(bais);
                
                // 0x00 (None) é o terminador universal de propriedades na Unreal
                if (nameIdx <= 0 || nameIdx >= up.getNameTable().size()) break;

                String propName = up.nameReference(nameIdx);
                if (propName.equals("None")) break;

                int info = bais.read();
                int type = (info & 0x0F); 

                // Processamento dos tipos (Strings, Objetos, etc.)
                if (type == 3 || type == 6 || type == 13) {
                    String value = readUnrealString(bais);
                    sb.append("\t").append(propName).append("=\"").append(value).append("\"\n");
                } else if (type == 2) { // Int
                    byte[] b = new byte[4]; bais.read(b);
                    int val = java.nio.ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
                    sb.append("\t").append(propName).append("=").append(val).append("\n");
                } else {
                    skipUnknownProperty(bais, type, info);
                    sb.append("\t").append(propName).append("=// Type ").append(type).append("\n");
                }
            }
        } catch (Exception e) {
            sb.append("\t// Alignment Error: ").append(e.getMessage()).append("\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    /**
     * Método auxiliar para não perder o alinhamento ao encontrar tipos desconhecidos
     */
    private void skipUnknownProperty(java.io.ByteArrayInputStream bais, int type, int info) {
        int sizeType = (info & 0x70) >> 4;
        int size = 0;
        switch(sizeType) {
            case 0: size = 1; break;
            case 1: size = 2; break;
            case 2: size = 4; break;
            case 3: size = 12; break;
            case 4: size = 16; break;
            case 5: size = bais.read(); break;
            case 6: size = (bais.read() | (bais.read() << 8)); break;
            case 7: size = (bais.read() | (bais.read() << 8) | (bais.read() << 16) | (bais.read() << 24)); break;
        }
        for(int i=0; i<size; i++) bais.read();
    }

    private String readUnrealString(java.io.ByteArrayInputStream bais) throws IOException {
        int length = org.l2explorer.utils.StreamsHelper.readCompactInt(bais);
        if (length <= 0) return "";
        byte[] strBytes = new byte[length];
        int read = bais.read(strBytes, 0, length);
        return new String(strBytes, 0, read).trim();
    }
    
    private String decompileProperty(ExportEntry entry) {
        String name = entry.getObjectName().getName();
        String propType = entry.getFullClassName().replace("Core.", "").replace("Property", "").toLowerCase();
        return "var " + propType + " " + name + ";";
    }

    private String decompileStruct(ExportEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("struct ").append(entry.getObjectName().getName()).append("\n{\n");
        
        int parentIndex = entry.getIndex() + 1;
        up.getExportTable().stream()
                .filter(e -> e.getObjectPackage() != null && e.getObjectPackage().getIndex() + 1 == parentIndex)
                .forEach(v -> sb.append("\t").append(decompileProperty(v)).append("\n"));
        
        sb.append("};\n");
        return sb.toString();
    }

    private String decompileEnum(ExportEntry entry) {
        return "enum " + entry.getObjectName().getName() + " { /* Enumeration list */ };\n";
    }

    private String decompileFunction(ExportEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("function ").append(entry.getObjectName().getName()).append("()\n{\n");
        try {
            byte[] rawData = entry.getObjectRawData();
            if (rawData.length > 32) {
                BytecodeDecompiler bcd = new BytecodeDecompiler(rawData, up, console); 
                bcd.setPC(12); // O pulo mágico que configuramos
                sb.append(bcd.decompile());
            } else {
                sb.append("\t// Native function body\n");
            }
        } catch (Exception e) {
            sb.append("\t// Error decompiling bytecode\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

	/**
	 * @return the console
	 */
	public ExplorerPanel.DebugConsole getConsole() {
		return console;
	}

	/**
	 * @param console the console to set
	 */
	public void setConsole(ExplorerPanel.DebugConsole console) {
		this.console = console;
	}
}