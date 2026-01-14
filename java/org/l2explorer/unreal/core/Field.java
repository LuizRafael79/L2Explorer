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
 * Represents a Field in the Unreal Engine.
 * <p>A Field is a base class for engine structures that can be linked 
 * together or inherit from a super-field, such as properties, enums, 
 * and structs.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class Field extends Object {
    /**
     * Reference to the parent field from which this field inherits.
     */
    @ObjectRef
    private Object superField;

    /**
     * Reference to the next field in the linked list of fields.
     */
    @ObjectRef
    private Field next;

    public Field() {
    }

    /**
     * Gets the super field of this field.
     *
     * @return The super field object reference.
     */
    public Object getSuperField() {
        return superField;
    }

    /**
     * Sets the super field of this field.
     *
     * @param superField The super field object reference to set.
     */
    public void setSuperField(Object superField) {
        this.superField = superField;
    }

    /**
     * Gets the next field in the chain.
     *
     * @return The next field object reference.
     */
    public Field getNext() {
        return next;
    }

    /**
     * Sets the next field in the chain.
     *
     * @param next The next field object reference to set.
     */
    public void setNext(Field next) {
        this.next = next;
    }
}