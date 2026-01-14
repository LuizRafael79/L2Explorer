package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.UByte;
import org.l2explorer.io.annotation.UShort;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Token de Contexto de Classe (Opcode 0x12).
 * Usado para acessar membros de uma classe específica (ex: Class.Member).
 */
public class ClassContext extends Token {
    public static final int OPCODE = 0x12;

    private Token clazz;
    
    @UShort
    private int wSkip;   // tamanho do membro
    
    @UByte
    private int bSize;   // tamanho do resultado da chamada
    
    private Token member;

    public ClassContext() {
    }

    public ClassContext(Token clazz, int wSkip, int bSize, Token member) {
        this.clazz = clazz;
        this.wSkip = wSkip;
        this.bSize = bSize;
        this.member = member;
    }

    // Getters e Setters
    public Token getClazz() { return clazz; }
    public void setClazz(Token clazz) { this.clazz = clazz; }

    public int getWSkip() { return wSkip; }
    public void setWSkip(int wSkip) { this.wSkip = wSkip; }

    public int getBSize() { return bSize; }
    public void setBSize(int bSize) { this.bSize = bSize; }

    public Token getMember() { return member; }
    public void setMember(Token member) { this.member = member; }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassContext)) return false;
        ClassContext that = (ClassContext) o;
        return wSkip == that.wSkip && 
               bSize == that.bSize && 
               Objects.equals(clazz, that.clazz) && 
               Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, wSkip, bSize, member);
    }

    @Override
    public String toString() {
        return "ClassContext(" + clazz + ", " + wSkip + ", " + bSize + ", " + member + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        if (clazz == null || member == null) {
            return "null.null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(clazz.toString(context)).append(".");

        boolean isStatic = true;
        if (member instanceof DefaultVariable) {
            isStatic = false;
        } else if (member instanceof BoolVariable) {
            if (((BoolVariable) member).getValue() instanceof DefaultVariable) {
                isStatic = false;
            }
        }

        if (isStatic) {
            sb.append("static.");
        }

        sb.append(member.toString(context));
        return sb.toString();
    }
}