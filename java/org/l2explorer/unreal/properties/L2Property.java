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
package org.l2explorer.unreal.properties;

import org.l2explorer.unreal.core.Property;
import org.l2explorer.unreal.core.StructProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a runtime instance of an Unreal Property in Lineage II.
 * <p>Uses java.lang.Object for the value array to avoid conflicts with 
 * the Unreal Object class.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public final class L2Property {
    private final Property template;
    
    /** * Explicit use of java.lang.Object to store primitive wrappers, 
     * Strings, and Lists without type conflicts.
     */
    private final java.lang.Object[] value;

    public L2Property(Property template) {
        if (template == null) {
            throw new NullPointerException("Property template cannot be null");
        }
        this.template = template;
        this.value = new java.lang.Object[template.getArrayDimension()];
    }

    public String getName() {
        return template.getEntry().getObjectName().getName();
    }

    public int getSize() {
        return value.length;
    }

    /**
     * @return The value as a java.lang.Object.
     */
    public java.lang.Object getAt(int index) {
        return value[index];
    }

    public void putAt(int index, java.lang.Object value) {
        this.value[index] = value;
    }

    public L2Property copy() {
        L2Property copy = new L2Property(template);
        boolean primitive = PropertiesUtil.isPrimitive(template);
        boolean isStruct = template instanceof StructProperty;

        for (int i = 0; i < getSize(); i++) {
            java.lang.Object currentVal = getAt(i);
            if (currentVal == null) {
                continue;
            }

            if (primitive) {
                copy.putAt(i, currentVal);
            } else if (isStruct) {
                @SuppressWarnings("unchecked")
                List<L2Property> structVal = (List<L2Property>) currentVal;
                copy.putAt(i, PropertiesUtil.cloneStruct(structVal));
            }
        }
        return copy;
    }

    public Property getTemplate() {
        return template;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        L2Property that = (L2Property) o;
        return Objects.equals(template, that.template) && 
               Arrays.deepEquals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(template);
        result = 31 * result + Arrays.deepHashCode(value);
        return result;
    }

    @Override
    public String toString() {
        return "L2Property[" + template + "]";
    }
}