/*
 * Copyright (c) 2026 Galagard/L2Explorer
 */
package org.l2explorer.utils.unreal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.l2explorer.app.ExplorerPanel;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.UnrealPackage.Entry;
import org.l2explorer.io.UnrealPackage.ExportEntry;
import org.l2explorer.unreal.Environment;
import org.l2explorer.unreal.SimpleEnv;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.UnrealSerializerFactory;
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
	/**
	 * Decompila UMA função individual (Core.Function)
	 */
	public String decompile(UnrealPackage.ExportEntry entry) throws IOException {
	    if (!entry.getFullClassName().equals("Core.Function")) {
	        return "// Not a function";
	    }

	    try {
	        // Cria um Environment simples apenas com o package atual
	        File packageFile = new File(entry.getUnrealPackage().getPackageName());
//	        Environment env = new Environment(packageFile.getParentFile(), Collections.emptyList());
	        
	        SimpleEnv env = new SimpleEnv(entry.getUnrealPackage());
	        
	        UnrealSerializerFactory factory = new UnrealSerializerFactory(env);
	        
	        org.l2explorer.unreal.core.Function func = 
	            (org.l2explorer.unreal.core.Function) factory.getOrCreateObject(entry);
	        
	        Token[] bytecode = func.getBytecode();
	        
	        if (bytecode == null || bytecode.length == 0) {
	            return "// Native or empty function";
	        }
	        
	        StringBuilder sb = new StringBuilder();
	        UnrealRuntimeContext ctx = new UnrealRuntimeContext(entry, factory);
	        
	        for (Token token : bytecode) {
	            sb.append(token.toString(ctx)).append("\n");
	        }
	        
	        return sb.toString();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "// Error: " + e.getMessage();
	    }
	}
	
/**
 * Decompila uma CLASSE completa com todas as suas funções
 */
public String decompileClassComplete(ExportEntry classEntry) throws IOException {
    StringBuilder sb = new StringBuilder();
    UnrealPackage up = classEntry.getUnrealPackage();
    
    // 1. HEADER DA CLASSE
    String superName = "Object";
    try {
        Object superObj = classEntry.getObjectSuperClass();
        if (superObj instanceof UnrealPackage.Entry) {
            UnrealPackage.Entry<?> superEntry = (UnrealPackage.Entry<?>) superObj;
            if (superEntry.getObjectName() != null) {
                superName = superEntry.getObjectName().getName();
            }
        }
    } catch (Exception e) {
        superName = "Object";
    }

    sb.append("//==============================================================================\n");
    sb.append("// ").append(classEntry.getObjectFullName()).append("\n");
    sb.append("//==============================================================================\n");
    sb.append("class ").append(classEntry.getObjectName().getName());
    sb.append(" extends ").append(superName).append(";\n\n");

    // 2. BUSCA TODOS OS MEMBROS (children) DESTA CLASSE
    int parentRef = classEntry.getObjectReference();
    List<ExportEntry> children = up.getExportTable().stream()
            .filter(e -> {
                Entry<?> pkg = e.getObjectPackage();
                return pkg != null && pkg.getObjectReference() == parentRef;
            })
            .collect(Collectors.toList());

    System.out.println("Found " + children.size() + " members in " + classEntry.getObjectName().getName());

    // 3. SEPARA POR TIPO
    List<ExportEntry> enums = new ArrayList<>();
    List<ExportEntry> structs = new ArrayList<>();
    List<ExportEntry> properties = new ArrayList<>();
    List<ExportEntry> functions = new ArrayList<>();
    
    for (ExportEntry child : children) {
        String className = child.getFullClassName();
        if (className.contains("Enum")) {
            enums.add(child);
        } else if (className.contains("Struct")) {
            structs.add(child);
        } else if (className.contains("Property")) {
            properties.add(child);
        } else if (className.equals("Core.Function")) {
            functions.add(child);
        }
    }

    // 4. ENUMS
    if (!enums.isEmpty()) {
        sb.append("//==============================================================================\n");
        sb.append("// Enums\n");
        sb.append("//==============================================================================\n");
        for (ExportEntry e : enums) {
            sb.append("enum ").append(e.getObjectName().getName()).append(" { /* TODO */ };\n");
        }
        sb.append("\n");
    }

    // 5. STRUCTS
    if (!structs.isEmpty()) {
        sb.append("//==============================================================================\n");
        sb.append("// Structs\n");
        sb.append("//==============================================================================\n");
        for (ExportEntry s : structs) {
            sb.append("struct ").append(s.getObjectName().getName()).append("\n{\n");
            sb.append("\t// TODO: struct members\n");
            sb.append("};\n\n");
        }
    }

    // 6. PROPERTIES
    if (!properties.isEmpty()) {
        sb.append("//==============================================================================\n");
        sb.append("// Properties\n");
        sb.append("//==============================================================================\n");
        for (ExportEntry p : properties) {
            String propType = p.getFullClassName().replace("Core.", "").replace("Property", "").toLowerCase();
            sb.append("var ").append(propType).append(" ").append(p.getObjectName().getName()).append(";\n");
        }
        sb.append("\n");
    }

    // 7. FUNCTIONS - AQUI É O IMPORTANTE!
    if (!functions.isEmpty()) {
        sb.append("//==============================================================================\n");
        sb.append("// Functions\n");
        sb.append("//==============================================================================\n\n");
        for (ExportEntry func : functions) {
            sb.append("function ").append(func.getObjectName().getName()).append("()\n");
            sb.append("{\n");
            
            // DECOMPILA O BYTECODE DA FUNÇÃO
            try {
                String bytecode = decompile(func);
                if (bytecode != null && !bytecode.isEmpty()) {
                    // Indenta cada linha
                    for (String line : bytecode.split("\n")) {
                        sb.append("\t").append(line).append("\n");
                    }
                }
            } catch (Exception e) {
                sb.append("\t// Error decompiling: ").append(e.getMessage()).append("\n");
            }
            
            sb.append("}\n\n");
        }
    }

    return sb.toString();
}

    /**
     * Método auxiliar para não perder o alinhamento ao encontrar tipos desconhecidos
     */
    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
	private String decompileEnum(ExportEntry entry) {
        return "enum " + entry.getObjectName().getName() + " { /* Enumeration list */ };\n";
    }

    @SuppressWarnings("unused")
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
    
    @SuppressWarnings("unused")
	private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
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