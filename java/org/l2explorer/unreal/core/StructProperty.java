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

import org.l2explorer.unreal.annotation.ObjectRef;

/**
 * Represents a Struct Property in the Unreal Engine.
 * <p>A StructProperty points to a Struct definition, which describes 
 * the memory layout and fields of the structured data being stored.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class StructProperty extends Property {
    /**
     * Reference to the Struct definition that defines this property's layout.
     */
    @ObjectRef
    private Struct struct;

    public StructProperty() {
    }

    /**
     * Gets the struct definition for this property.
     *
     * @return The Struct object reference.
     */
    public Struct getStruct() {
        return struct;
    }

    /**
     * Sets the struct definition for this property.
     *
     * @param struct The Struct object reference to set.
     */
    public void setStruct(Struct struct) {
        this.struct = struct;
    }
}