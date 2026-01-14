/*
 * Copyright (c) 2026 Galagard/L2Explorer
 */
package org.l2explorer.utils.bytecode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import org.l2explorer.app.ExplorerPanel;
import org.l2explorer.app.ExplorerPanel.DebugConsole;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.utils.enums.UnrealOpcode;

/**
 * Decompiler Avançado com Pilha de Expressões e Fluxo de Controle.
 */
public class BytecodeDecompiler {
    private final byte[] script;
    private final UnrealPackage up;
    private final ExplorerPanel.DebugConsole console; // Adicione este campo
    private int pc = 0;
    private int depth = 1;

    /**
     * Define a posição atual do Program Counter (PC).
     * @param pc O offset inicial para começar a leitura do bytecode.
     */
    public void setPC(int pc) {
        this.pc = pc;
    }
    
    public BytecodeDecompiler(byte[] script, UnrealPackage up, DebugConsole console) {
        this.script = script;
        this.up = up;
        this.console = console;
        }

    public String decompile() {
        StringBuilder sb = new StringBuilder();
        try {
            while (pc < script.length) {
                String line = processToken();
                if (!line.isEmpty()) {
                    sb.append(getIndent()).append(line).append(";\n");
                }
            }
        } catch (Exception e) {
            sb.append("\n/* DECOMPILATION ERROR at PC 0x")
              .append(Integer.toHexString(pc).toUpperCase())
              .append(": ").append(e.getMessage()).append(" */");
        }
        return sb.toString();
    }

    private String getIndent() {
        return "\t".repeat(Math.max(0, depth));
    }

    /**
     * Resolve o nome de um índice na NameTable do pacote.
     */
    private String resolve(int index) {
        // AGORA SIM: Usamos o roteador que conhece Imports e Exports
        return up.getFriendlyName(index);
    }
    
	private String getHexDump(int centerPc, int radius) {
        StringBuilder sb = new StringBuilder();
        int start = Math.max(0, centerPc - radius);
        int end = Math.min(script.length, centerPc + radius);
        
        for (int i = start; i < end; i++) {
            if (i == centerPc) sb.append("[").append(String.format("%02X", script[i] & 0xFF)).append("] ");
            else sb.append(String.format("%02X ", script[i] & 0xFF));
        }
        return sb.toString().trim();
    }
    
	public String processToken() {
	    int rawToken = script[pc++] & 0xFF;
	    UnrealOpcode op = UnrealOpcode.fromInt(rawToken);

	    if (op == null) {
	        String dump = getHexDump(pc - 1, 5);
	        console.log("Unknown Token 0x" + Integer.toHexString(rawToken) + " Dump: " + dump);
	        return "/* Unknown 0x" + Integer.toHexString(rawToken) + " */";
	    }

	    switch (op) {
	        case NOTHING: // 0x0B
	            pc = script.length; // Para a função aqui (evita ler o lixo 00 00 00)
	            return "";

	        case INT_ZERO: return "0"; // 0x25
	        case INT_ONE:  return "1"; // 0x26
	        case TRUE:     return "true"; // 0x27
	        case FALSE:    return "false"; // 0x28

	        case BYTE_CONST: // 0x24
	        case INT_CONST_BYTE: // 0x2C
	            return String.valueOf(script[pc++] & 0xFF);

	        case INT_CONST: // 0x1D
	            return String.valueOf(readInt32());

	        case FLOAT_CONST: // 0x1E
	            return String.valueOf(Float.intBitsToFloat(readInt32())) + "f";

	        case STRING_CONST: // 0x1F
	            return "\"" + readString() + "\"";

	        case OBJECT_CONST: // 0x20
	            return resolve(readCompactIndex());

	        case NAME_CONST: // 0x21
	            return "'" + resolve(readCompactIndex()) + "'";

	        case SELF: // 0x17
	            return "Self";

	        case INSTANCE_VARIABLE: // 0x01 (No seu enum é 0x01)
	            return resolve(readCompactIndex());

	        case LOCAL_VARIABLE: // 0x00
	            return resolve(readCompactIndex());

	        case CONTEXT: // 0x19 (O ponto '.')
	            String ctx = processToken();
	            pc += 3; // Pula o skip size do motor Samurai
	            return ctx + "." + processToken();

	        case FINAL_FUNCTION: // 0x1C
	        case VIRTUAL_FUNCTION: // 0x1B
	            return resolve(readCompactIndex()) + "(";

	        case END_FUNCTION_PARAMS: // 0x16
	            return ")";

	        case LET: // 0x0F
	            String var = processToken();
	            String value = processToken();
	            return var + " = " + value;

	         // Adicione suporte para nativos comuns que apareceram no seu dump
	        case CONCATEN: // Nativo de concatenação (muito comum em caminhos de janela)
	            return processToken() + " $ " + processToken();

	        case EQUAL: // Comparação de igualdade
	            return processToken() + " == " + processToken();

	        case DIFFERENCE: // Comparação de diferença
	            return processToken() + " != " + processToken();
	            
	        case JUMP: // 0x06
	            pc += 2; // Consome o offset do pulo
	            return "";
	         // No seu switch:
	        case BOOL_TO_STRING:
	        case FLOAT_TO_STRING:
	        case OBJECT_TO_STRING:
	        case NAME_TO_STRING:
	            return op.getName().split("To")[0].toLowerCase() + "(" + processToken() + ")";

	        default:
	            return "/* " + op.getName() + " */";
	    }
	}

    private String processParams() {
        StringBuilder params = new StringBuilder();
        while (pc < script.length) {
            int nextToken = script[pc] & 0xFF;
            if (nextToken == 0x16) {
                pc++; 
                break;
            }
            if (params.length() > 0) params.append(", ");
            params.append(processToken());
        }
        return params.toString();
    }

    private String processNativeCall(int b) {
        int nativeIndex = ((b - 0x60) << 8) + (script[pc++] & 0xFF);
        return "Native_" + nativeIndex + "(" + processParams() + ")";
    }

    // --- Helpers de Leitura ---
    private int readInt16() {
        int val = (script[pc] & 0xFF) | ((script[pc + 1] & 0xFF) << 8);
        pc += 2;
        return val;
    }
    private int readInt32() {
        int val = ByteBuffer.wrap(script, pc, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        pc += 4;
        return val;
    }
    @SuppressWarnings("unused")
	private long readInt64() {
        long val = ByteBuffer.wrap(script, pc, 8).order(ByteOrder.LITTLE_ENDIAN).getLong();
        pc += 8;
        return val;
    }
    @SuppressWarnings("unused")
	private float readFloat() {
        float val = ByteBuffer.wrap(script, pc, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        pc += 4;
        return val;
    }
 // Adicione este método auxiliar para ler strings nulas (C-Style)
    private String readString() {
        StringBuilder sb = new StringBuilder();
        byte b;
        while ((b = script[pc++]) != 0x00) {
            sb.append((char) b);
        }
        return sb.toString();
    }
    
    private int readCompactIndex() {
        int output = 0;
        boolean signed = false;
        for (int i = 0; i < 5; i++) {
            int x = script[pc++] & 0xFF;
            if (i == 0) {
                if ((x & 0x80) != 0) signed = true;
                output |= (x & 0x3F);
                if ((x & 0x40) == 0) break;
            } else if (i == 4) {
                output |= (x & 0x1F) << (6 + (3 * 7));
            } else {
                output |= (x & 0x7F) << (6 + ((i - 1) * 7));
                if ((x & 0x80) == 0) break;
            }
        }
        return signed ? -output : output;
    }

	/**
	 * @return the console
	 */
	public ExplorerPanel.DebugConsole getConsole() {
		return console;
	}
}