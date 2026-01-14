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
 * Represents a Dynamic Array Property in the Unreal Engine.
 * <p>In UnrealScript, dynamic arrays are defined by this property, 
 * which points to an inner Property representing the type of the 
 * array's elements.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class ArrayProperty extends Property {
    /**
     * The inner property that defines the type of the array elements.
     */
    @ObjectRef
    private Property inner;

    public ArrayProperty() {
    }

    /**
     * Gets the inner property (element type) of the array.
     *
     * @return The inner Property object reference.
     */
    public Property getInner() {
        return inner;
    }

    /**
     * Sets the inner property (element type) of the array.
     *
     * @param inner The inner Property object reference to set.
     */
    public void setInner(Property inner) {
        this.inner = inner;
    }
}