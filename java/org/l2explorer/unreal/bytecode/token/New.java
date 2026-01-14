package org.l2explorer.unreal.bytecode.token;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.l2explorer.unreal.UnrealRuntimeContext;

/**
 * Represents the 'new' operator used for object instantiation (Opcode 0x11).
 * <p>In UnrealScript, the 'new' operator can take optional parameters for 
 * the Outer object, the name of the new instance, and object flags.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class New extends Token {
    /**
     * The bytecode operation code for New.
     */
    public static final int OPCODE = 0x11;

    private Token outer;
    private Token name;
    private Token flags;
    private Token clazz;

    /**
     * Default constructor for New.
     */
    public New() {
    }

    /**
     * Constructs a New token with all possible instantiation parameters.
     *
     * @param outer The outer object (owner).
     * @param name  The name for the new object instance.
     * @param flags The object flags.
     * @param clazz The class to instantiate.
     */
    public New(Token outer, Token name, Token flags, Token clazz) {
        this.outer = outer;
        this.name = name;
        this.flags = flags;
        this.clazz = clazz;
    }

    public Token getOuter() {
        return outer;
    }

    public void setOuter(Token outer) {
        this.outer = outer;
    }

    public Token getName() {
        return name;
    }

    public void setName(Token name) {
        this.name = name;
    }

    public Token getFlags() {
        return flags;
    }

    public void setFlags(Token flags) {
        this.flags = flags;
    }

    public Token getClazz() {
        return clazz;
    }

    public void setClazz(Token clazz) {
        this.clazz = clazz;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof New)) return false;
        New aNew = (New) o;
        return Objects.equals(outer, aNew.outer) && 
               Objects.equals(name, aNew.name) && 
               Objects.equals(flags, aNew.flags) && 
               Objects.equals(clazz, aNew.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outer, name, flags, clazz);
    }

    @Override
    public String toString() {
        return "New(" + outer + ", " + name + ", " + flags + ", " + clazz + ")";
    }

    /**
     * Returns the decompiler representation of the 'new' statement.
     * <p>Optional parameters (outer, name, flags) are included only if they are 
     * not of type 'Nothing'.</p>
     *
     * @param context The runtime context for the Unreal engine.
     * @return The formatted string "new [(outer, name, flags)] Class".
     */
    @Override
    public String toString(UnrealRuntimeContext context) {
        StringBuilder sb = new StringBuilder("new ");
        
        List<Token> params = Stream.of(outer, name, flags)
                .filter(it -> it != null && !(it instanceof Nothing))
                .collect(Collectors.toList());
        
        if (!params.isEmpty()) {
            sb.append(params.stream()
                    .map(it -> it.toString(context))
                    .collect(Collectors.joining(", ", "(", ") ")));
        }
        
        sb.append(clazz == null ? "null" : clazz.toString(context));
        return sb.toString();
    }
}