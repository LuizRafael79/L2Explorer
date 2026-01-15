/*
 * Copyright (c) 2026 Galagard/L2Explorer
 */
package org.l2explorer.utils.unreal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.l2explorer.app.ExplorerPanel;
import org.l2explorer.io.L2DataInput;
import org.l2explorer.io.ObjectInput;
import org.l2explorer.io.Serializer;
import org.l2explorer.io.UnrealObjectInput;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.UnrealPackage.ExportEntry;
import org.l2explorer.unreal.SimpleEnv;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.UnrealSerializerFactory;
import org.l2explorer.unreal.bytecode.BytecodeContext;
import org.l2explorer.unreal.bytecode.TokenSerializerFactory;
import org.l2explorer.unreal.bytecode.token.Token;
import org.l2explorer.unreal.core.Field;

/**
 * Decompiler completo e integrado. 
 * Resolve herança, organiza membros por categoria e extrai Default Properties.
 */
public class UnrealDecompiler {
    private UnrealPackage up;
    private ExplorerPanel.DebugConsole console;
    @SuppressWarnings("unused")
    private byte[] rawData;
    private File baseDir;
    
    public UnrealDecompiler(UnrealPackage up, File baseDir) {
        this.up = up;
        this.baseDir = baseDir;
    }

    // --- GETTERS E SETTERS (FORA DO CONSTRUTOR) ---

    public UnrealPackage getUp() {
        return up;
    }

    public void setUp(UnrealPackage up) {
        this.up = up;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public ExplorerPanel.DebugConsole getConsole() {
        return console;
    }

    public void setConsole(ExplorerPanel.DebugConsole console) {
        this.console = console;
    }        

    public String decompileFunction(ExportEntry entry) throws IOException {
        if (entry.getObjectRawData() == null) return "// Native or empty";

        UnrealPackage up = entry.getUnrealPackage();
        BytecodeContext context = new BytecodeContext(up);
        TokenSerializerFactory tokenFactory = new TokenSerializerFactory();
        
        // Contexto para transformar Token em Texto (UnrealScript)
        UnrealSerializerFactory serializerFactory = new UnrealSerializerFactory(new SimpleEnv(up));
        UnrealRuntimeContext runtimeCtx = new UnrealRuntimeContext(entry, serializerFactory);

        StringBuilder sb = new StringBuilder();
        byte[] entryBytes = entry.getObjectRawData();

        try (ByteArrayInputStream bais = new ByteArrayInputStream(entryBytes)) {
            // O ObjectInput do ACMI já gerencia o Charset e os Tokens
        	L2DataInput di = L2DataInput.dataInput(bais, up.getFile().getCharset());

        	// 2. Explicitamos o tipo genérico <BytecodeContext> na instanciação
        	UnrealObjectInput<BytecodeContext> input = 
        	    new UnrealObjectInput<BytecodeContext>(
        	        di, 
        	        tokenFactory, 
        	        context
        	    );

            // --- PULA O CABEÇALHO (Conforme o Teste do ACMI) ---
            if (!entry.getFullClassName().equalsIgnoreCase("Core.Class")) {
                input.readCompactInt(); // superField
            }
            input.readCompactInt(); // next
            input.readCompactInt(); // scriptText
            input.readCompactInt(); // child
            input.readCompactInt(); // friendlyName
            input.readCompactInt(); // cppText
            input.readCompactInt(); // cppText (sim, o teste pula duas vezes)
            input.readInt();        // line
            input.readInt();        // textPos
            
            // AQUI ESTÁ O SEGREDO:
            int bytecodeSize = input.readInt(); 
            int processedSize = 0;

            // --- LEITURA DOS TOKENS ---
            while (processedSize < bytecodeSize) {
                try {
                    Token token = input.readObject(Token.class);
                    if (token == null) break;

                    // Adiciona o comentário com o endereço (estilo decompiler profissional)
                    String hexAddr = String.format("/*0x%04x*/", processedSize);
                    sb.append(hexAddr).append("\t").append(token.toString(runtimeCtx)).append("\n");

                    processedSize += token.getSize(context);
                } catch (Exception e) {
                    sb.append("// Error at offset ").append(processedSize).append(": ").append(e.getMessage()).append("\n");
                    break;
                }
            }
        } catch (Exception e) {
            sb.append("// Critical Error: ").append(e.getMessage());
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
        List<ExportEntry> constants = new ArrayList<>();
        List<ExportEntry> enums = new ArrayList<>();
        List<ExportEntry> structs = new ArrayList<>();
        List<ExportEntry> properties = new ArrayList<>();
        List<ExportEntry> functions = new ArrayList<>();

        for (ExportEntry child : children) {
            String className = child.getFullClassName();
            if (className.contains("Const")) {
                constants.add(child);            
            }else if (className.contains("Enum")) {
                enums.add(child);
            } else if (className.contains("Struct")) {
                structs.add(child);
            } else if (className.contains("Property")) {
                properties.add(child);
            } else if (className.equals("Core.Function")) {
                functions.add(child);
            }
        }

     // --- CONST ---
        if (!constants.isEmpty()) {
            sb.append("//==============================================================================\n");
            sb.append("// Const\n");
            sb.append("//==============================================================================\n");
            for (ExportEntry c : constants) {
                String name = c.getObjectName().getName();
                String value = getConstantValue(c); // Chama o helper novo
                
                sb.append("const ").append(name).append(" = ").append(value).append(";\n");
            }
            sb.append("\n");
        }
        
        // --- ENUMS ---
        if (!enums.isEmpty()) {
            sb.append("//==============================================================================\n");
            sb.append("// Enums\n");
            sb.append("//==============================================================================\n");
            for (ExportEntry e : enums) {
                String propType = e.getFullClassName().replace("Core.", "").replace("Enums", "").toLowerCase();
                sb.append("enum ").append(propType).append(" ").append(e.getObjectName().getName()).append(";\n");
            }
            sb.append("\n");
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
        }

        // --- STRUCTS ---
        if (!structs.isEmpty()) {
            for (ExportEntry s : structs) {
                String propType = s.getFullClassName().replace("Core.", "").replace("Structs", "").toLowerCase();
                sb.append("var ").append(propType).append(" ").append(s.getObjectName().getName()).append(";\n");
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
                    String bytecode = decompileFunction(func);
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
    
    /**
     * Extrai o valor de uma constante buscando pela Tag "Value".
     * Se falhar, utiliza o scanBruto para pescar o dado no binário.
     * @throws IOException 
     */
    private String getConstantValue(ExportEntry c) throws IOException {
        try {
            byte[] data = c.getObjectRawData();
            if (data == null || data.length == 0) return "0";

            UnrealSerializerFactory sf = new UnrealSerializerFactory(new SimpleEnv(c.getUnrealPackage()));
            UnrealRuntimeContext context = new UnrealRuntimeContext(c, sf);

            try (java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(data)) {
                L2DataInput di = L2DataInput.dataInput(bais, c.getUnrealPackage().getFile().getCharset());
                ObjectInput<UnrealRuntimeContext> input = new UnrealObjectInput<>(di, sf, context);

                // 1. Pula metadados fixos do Field (superField e next)
                input.readCompactInt(); // Nome do objeto
                input.readCompactInt(); // superField
                input.readCompactInt(); // next

                // 2. TENTATIVA PADRÃO OURO (Tags)
                bais.mark(128); // Salva o ponto pós-header
                while (bais.available() > 0) {
                    int nameIdx = di.readCompactInt();
                    if (nameIdx <= 0) break;

                    String propName = c.getUnrealPackage().nameReference(nameIdx);
                    int info = di.readUnsignedByte();
                    int type = info & 0x0F;

                    if ("Value".equalsIgnoreCase(propName)) {
                        return switch (type) {
                            case 1 -> String.valueOf(di.readUnsignedByte());
                            case 2 -> String.valueOf(di.readInt());
                            case 11 -> c.getUnrealPackage().nameReference(di.readCompactInt());
                            case 12 -> di.readUTF();
                            default -> "0";
                        };
                    }
                    // Pula se não for Value (lógica simplificada para UConst)
                    int sizeFlag = (info & 0x70) >> 4;
                    if (sizeFlag < 5) di.skipBytes((int)Math.pow(2, sizeFlag));
                }

                // 3. TENTATIVA L2-RAW (Onde o 148/wX se esconde)
                // Se não achou a tag "Value", o L2 pode ter jogado o valor puro
                bais.reset();
                if (bais.available() > 0) {
                    // Tenta ler o valor como um CompactInt direto
                    int rawVal = di.readCompactInt();
                    // Se o valor for razoável, é ele.
                    if (rawVal != 0) return String.valueOf(rawVal);
                }
            }
        } catch (Exception e) {
            // Fallback silencioso
        }
        return scanBruto(c); // Passamos o ExportEntry para o scan ser mais inteligente
    }

    private String scanBruto(ExportEntry c) throws IOException {
        byte[] data = c.getObjectRawData();
        String objName = c.getObjectName().getName();
        String raw = new String(data, java.nio.charset.StandardCharsets.ISO_8859_1);
        
        // Regex mais agressiva: pega números ou palavras que não sejam o nome do objeto
        Matcher m = Pattern.compile("[a-zA-Z0-9]{1,}").matcher(raw);
        while (m.find()) {
            String match = m.group();
            // Ignora o nome da classe e o nome da constante
            if (match.equalsIgnoreCase("Const") || match.equalsIgnoreCase("Core") || 
                match.equalsIgnoreCase(objName) || match.length() < 1) {
                continue;
            }
            // Se for só uma letra "J", "B", "w", provavelmente é lixo de tag. 
            // Só aceitamos se for número ou string longa.
            if (match.length() == 1 && !Character.isDigit(match.charAt(0))) {
                continue;
            }
            return match;
        }
        return "0";
    }
}