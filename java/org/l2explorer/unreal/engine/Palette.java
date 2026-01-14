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

import org.l2explorer.unreal.core.Object;

/**
 * Represents a color Palette in the Unreal Engine.
 * <p>Used primarily by indexed textures (8-bit) to store an array of colors 
 * in ARGB format.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class Palette extends Object {
    /**
     * Array of colors in ARGB (Alpha, Red, Green, Blue) format.
     */
    private int[] colors;

    public Palette() {
    }

    /**
     * Gets the array of colors stored in this palette.
     *
     * @return An array of integers representing ARGB colors.
     */
    public int[] getColors() {
        return colors;
    }

    /**
     * Sets the array of colors for this palette.
     *
     * @param colors The array of ARGB colors to set.
     */
    public void setColors(int[] colors) {
        this.colors = colors;
    }
}