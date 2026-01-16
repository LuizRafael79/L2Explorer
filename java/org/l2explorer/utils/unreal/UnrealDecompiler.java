/*
 * Copyright (c) 2026 Galagard/L2Explorer
 */
package org.l2explorer.utils.unreal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.Class;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.l2explorer.app.ExplorerPanel;
import org.l2explorer.io.L2DataInput;
import org.l2explorer.io.ObjectInput;
import org.l2explorer.io.Serializer;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.UnrealPackage.ExportEntry;
import org.l2explorer.unreal.SimpleEnv;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.UnrealSerializerFactory;
import org.l2explorer.unreal.bytecode.BytecodeContext;
import org.l2explorer.unreal.bytecode.TokenSerializerFactory;
import org.l2explorer.unreal.bytecode.token.Length;
import org.l2explorer.unreal.bytecode.token.Token;
import org.l2explorer.unreal.core.Field;
import org.l2explorer.unreal.core.Property;
import org.l2explorer.utils.IOUtil;
import org.l2explorer.utils.StreamsHelper;
import org.l2explorer.utils.enums.UnrealOpcode;

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
        UnrealPackage up = entry.getUnrealPackage();
        // Usamos o seu novo UnrealOpcode.java para o fallback
        UnrealSerializerFactory serializerFactory = new UnrealSerializerFactory(new SimpleEnv(up));
        StringBuilder sb = new StringBuilder();
        
        try {
            sb.append("// Entry class: ").append(entry.getFullClassName()).append("\n");
            
            // --- TENTATIVA 1: SERIALIZER OFICIAL (ACMI) ---
            Object obj = null;
            try {
                obj = serializerFactory.getOrCreateObject(entry);
            } catch (Exception e) {
                sb.append("// ⚠️ Serializer failed: ").append(e.getMessage()).append("\n");
                sb.append("// Falling back to Raw Bytecode Analysis...\n");
            }

            if (obj != null) {
                // ... (Seu código original de reflexão para o bytecode campo funciona aqui) ...
                // [Mantenha a lógica do field bytecode que você já tem]
            } else {
                // --- TENTATIVA 2: RAW DISASSEMBLER (SAMURAI CROW STYLE) ---
                // Se o serializer falhou, lemos os bytes brutos e usamos o seu Enum
                byte[] rawData = entry.getObjectRawData();
                sb.append(disassembleRawBytecode(rawData));
            }
            
        } catch (Exception e) {
            // Seu log de erro fatal atual...
        }
        return sb.toString();
    }

    /**
     * Disassembler de emergência usando o seu UnrealOpcode.Main.
     * Isso garante que você veja o código mesmo que o Serializer exploda.
     */
    private String disassembleRawBytecode(byte[] data) {
        StringBuilder dsb = new StringBuilder("// --- RAW DISASSEMBLY ---\n");
        // Em funções L2, os bytes de header (flags, etc) costumam ocupar os primeiros ~40 bytes
        // O bytecode real começa onde os tokens estão.
        for (int i = 0; i < data.length; i++) {
            int val = data[i] & 0xFF;
            UnrealOpcode.Main op = UnrealOpcode.Main.fromInt(val);
            
            if (op != null) {
                dsb.append(String.format("    0x%04X: %s (0x%02X)\n", i, op.getName(), val));
                // Adicione lógica de skip baseada no token se quiser (ex: se for Jump, pula 2 bytes)
            }
        }
        return dsb.toString();
    }

    /**
     * Versão RAW para debug - mostra TUDO sem interpretar
     */
    public String decompileFunctionRaw(ExportEntry entry) throws IOException {
        byte[] data = entry.getObjectRawData();
        if (data == null || data.length < 44) return "// Too small";
        
        StringBuilder sb = new StringBuilder();
        sb.append("// RAW BYTECODE (offset 44+):\n");
        
        int offset = 44;
        for (int i = offset; i < data.length; i++) {
            if ((i - offset) % 16 == 0) {
                sb.append(String.format("\n/*0x%04X*/  ", i - offset));
            }
            sb.append(String.format("%02X ", data[i] & 0xFF));
        }
        
        sb.append("\n\n// AS ASCII:\n");
        for (int i = offset; i < data.length; i++) {
            if ((i - offset) % 64 == 0) {
                sb.append("\n");
            }
            char c = (data[i] >= 32 && data[i] < 127) ? (char)data[i] : '.';
            sb.append(c);
        }
        
        return sb.toString();
    }

    /**
     * Diagnóstico completo da estrutura
     */
    public String diagnoseFunctionStructure(ExportEntry entry) throws IOException {
        byte[] data = entry.getObjectRawData();
        if (data == null) return "// No data";
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== FUNCTION DIAGNOSTIC ===\n");
        sb.append("Entry: ").append(entry.getObjectName().getName()).append("\n");
        sb.append("Class: ").append(entry.getFullClassName()).append("\n");
        sb.append("Size: ").append(data.length).append(" bytes\n\n");
        
        // Header completo
        sb.append("=== HEADER (0-64) ===\n");
        for (int i = 0; i < Math.min(64, data.length); i++) {
            if (i % 16 == 0) sb.append(String.format("\n%04X: ", i));
            sb.append(String.format("%02X ", data[i] & 0xFF));
        }
        
        // ASCII do header
        sb.append("\n\nASCII: ");
        for (int i = 0; i < Math.min(64, data.length); i++) {
            char c = (data[i] >= 32 && data[i] < 127) ? (char)data[i] : '.';
            sb.append(c);
        }
        
        // Candidatos a tamanho
        sb.append("\n\n=== SIZE CANDIDATES ===\n");
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            L2DataInput di = L2DataInput.dataInput(bais, entry.getUnrealPackage().getFile().getCharset());
            
            for (int offset = 32; offset <= 52; offset += 4) {
                if (offset + 4 > data.length) break;
                
                bais.reset();
                bais.skip(offset);
                int size = di.readInt();
                
                boolean valid = size > 0 && size <= data.length && 
                              (offset + 4 + size) <= data.length;
                
                sb.append(String.format("@%02d: %6d bytes %s", 
                                      offset, size, valid ? "✓" : "✗"));
                
                if (valid) {
                    sb.append(String.format(" (ends at %d/%d)", offset + 4 + size, data.length));
                }
                sb.append("\n");
            }
        }
        
        // Opcodes do bytecode
        sb.append("\n=== BYTECODE OPCODES (offset 44+) ===\n");
        int offset = 44;
        for (int i = offset; i < Math.min(offset + 64, data.length); i++) {
            if ((i - offset) % 16 == 0) {
                sb.append(String.format("\n%04X: ", i - offset));
            }
            int opcode = data[i] & 0xFF;
            sb.append(String.format("%02X ", opcode));
            
            // Destaca o 0x37
            if (opcode == 0x37) {
                sb.append("<LENGTH! ");
            }
        }
        
        sb.append("\n\n=== TOKEN PARSING TEST ===\n");
        testTokenParsing(entry, sb);
        
        return sb.toString();
    }

    /**
     * Testa parsing token por token com debug detalhado
     */
    private void testTokenParsing(ExportEntry entry, StringBuilder sb) {
        try {
            byte[] data = entry.getObjectRawData();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            L2DataInput di = L2DataInput.dataInput(bais, entry.getUnrealPackage().getFile().getCharset());
            
            UnrealPackage up = entry.getUnrealPackage();
            BytecodeContext context = new BytecodeContext(up);
            TokenSerializerFactory tokenFactory = new TokenSerializerFactory();
            UnrealSerializerFactory serializerFactory = new UnrealSerializerFactory(new SimpleEnv(up));
            UnrealRuntimeContext runtimeCtx = new UnrealRuntimeContext(entry, serializerFactory);
            
            bais.skip(44); // Pula header
            ObjectInput<BytecodeContext> input = ObjectInput.objectInput(di, tokenFactory, context);
            
            int tokenNum = 0;
            while (bais.available() > 0 && tokenNum < 10) { // Limita a 10 tokens
                int pos = data.length - 44 - bais.available();
                bais.mark(256);
                int opcode = bais.read() & 0xFF;
                bais.reset();
                
                sb.append(String.format("\nToken #%d @ 0x%04X, opcode=0x%02X", tokenNum, pos, opcode));
                
                try {
                    Token token = input.readObject(Token.class);
                    
                    if (token == null) {
                        sb.append(" -> NULL\n");
                        bais.skip(1);
                        tokenNum++;
                        continue;
                    }
                    
                    String className = token.getClass().getSimpleName();
                    sb.append(" -> ").append(className);
                    
                    // Se for Length, detalha o sub-token
                    if (token instanceof Length) {
                        Length lengthToken = 
                            (Length) token;
                        Token value = lengthToken.getValue();
                        sb.append("\n    └─ value: ");
                        if (value == null) {
                            sb.append("NULL (ERROR!)");
                        } else {
                            sb.append(value.getClass().getSimpleName());
                            sb.append(" = ").append(value.toString(runtimeCtx));
                        }
                    }
                    
                    String decompiled = token.toString(runtimeCtx);
                    sb.append("\n    Output: ").append(decompiled);
                    
                } catch (Exception e) {
                    sb.append(" -> ERROR: ").append(e.getClass().getSimpleName());
                    sb.append(": ").append(e.getMessage());
                    
                    // Mostra stack trace resumido
                    StackTraceElement[] stack = e.getStackTrace();
                    if (stack.length > 0) {
                        sb.append("\n    at ").append(stack[0]);
                    }
                    
                    bais.skip(1); // Pula byte problemático
                }
                
                tokenNum++;
            }
            
        } catch (Exception e) {
            sb.append("\nFATAL: ").append(e.getMessage());
        }
    }

    /**
     * Constrói saída alternativa quando toString() falha ou retorna null
     */

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

        int parentRef = classEntry.getObjectReference(); // ADICIONE ESTA LINHA AQUI
        
     // --- BUSCA MEMBROS (MAIS RESILIENTE) ---
        List<ExportEntry> constants = new ArrayList<>();
        List<ExportEntry> enums = new ArrayList<>();
        List<ExportEntry> structs = new ArrayList<>();
        List<ExportEntry> properties = new ArrayList<>();
        List<ExportEntry> functions = new ArrayList<>();

        // 1. Primeiro, pegamos todos os filhos do jeito que funciona (pelo parentRef)
        List<ExportEntry> allChildren = up.getExportTable().stream()
                .filter(e -> {
                    UnrealPackage.Entry<?> pkg = e.getObjectPackage();
                    return pkg != null && pkg.getObjectReference() == parentRef;
                })
                .collect(Collectors.toList());

        // 2. Tentativa de ler a Chain para ordenar (A "Caixa de Pandora")
        boolean chainLoaded = false;
        try {
            byte[] classData = classEntry.getObjectRawData();
            if (classData != null && classData.length > 0) {
                UnrealSerializerFactory sf = new UnrealSerializerFactory(new SimpleEnv(up));
                UnrealRuntimeContext runtimeCtx = new UnrealRuntimeContext(classEntry, sf);
                L2DataInput di = L2DataInput.dataInput(new ByteArrayInputStream(classData), up.getFile().getCharset());
                org.l2explorer.io.ObjectInput<UnrealRuntimeContext> input = new org.l2explorer.io.UnrealObjectInput<>(di, sf, runtimeCtx);

                org.l2explorer.unreal.core.Class classObj = (org.l2explorer.unreal.core.Class) sf.forClass(org.l2explorer.unreal.core.Class.class).instantiate(input);
                sf.forClass(org.l2explorer.unreal.core.Class.class).readObject(classObj, input);

                for (Field f : classObj) {
                    ExportEntry child = f.getEntry();
                    distributeChild(child, constants, enums, structs, properties, functions);
                }
                chainLoaded = true;
            }
        } catch (Exception e) {

        }

        if (!chainLoaded || (constants.isEmpty() && properties.isEmpty() && functions.isEmpty())) {
            constants.clear(); properties.clear(); functions.clear(); // Limpa lixo parcial
            for (ExportEntry child : allChildren) {
                distributeChild(child, constants, enums, structs, properties, functions);
            }
        }

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
        
        if (!enums.isEmpty()) {
            sb.append("//==============================================================================\n");
            sb.append("// Enums\n");
            sb.append("//==============================================================================\n");
            for (ExportEntry e : enums) {
                sb.append(decompileEnum(e)).append("\n");
            }
        }
        
        if (!structs.isEmpty()) {
            sb.append("//==============================================================================\n");
            sb.append("// Structs\n");
            sb.append("//==============================================================================\n");
            	for (ExportEntry s : structs) {
                    sb.append(decompileStruct(s)).append("\n");
            	}
            }

        if (!properties.isEmpty()) {
            sb.append("//==============================================================================\n");
            sb.append("// Properties\n");
            sb.append("//==============================================================================\n");
            for (ExportEntry p : properties) {
                sb.append(getFullPropertyLine(p)).append("\n"); // O método novo faz tudo
            }
            
            sb.append("\n");
        }

        if (!functions.isEmpty()) {
            sb.append("//==============================================================================\n");
            sb.append("// Functions\n");
            sb.append("//==============================================================================\n\n");
            for (ExportEntry func : functions) {
                sb.append("function ").append(func.getObjectName().getName()).append("()\n");
                sb.append("{\n");
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

        if (properties.isEmpty()) {
            System.out.println("ALERTA: Nenhuma propriedade encontrada para " + classEntry.getObjectName().getName());
        }

        try {
            String defaults = decompileDefaultProperties(classEntry, properties);
            if (defaults != null && !defaults.isEmpty()) {
                sb.append(defaults).append("\n");
            }
        } catch (Exception e) {
            sb.append("\n// Error in defaultproperties: ").append(e.getMessage()).append("\n");
        }

        return sb.toString();
    }
    
    private void distributeChild(ExportEntry child, List<ExportEntry> constants, List<ExportEntry> enums, 
            List<ExportEntry> structs, List<ExportEntry> properties, List<ExportEntry> functions) {
			String className = child.getFullClassName();
			if (className.contains("Const")) constants.add(child);
			else if (className.contains("Enum")) enums.add(child);
			else if (className.contains("Property")) properties.add(child);
			else if (className.contains("Struct")) structs.add(child);
			else if (className.equals("Core.Function")) functions.add(child);
			}
    
    public String decompileEnum(ExportEntry enumEntry) {
        StringBuilder sb = new StringBuilder();
        String enumName = enumEntry.getObjectName().getName();
        sb.append("enum ").append(enumName).append("\n{\n");

        try {
            byte[] data = enumEntry.getObjectRawData();
            if (data != null && data.length > 0) {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                    // 1. Pula Super e Next (Cabeçalho de Field)
                    StreamsHelper.readCompactInt(bais); 
                    StreamsHelper.readCompactInt(bais);

                    int count = StreamsHelper.readCompactInt(bais);
                    
                    List<String> items = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        if (bais.available() <= 0) break;
                        
                        int nameIndex = StreamsHelper.readCompactInt(bais);
                        
                        if (nameIndex > 0 && nameIndex < enumEntry.getUnrealPackage().getNameTable().size()) {
                            String itemName = enumEntry.getUnrealPackage().getNameTable().get(nameIndex).getName();
                            
                            if (!itemName.equals(enumName) && !itemName.endsWith("_MAX") && !isL2Token(itemName)) {
                                items.add(itemName);
                            }
                        }
                    }

                    for (int i = 0; i < items.size(); i++) {
                        String name = items.get(i);
                        sb.append("    ").append(name);
                        if (i < items.size() - 1) sb.append(",");
                        int padding = 32 - name.length();
                        for (int p = 0; p < (padding > 0 ? padding : 1); p++) sb.append(" ");
                        sb.append("// ").append(i).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            sb.append("    // Error: ").append(e.getMessage()).append("\n");
        }

        sb.append("};");
        return sb.toString();
    }

    private boolean isL2Token(String name) {
        return name.equalsIgnoreCase("None") || name.contains("Int") || name.contains("Float");
    }

    /**
     * Verifica se o nome é um token de sistema da Unreal Engine (Bytecode/Metadata).
     */
    
    public String decompileStruct(ExportEntry structEntry) {
        StringBuilder sb = new StringBuilder();
        String structName = structEntry.getObjectName().getName();
        int structRef = structEntry.getObjectReference();

        sb.append("struct ").append(structName).append("\n{\n");

        List<ExportEntry> members = structEntry.getUnrealPackage().getExportTable().stream()
                .filter(e -> e.getObjectPackage() != null && e.getObjectPackage().getObjectReference() == structRef)
                .filter(e -> e.getFullClassName().contains("Property")) // Só queremos as variáveis
                .collect(Collectors.toList());

        if (members.isEmpty()) {
            sb.append("    // Native or Empty Struct\n");
        } else {
            for (ExportEntry member : members) {
                sb.append("    ").append(getFullPropertyLine(member)).append("\n");
            }
        }

        sb.append("};");
        return sb.toString();
    }
   
    private String getConstantValue(ExportEntry c) throws IOException {
        try {
            byte[] data = c.getObjectRawData();
            if (data == null || data.length == 0) return "0";

            // Se o dado for muito pequeno, geralmente é o valor puro em CompactInt
            if (data.length <= 4) {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                    L2DataInput di = L2DataInput.dataInput(bais, c.getUnrealPackage().getFile().getCharset());
                    return String.valueOf(di.readCompactInt());
                }
            }

            try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                L2DataInput di = L2DataInput.dataInput(bais, c.getUnrealPackage().getFile().getCharset());
                
                di.readCompactInt(); 

                while (bais.available() > 1) { // Precisa de pelo menos 2 bytes (Tag + Info)
                    bais.mark(16);
                    int potentialNameIdx = di.readCompactInt();
                    
                    if (potentialNameIdx > 0 && potentialNameIdx < c.getUnrealPackage().getNameTable().size()) {
                        String name = c.getUnrealPackage().nameReference(potentialNameIdx);
                        
                        if ("Value".equalsIgnoreCase(name)) {
                            int info = di.readUnsignedByte();
                            int type = info & 0x0F;
                            
                            // Se achou a tag, o próximo dado é o ouro
                            return switch (type) {
                                case 1 -> String.valueOf(di.readUnsignedByte()); // Byte (o 148/wX!)
                                case 2 -> String.valueOf(di.readInt());          // Integer
                                case 4 -> String.valueOf(di.readFloat());        // Float
                                case 12 -> di.readUTF();                         // String
                                default -> String.valueOf(di.readCompactInt());  // Fallback Compact
                            };
                        }
                    }
                    bais.reset();
                    di.readByte(); // Desliza um byte e tenta alinhar de novo
                }
            }
        } catch (Exception e) {

        }
        return scanBruto(c);
    }

    /**
     * Fallback para quando o alinhamento de tags falha.
     * Ignora o nome da classe, o nome do objeto e caracteres de controle (Lixo B, J, w).
     * @throws IOException 
     */
    private String scanBruto(ExportEntry c) throws IOException {
        byte[] data = c.getObjectRawData();
        if (data == null) return "0";
        
        String objName = c.getObjectName().getName();
        String raw = new String(data, java.nio.charset.StandardCharsets.ISO_8859_1);
        
        Matcher m = Pattern.compile("[a-zA-Z0-9_.-]{1,}").matcher(raw);
        List<String> candidates = new ArrayList<>();
        
        while (m.find()) {
            String match = m.group();
            if (match.equalsIgnoreCase("Const") || match.equalsIgnoreCase("Core") || 
                match.equalsIgnoreCase(objName) || match.equalsIgnoreCase("Value") ||
                (match.length() == 1 && !Character.isDigit(match.charAt(0)))) {
                continue;
            }
            candidates.add(match);
        }
        
        if (!candidates.isEmpty()) {
            return candidates.get(candidates.size() - 1);
        }
        
        return "0";
    }
    
    public String decompileDefaultProperties(ExportEntry classEntry, List<ExportEntry> properties) throws IOException {
        byte[] data = classEntry.getObjectRawData();
        if (data == null || data.length < 32) return "";

        StringBuilder sb = new StringBuilder();
        UnrealPackage up = classEntry.getUnrealPackage();

        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
        	L2DataInput di = L2DataInput.dataInput(bais, up.getFile().getCharset());

            int startOffset = -1;
            for (int i = 0; i < data.length - 8; i++) {
                bais.reset();
                bais.skip(i);
                try {
                    int nameIdx = di.readCompactInt();
                    if (nameIdx > 0 && nameIdx < up.getNameTable().size()) {
                        String name = up.getNameTable().get(nameIdx).getName();
                        
                        if (name.equalsIgnoreCase("m_WindowName")) {
                            startOffset = i;
                            int infoByte = bais.read();
                            if ((infoByte & 0x0F) == 11 || (infoByte & 0x0F) == 6) { // String ou Name
                                break; 
                            }
                        }
                    }
                } catch (Exception e) {}
            }

            if (startOffset == -1) {
                for (int i = data.length - 10; i > 0; i--) {
                    bais.reset();
                    bais.skip(i);
                    try {
                        int nameIdx = di.readCompactInt();
                        if (up.getNameTable().get(nameIdx).getName().equalsIgnoreCase("Me")) {
                            startOffset = i;
                            break;
                        }
                    } catch (Exception e) {}
                }
            }

            if (startOffset == -1) return "";

            bais.reset();
            bais.skip(startOffset);
            sb.append("\ndefaultproperties\n{\n");

            // 2. LOOP DE LEITURA (Protocolo 542 / UE2.5)
            while (bais.available() > 0) {
                int nameIdx = di.readCompactInt();
                if (nameIdx <= 0 || nameIdx >= up.getNameTable().size()) break;

                String propName = up.getNameTable().get(nameIdx).getName();
                if (propName.equalsIgnoreCase("None")) break;

                int info = di.readUnsignedByte();
                int type = info & 0x0F;
                int sizePack = (info & 0x70) >> 4;
                boolean boolOrArray = (info & 0x80) != 0;

                if (type == 3) { // Bool
                    sb.append("    ").append(propName).append("=").append(boolOrArray ? "true" : "false").append("\n");
                    continue;
                }

                if (type == 10) { di.readCompactInt(); } // Struct Name

                int size = switch (sizePack) {
                    case 0 -> 1; case 1 -> 2; case 2 -> 4; case 3 -> 12;
                    case 4 -> 16; case 5 -> di.readUnsignedByte();
                    case 6 -> di.readUnsignedShort(); case 7 -> di.readInt();
                    default -> 0;
                };

                if (boolOrArray) { di.readCompactInt(); } // ArrayIndex

                String value = "";
                try {
                    switch (type) {
                        case 1 -> value = String.valueOf(di.readUnsignedByte()); // Byte
                        case 2 -> value = String.valueOf(IOUtil.readInt(bais)); // Int
                        case 3 -> value = (boolOrArray) ? "true" : "false"; // Bool
                        case 4 -> value = String.valueOf(IOUtil.readFloat(bais)); // Float
                        case 5, 7 -> { // ObjectProperty
                            int ref = di.readCompactInt();
                            var obj = up.objectReference(ref);
                            value = (obj != null) ? obj.getObjectName().getName() : "none";
                        }
                        case 6, 8 -> value = "'" + up.getNameTable().get(di.readCompactInt()).getName() + "'"; // Name
                        case 11 -> value = "\"" + StreamsHelper.readString(bais) + "\""; // ANSI String
                        case 13 -> { // Unicode String 
                            value = "\"" + StreamsHelper.readString(bais) + "\"";
                        }
                        case 10 -> { // StructProperty
                            di.readCompactInt(); // Struct Name
                            bais.skip(size);
                            value = "/* struct */";
                        }
                        default -> {
                            bais.skip(Math.min(size, (int)bais.available()));
                            value = "/* type " + type + " size " + size + " */";
                        }
                    }
                } catch (Exception e) {
                    value = "/* parse error */";
                }

                sb.append("    ").append(propName).append("=").append(value).append("\n");
            }
            sb.append("}\n");

        } catch (Exception e) {
            return "// Error: " + e.getMessage();
        }
        return sb.toString();
    }
    
    public String getFullPropertyLine(ExportEntry p) {
        String name = p.getObjectName().getName();
        String className = p.getFullClassName();
        String typeName = className.contains("Bool") ? "bool" : "object"; // Fallback seguro

        try {
            byte[] data = p.getObjectRawData();
            if (data != null && data.length > 0) {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                    L2DataInput di = L2DataInput.dataInput(bais, p.getUnrealPackage().getFile().getCharset());
                    
                    // 1. Pula Field Header
                    di.readCompactInt(); // Super
                    di.readCompactInt(); // Next

                    // 2. Metadados Básicos
                    int dimension = di.readUnsignedShort();
                    int flags = di.readInt();
                    
                    // 3. Category
                    di.readCompactInt();

                    // 4. Pula Replication se houver
                    if ((flags & 0x00000020) != 0) di.readUnsignedShort();

                    // 5. O SEGREDO DO ESSENCE:
                    // Se o typeName ainda é object e temos bytes, vamos caçar o typeRef
                    while (bais.available() > 0) {
                        int ref = di.readCompactInt();
                        
                        // Se achamos um índice que aponta para algo válido na NameTable/ExportTable
                        if (ref != 0) {
                            var entry = p.getUnrealPackage().objectReference(ref);
                            if (entry != null) {
                                String refName = entry.getObjectName().getName();
                                
                                // Se o nome da referência for uma classe de Property, 
                                // é o Inner de um Array. Se não, é o tipo do Objeto/Struct/Enum.
                                if (refName.endsWith("Property")) {
                                    typeName = "array<" + refName.replace("Property", "").toLowerCase() + ">";
                                } else {
                                    typeName = refName;
                                }
                                break; // Achamos o tipo, sai do loop!
                            }
                        }
                    }
                    
                    // Limpeza final para tipos primitivos
                    if (className.contains("IntProperty")) typeName = "int";
                    else if (className.contains("FloatProperty")) typeName = "float";
                    else if (className.contains("StrProperty")) typeName = "string";

                    String dimStr = (dimension > 1 && dimension < 500) ? "[" + dimension + "]" : "";
                    return "var " + typeName + " " + name + dimStr + ";";
                }
            }
        } catch (Exception e) {
            // Fallback
        }
        return "var " + typeName + " " + name + ";";
    }
    
    public String decompileConst(ExportEntry entry) {
        try {
            byte[] data = entry.getObjectRawData();
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                L2DataInput di = L2DataInput.dataInput(bais, entry.getUnrealPackage().getFile().getCharset());
                
                /* 1. Header Field (Super + Next) */
                di.readCompactInt();
                di.readCompactInt();

                /* 2. O valor da constante é uma Unreal String */
                String value = StreamsHelper.readString(bais);
                
                return "const " + entry.getObjectName().getName() + " = " + value + ";";
            }
        } catch (Exception e) {
            return "// Error decompiling Const: " + e.getMessage();
        }
    }

 // Teste rápido para o TextBuffer
    public String decompileTextBuffer(ExportEntry entry) throws IOException {
        byte[] data = entry.getObjectRawData();
        // Tenta ler a string de qualquer lugar onde ela comece com um caractere legível
        for (int i = 0; i < Math.min(data.length, 16); i++) {
            if (data[i] >= 32 && data[i] <= 126) { // Achou um caractere ANSI
                 return new String(data, i, data.length - i, StandardCharsets.UTF_8).trim();
            }
        }
        return "// No readable text";
    }
    
    @SuppressWarnings({ "unused", "unchecked" })
	private <T extends Property> void readPropertyData(
            UnrealSerializerFactory sf, Class<?> targetClass, Property propObj, 
            ObjectInput<UnrealRuntimeContext> input) throws IOException {
        
        Serializer<T, UnrealRuntimeContext> serializer = 
            (Serializer<T, UnrealRuntimeContext>) sf.forClass(targetClass);
        serializer.readObject((T) propObj, input);
    }

    @SuppressWarnings("unused")
	private String bytesToHex(byte[] bytes, int limit) {
        if (bytes == null) return "null";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(bytes.length, limit); i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        return sb.toString();
    }
}