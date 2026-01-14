/*
 * Copyright (c) 2026 Galagard/L2Explorer
 */
package org.l2explorer.utils.unreal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.l2explorer.app.ExplorerPanel;
import org.l2explorer.io.L2DataInput;
import org.l2explorer.io.SerializerFactory;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.UnrealPackage.ExportEntry;
import org.l2explorer.unreal.Environment;
import org.l2explorer.unreal.SimpleEnv;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.UnrealSerializerFactory;
import org.l2explorer.unreal.bytecode.BytecodeContext;
import org.l2explorer.unreal.bytecode.TokenSerializerFactory;
import org.l2explorer.unreal.bytecode.token.Token;

/**
 * Decompiler completo e integrado. 
 * Resolve herança, organiza membros por categoria e extrai Default Properties.
 */
public class UnrealDecompiler {
    private final UnrealPackage up = null;
    private ExplorerPanel.DebugConsole console;
    @SuppressWarnings("unused")
    private byte[] rawData;
    private File baseDir;

    public void setBaseDir(File dir) {
        this.baseDir = dir;
    }

    public ExplorerPanel.DebugConsole getConsole() {
        return console;
    }

    public void setConsole(ExplorerPanel.DebugConsole console) {
        this.console = console;
    }

    // ============================ FUNÇÃO ADAPTADA AO ACMI ============================
    public String decompileFunctionACMI(ExportEntry entry) throws IOException {
        if (!entry.getFullClassName().equals("Core.Function")) {
            return "// Not a function";
        }

        byte[] entryBytes = entry.getObjectRawData();
        if (entryBytes == null || entryBytes.length == 0) {
            return "// Native or empty function";
        }

        UnrealPackage up = entry.getUnrealPackage();
        BytecodeContext context = new BytecodeContext(up);
        TokenSerializerFactory tokenFactory = new TokenSerializerFactory();

        UnrealRuntimeContext runtimeCtx = new UnrealRuntimeContext(
                entry,
                new UnrealSerializerFactory(new SimpleEnv(up))
        );

        StringBuilder sb = new StringBuilder();

        try (ByteArrayInputStream bais = new ByteArrayInputStream(entryBytes)) {

            // Cria o ObjectInput no estilo ACMI
        	L2DataInput di = L2DataInput.dataInput(bais, entry.getUnrealPackage().getFile().getCharset());

        	org.l2explorer.io.UnrealObjectInput<BytecodeContext> input =
        	        new org.l2explorer.io.UnrealObjectInput<BytecodeContext>(di, tokenFactory, context);

        	int readSize = 0; // declarar antes do loop

            // Lista de tokens
            List<Token> tokens = new ArrayList<>();
            while (true) {
            	try {
            	    Token t = input.readObject(Token.class);
            	    if (t == null) break;
            	    tokens.add(t);
            	} catch (IOException e) {
            	    sb.append("// Unknown token at offset ").append(readSize)
            	      .append(": ").append(e.getMessage()).append("\n");
            	    break;
            	}

            }

            // Converte tokens para string
            for (Token t : tokens) {
                sb.append(t.toString(runtimeCtx)).append("\n");
            }

        } catch (Exception e) {
            sb.append("// Critical Error: ").append(e.getMessage()).append("\n");
        }

        return sb.toString();
    }




    // ============================ DECOMPILA CLASSE COMPLETA ============================
    public String decompileClassComplete(ExportEntry classEntry) throws IOException {
        StringBuilder sb = new StringBuilder();
        UnrealPackage up = classEntry.getUnrealPackage();

        // --- HEADER DA CLASSE ---
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

        // --- BUSCA MEMBROS ---
        int parentRef = classEntry.getObjectReference();
        List<ExportEntry> children = up.getExportTable().stream()
                .filter(e -> {
                    UnrealPackage.Entry<?> pkg = e.getObjectPackage();
                    return pkg != null && pkg.getObjectReference() == parentRef;
                })
                .collect(Collectors.toList());

        System.out.println("Found " + children.size() + " members in " + classEntry.getObjectName().getName());

        // --- SEPARA POR TIPO ---
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

        // --- ENUMS ---
        if (!enums.isEmpty()) {
            sb.append("//==============================================================================\n");
            sb.append("// Enums\n");
            sb.append("//==============================================================================\n");
            for (ExportEntry e : enums) {
                sb.append("enum ").append(e.getObjectName().getName()).append(" { /* TODO */ };\n");
            }
            sb.append("\n");
        }

        // --- STRUCTS ---
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

        // --- PROPERTIES ---
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

        // --- FUNCTIONS ---
        if (!functions.isEmpty()) {
            sb.append("//==============================================================================\n");
            sb.append("// Functions\n");
            sb.append("//==============================================================================\n\n");
            for (ExportEntry func : functions) {
                sb.append("function ").append(func.getObjectName().getName()).append("()\n");
                sb.append("{\n");

                // --- DECOMPILA USANDO ACMI ---
                try {
                    String bytecode = decompileFunctionACMI(func);
                    if (bytecode != null && !bytecode.isEmpty()) {
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
}