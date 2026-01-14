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

import org.l2explorer.unreal.annotation.NameRef;

/**
 * Represents an Enumeration definition in the Unreal Engine.
 * <p>Enums store a list of names that represent the available 
 * constant values for this enumeration type.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Modernization)
 * @since 13-01-2026
 */
public class Enum extends Field {
    /**
     * The list of names (constants) defined in this enumeration.
     */
    @NameRef
    private String[] values;

    public Enum() {
    }

    /**
     * Gets the enumeration values.
     *
     * @return An array of strings representing the enum constants.
     */
    public String[] getValues() {
        return values;
    }

    /**
     * Sets the enumeration values.
     *
     * @param values An array of strings to set as enum constants.
     */
    public void setValues(String[] values) {
        this.values = values;
    }
}