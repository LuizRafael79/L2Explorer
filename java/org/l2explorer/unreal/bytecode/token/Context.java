package org.l2explorer.unreal.bytecode.token;

import java.util.Objects;
import org.l2explorer.io.annotation.UByte;
import org.l2explorer.io.annotation.UShort;
import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Token de Contexto de Objeto (Opcode 0x19).
 * Representa o acesso a um membro de uma instância (ex: Object.Member).
 */
public class Context extends Token {
    public static final int OPCODE = 0x19;

    private Token object;
    
    @UShort
    private int wSkip;   // Tamanho para pular em caso de erro/vazio
    
    @UByte
    private int bSize;   // Tamanho do resultado do membro
    
    private Token member;

    public Context() {
    }

    public Context(Token object, int wSkip, int bSize, Token member) {
        this.object = object;
        this.wSkip = wSkip;
        this.bSize = bSize;
        this.member = member;
    }

    // Getters e Setters
    public Token getObject() { return object; }
    public void setObject(Token object) { this.object = object; }

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
        if (!(o instanceof Context)) return false;
        Context context = (Context) o;
        return wSkip == context.wSkip && 
               bSize == context.bSize && 
               Objects.equals(object, context.object) && 
               Objects.equals(member, context.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, wSkip, bSize, member);
    }

    @Override
    public String toString() {
        return "Context(" + object + ", " + wSkip + ", " + bSize + ", " + member + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        // Se um dos lados falhar, evitamos o NullPointerException
        String objStr = (object == null) ? "null" : object.toString(context);
        String memStr = (member == null) ? "null" : member.toString(context);
        
        return objStr + "." + memStr;
    }
}