/*
 * Copyright (c) 2016 acmi
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
package org.l2explorer.utils.unreal;

import org.l2explorer.io.UnrealPackage;
import org.l2explorer.unreal.UnrealPackageContext;
import org.l2explorer.unreal.UnrealSerializerFactory;

/**
 * Provides specific runtime context for an ExportEntry within an Unreal Package.
 * <p>This context links the package data with the appropriate serializer 
 * needed to instantiate game objects from the binary data.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public class UnrealContext extends UnrealPackageContext {
    private final UnrealPackage.ExportEntry entry;
    private final UnrealSerializerFactory serializer;

    /**
     * Constructs a new UnrealContext for a specific export entry.
     *
     * @param entry      The {@link UnrealPackage.ExportEntry} being processed.
     * @param serializer The {@link UnrealSerializerFactory} to be used for object mapping.
     * @throws NullPointerException if entry or serializer is null.
     */
    public UnrealContext(UnrealPackage.ExportEntry entry, UnrealSerializerFactory serializer) {
        super(entry.getUnrealPackage());
        this.entry = entry;
        this.serializer = serializer;
    }

    /**
     * Gets the export entry associated with this context.
     *
     * @return the {@link UnrealPackage.ExportEntry}.
     */
    public UnrealPackage.ExportEntry getEntry() {
        return entry;
    }

    /**
     * Gets the serializer factory associated with this context.
     *
     * @return the {@link UnrealSerializerFactory}.
     */
    public UnrealSerializerFactory getSerializer() {
        return serializer;
    }

    @Override
    public String toString() {
        return "UnrealContext[" +
                "package=" + getUnrealPackage().getPackageName() +
                ", entry=" + entry.getObjectName() +
                ", serializer=" + serializer.getClass().getSimpleName() +
                ']';
    }
}