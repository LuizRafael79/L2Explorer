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
 */
package org.l2explorer.utils.defaultio;

import org.l2explorer.utils.IOEntity;
import org.l2explorer.utils.IOUtil;
import org.l2explorer.utils.UIEntity;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Modern Java 25 replacement for Groovy's AST Transformation using Swing/AWT components.
 * <p>Uses Reflection to read and write fields of objects annotated with {@link DefaultIOTransformation}.
 * This version uses {@link java.awt.Color} for compatibility with Swing UIs.</p>
 *
 * @author acmi (Original Logic)
 * @author Galagard (L2Explorer Modernization)
 * @since 13-01-2026
 */
public final class ReflectiveObjectIO {

    /**
     * Reads data from an InputStream into an object's fields via Reflection.
     *
     * @param target The object instance to populate.
     * @param input  The source InputStream.
     * @throws IOException If I/O or Reflection fails.
     */
    public static void read(java.lang.Object target, InputStream input) throws IOException {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (shouldSkip(field)) continue;

            field.setAccessible(true);
            try {
                java.lang.Object value = readField(field, input, target);
                field.set(target, value);
            } catch (ReflectiveOperationException e) {
                throw new IOException("Failed to read field: " + field.getName(), e);
            }
        }
    }

    /**
     * Writes data from an object's fields to an OutputStream via Reflection.
     *
     * @param target The object instance to read from.
     * @param output The destination OutputStream.
     * @throws IOException If I/O or Reflection fails.
     */
    public static void write(java.lang.Object target, OutputStream output) throws IOException {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (shouldSkip(field)) continue;

            field.setAccessible(true);
            try {
                java.lang.Object value = field.get(target);
                writeField(field, value, output);
            } catch (ReflectiveOperationException e) {
                throw new IOException("Failed to write field: " + field.getName(), e);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static java.lang.Object readField(Field field, InputStream input, java.lang.Object target) 
            throws IOException, ReflectiveOperationException {
        
        Class<?> type = field.getType();

        // Standard Primitives and CompactInt
        if (type == int.class || type == Integer.class) {
            if (field.isAnnotationPresent(CompactInt.class)) {
                return IOUtil.readCompactInt(input);
            }
            return IOUtil.readInt(input);
        }
        if (type == boolean.class || type == Boolean.class) return IOUtil.readBoolean(input);
        if (type == float.class || type == Float.class) return IOUtil.readFloat(input);
        if (type == String.class) return IOUtil.readString(input);
        if (type.isEnum()) return IOUtil.readEnum(input, (Class<? extends Enum>) type);
        
        // AWT Color support (Swing)
        if (type == Color.class) {
            if (field.isAnnotationPresent(RGBA.class)) {
                return IOUtil.readRGBA(input);
            }
            return IOUtil.readColor(input);
        }

        // Collection support
        if (List.class.isAssignableFrom(type)) {
            ParameterizedType listType = (ParameterizedType) field.getGenericType();
            Class<?> genericClass = (Class<?>) listType.getActualTypeArguments()[0];
            return IOUtil.readList(input, genericClass);
        }

        // Unreal Entity support
        if (UIEntity.class.isAssignableFrom(type)) {
            return IOUtil.readUIEntity(input, target.getClass().getPackageName(), target.getClass().getClassLoader());
        }

        if (IOEntity.class.isAssignableFrom(type)) {
            return IOUtil.readIOEntity(input, (Class<? extends IOEntity>) type);
        }

        throw new IllegalStateException("Unsupported field type for Reflection: " + type.getName());
    }

    private static void writeField(Field field, java.lang.Object value, OutputStream output) throws IOException {
        Class<?> type = field.getType();

        if (type == int.class || type == Integer.class) {
            if (field.isAnnotationPresent(CompactInt.class)) {
                IOUtil.writeCompactInt(output, (Integer) value);
            } else {
                IOUtil.writeInt(output, (Integer) value);
            }
        } else if (type == boolean.class || type == Boolean.class) {
            IOUtil.writeBoolean(output, (Boolean) value);
        } else if (type == float.class || type == Float.class) {
            IOUtil.writeFloat(output, (Float) value);
        } else if (type == String.class) {
            IOUtil.writeString(output, (String) value);
        } else if (type.isEnum()) {
            IOUtil.writeEnum(output, (Enum<?>) value);
        } else if (type == Color.class) {
            if (field.isAnnotationPresent(RGBA.class)) IOUtil.writeRGBA(output, (Color) value);
            else IOUtil.writeColor(output, (Color) value);
        } else if (List.class.isAssignableFrom(type)) {
            IOUtil.writeList(output, (List<?>) value);
        } else if (IOEntity.class.isAssignableFrom(type)) {
            IOUtil.writeIOEntity(output, (IOEntity) value);
        }
    }

    private static boolean shouldSkip(Field field) {
        int mod = field.getModifiers();
        return Modifier.isStatic(mod) || Modifier.isTransient(mod);
    }

    private ReflectiveObjectIO() {}
}