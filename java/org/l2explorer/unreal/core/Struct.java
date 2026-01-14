/*
 * Copyright (c) 2021 acmi
 * Copyright (c) 2026 Galagard/L2Explorer
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
package org.l2explorer.unreal.core;

import org.l2explorer.unreal.annotation.Bytecode;
import org.l2explorer.unreal.annotation.NameRef;
import org.l2explorer.unreal.annotation.ObjectRef;
import org.l2explorer.unreal.bytecode.token.Token;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;

/**
 * Represents a Struct in the Unreal Engine.
 * <p>A Struct is a field that can contain other fields (children) and 
 * executable bytecode. It serves as the base for Functions, States, and Classes.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class Struct extends Field implements Iterable<Field> {
    @ObjectRef
    private TextBuffer scriptText;
    
    @ObjectRef
    private Field child;
    
    @NameRef
    private String friendlyName;
    
    @ObjectRef
    private TextBuffer cppText;
    
    private int line;
    
    private int textPos;
    
    @Bytecode
    private Token[] bytecode;

    public Struct() {
    }

    @Override
    public Iterator<Field> iterator() {
        return new Iterator<Field>() {
            Field field = child;

            @Override
            public boolean hasNext() {
                return Objects.nonNull(field);
            }

            @Override
            public Field next() {
                try {
                    return field;
                } finally {
                    field = field.getNext();
                }
            }
        };
    }

    @Override
    public Spliterator<Field> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), 
                Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.IMMUTABLE);
    }

    // --- Getters and Setters ---

    public TextBuffer getScriptText() {
        return scriptText;
    }

    public void setScriptText(TextBuffer scriptText) {
        this.scriptText = scriptText;
    }

    public Field getChild() {
        return child;
    }

    public void setChild(Field child) {
        this.child = child;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public TextBuffer getCppText() {
        return cppText;
    }

    public void setCppText(TextBuffer cppText) {
        this.cppText = cppText;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getTextPos() {
        return textPos;
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
    }

    public Token[] getBytecode() {
        return bytecode;
    }

    public void setBytecode(Token[] bytecode) {
        this.bytecode = bytecode;
    }
}