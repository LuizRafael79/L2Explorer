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
package org.l2explorer.unreal.engine;

import org.l2explorer.io.annotation.Compact;
import org.l2explorer.unreal.annotation.ObjectRef;
import org.l2explorer.unreal.core.Object;

/**
 * Represents a Font resource in the Unreal Engine.
 * <p>Fonts consist of a character mapping table and a list of textures 
 * where the glyphs are stored.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class Font extends Object {
    /**
     * Array of character definitions (mapping and UV coordinates).
     */
    private Character[] characters;

    /**
     * List of texture objects containing the font glyphs.
     */
    @ObjectRef
    private Object[] textureList;

    private int unk1;
    private int unk2;

    public Font() {
    }

    // --- Getters and Setters ---

    public Character[] getCharacters() {
        return characters;
    }

    public void setCharacters(Character[] characters) {
        this.characters = characters;
    }

    public Object[] getTextureList() {
        return textureList;
    }

    public void setTextureList(Object[] textureList) {
        this.textureList = textureList;
    }

    public int getUnk1() {
        return unk1;
    }

    public void setUnk1(int unk1) {
        this.unk1 = unk1;
    }

    public int getUnk2() {
        return unk2;
    }

    public void setUnk2(int unk2) {
        this.unk2 = unk2;
    }

    /**
     * Inner class representing a single font character mapping.
     */
    public static class Character {
        public int x;
        public int y;
        public int width;
        public int height;

        @Compact
        public int textureIndex;

        // Note: For simple inner data classes like this, 
        // public fields are often acceptable, but you can encapsulate 
        // them if you prefer total consistency.
    }
}