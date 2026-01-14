/*
 * Copyright (c) 2021 acmi
 * Copyright (c) 2026 Galagard/L2Explorer
 */
package org.l2explorer.unreal.properties;

import org.l2explorer.unreal.UnrealException;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.UnrealSerializerFactory;
import org.l2explorer.unreal.core.*;
import org.l2explorer.unreal.core.Class;
import org.l2explorer.io.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class for managing Unreal Properties.
 * All generic values use java.lang.Object to avoid conflicts with Unreal's Object class.
 */
@SuppressWarnings("unchecked")
public class PropertiesUtil {
    private static final Logger log = Logger.getLogger(PropertiesUtil.class.getName());

    public static List<L2Property> readProperties(ObjectInput<UnrealRuntimeContext> objectInput, String objClass) throws UnrealException {
        List<L2Property> properties = new ArrayList<>();
        List<Property> classTemplate = null;
        UnrealPackage up = objectInput.getContext().getUnrealPackage();

        try {
            String name;
            while (!(name = up.getNameTable().get(objectInput.readCompactInt()).getName()).equals("None")) {
                if (classTemplate == null) {
                    classTemplate = getPropertyFields(objectInput.getContext().getSerializer(), objClass).collect(Collectors.toList());
                }

                int info = objectInput.readUnsignedByte();
                Type propertyType = getPropertyType(info);
                int sizeType = getPropertySizeType(info);
                boolean array = isArray(info);

                if (propertyType == Type.STRUCT) {
                    up.nameReference(objectInput.readCompactInt());
                }
                
                int size = readPropertySize(sizeType, objectInput);
                int arrayIndex = (array && propertyType != Type.BOOL) ? objectInput.readCompactInt() : 0;

                final String n = name;
                L2Property property = getAt(properties, n);
                if (property == null) {
                    Property template = classTemplate.parallelStream()
                            .filter(pt -> pt.getEntry().getObjectName().getName().equalsIgnoreCase(n))
                            .filter(pt -> match(pt.getClass(), propertyType))
                            .findAny()
                            .orElse(null);

                    if (template == null) {
                        log.warning(() -> objClass + ": Property template not found: " + n);
                    } else {
                        property = new L2Property(template);
                        properties.add(property);
                    }
                }

                if (property != null) {
                    Struct struct = null;
                    if (propertyType == Type.STRUCT) {
                        struct = ((StructProperty) property.getTemplate()).getStruct();
                    }
                    Property arrayInner = null;
                    if (propertyType == Type.ARRAY) {
                        arrayInner = ((ArrayProperty) property.getTemplate()).getInner();
                    }
                    // O retorno de read() é java.lang.Object, compatível com putAt()
                    property.putAt(arrayIndex, read(objectInput, propertyType, array, arrayInner, struct));
                } else {
                    objectInput.skip(size);
                }
            }
        } catch (Exception e) {
            throw new UnrealException("Failed to read properties for " + objClass, e);
        }
        return properties;
    }

    public static java.lang.Object read(ObjectInput<UnrealRuntimeContext> objBuffer, Type propertyType, boolean array, Property arrayInner, Struct struct) throws IOException {
        return switch (propertyType) {
            case BYTE -> Integer.valueOf(objBuffer.readUnsignedByte());
            case INT -> Integer.valueOf(objBuffer.readInt());
            case BOOL -> Boolean.valueOf(array);
            case FLOAT -> Float.valueOf(objBuffer.readFloat());
            case OBJECT, NAME -> Integer.valueOf(objBuffer.readCompactInt());
            case STR -> objBuffer.readLine();
            case STRUCT -> readStruct(objBuffer, struct);
            case ARRAY -> {
                int arraySize = objBuffer.readCompactInt();
                // AQUI: List de java.lang.Object
                List<java.lang.Object> arrayList = new ArrayList<>(arraySize);
                Type innerType = getType(arrayInner);
                Struct innerStruct = (innerType == Type.STRUCT) ? ((StructProperty) arrayInner).getStruct() : null;
                Property innerArrayInner = (innerType == Type.ARRAY) ? ((ArrayProperty) arrayInner).getInner() : null;
                for (int i = 0; i < arraySize; i++) {
                    arrayList.add(read(objBuffer, innerType, false, innerArrayInner, innerStruct));
                }
                yield arrayList;
            }
            default -> throw new IllegalStateException("Unsupported type: " + propertyType);
        };
    }

    public static List<L2Property> readStruct(ObjectInput<UnrealRuntimeContext> objBuffer, Struct struct) throws IOException {
        String structName = struct.getEntry().getObjectFullName();
        return switch (structName) {
            case "Core.Object.Vector", "Core.Object.Rotator", "Core.Object.Color" -> readStructBin(objBuffer, structName);
            default -> readProperties(objBuffer, structName);
        };
    }

    public static List<L2Property> readStructBin(ObjectInput<UnrealRuntimeContext> objBuffer, String structName) throws IOException {
        List<Property> properties = getPropertyFields(objBuffer.getContext().getSerializer(), structName).collect(Collectors.toList());
        return properties.stream()
                .map(L2Property::new)
                .peek(l2p -> {
                    try {
                        l2p.putAt(0, read(objBuffer, getType(l2p.getTemplate()), false, null, null));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }).collect(Collectors.toList());
    }

    public static void writeProperties(ObjectOutput<UnrealRuntimeContext> output, List<L2Property> properties) throws IOException {
        UnrealPackage up = output.getContext().getUnrealPackage();
        for (L2Property property : properties) {
            Property template = property.getTemplate();
            for (int i = 0; i < property.getSize(); i++) {
                java.lang.Object obj = property.getAt(i);
                if (obj == null) continue;

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutput<UnrealRuntimeContext> objBuffer = new ObjectOutputStream<>(baos, output.getCharset(), 0, output.getSerializerFactory(), output.getContext());
                write(objBuffer, template, obj);
                byte[] bytes = baos.toByteArray();

                Type type = getType(template);
                int sizeFlag = getPropertySizeFlag(bytes.length);
                boolean isArray = (i > 0) || (type == Type.BOOL && (Boolean) obj);
                int info = (isArray ? 0x80 : 0) | (sizeFlag << 4) | type.ordinal();

                output.writeCompactInt(up.nameReference(template.getEntry().getObjectName().getName()));
                output.writeByte(info);
                if (type == Type.STRUCT) {
                    output.writeCompactInt(up.nameReference(((StructProperty) template).getStruct().getEntry().getObjectName().getName()));
                }
                switch (sizeFlag) {
                    case 5 -> output.writeByte(bytes.length);
                    case 6 -> output.writeShort(bytes.length);
                    case 7 -> output.writeInt(bytes.length);
                }
                if (i > 0) output.writeByte(i);
                output.writeBytes(bytes);
            }
        }
        output.writeCompactInt(up.nameReference("None"));
    }

    public static void write(ObjectOutput<UnrealRuntimeContext> objBuffer, Property template, java.lang.Object obj) throws IOException {
        if (template instanceof ByteProperty) objBuffer.writeByte((Integer) obj);
        else if (template instanceof IntProperty) objBuffer.writeInt((Integer) obj);
        else if (template instanceof BoolProperty) {} 
        else if (template instanceof FloatProperty) objBuffer.writeFloat((Float) obj);
        else if (template instanceof ObjectProperty || template instanceof NameProperty) objBuffer.writeCompactInt((Integer) obj);
        else if (template instanceof StrProperty) objBuffer.writeLine((String) obj);
        else if (template instanceof ArrayProperty arrayProp) {
            List<java.lang.Object> list = (List<java.lang.Object>) obj;
            objBuffer.writeCompactInt(list.size());
            for (java.lang.Object item : list) write(objBuffer, arrayProp.getInner(), item);
        } else if (template instanceof StructProperty structProp) {
            writeStruct(objBuffer, structProp.getStruct().getEntry().getObjectFullName(), (List<L2Property>) obj);
        }
    }

    public static void writeStruct(ObjectOutput<UnrealRuntimeContext> objBuffer, String structName, List<L2Property> struct) throws IOException {
        switch (structName) {
            case "Core.Object.Color", "Core.Object.Vector", "Core.Object.Rotator" -> writeStructBin(objBuffer, struct, structName);
            default -> writeProperties(objBuffer, struct);
        }
    }

    public static void writeStructBin(L2DataOutput objBuffer, List<L2Property> struct, String structName) throws IOException {
        switch (structName) {
            case "Core.Object.Color" -> {
                objBuffer.writeByte(findPropInt(struct, "R"));
                objBuffer.writeByte(findPropInt(struct, "G"));
                objBuffer.writeByte(findPropInt(struct, "B"));
                objBuffer.writeByte(findPropInt(struct, "A"));
            }
            case "Core.Object.Vector" -> {
                objBuffer.writeFloat(findPropFloat(struct, "X"));
                objBuffer.writeFloat(findPropFloat(struct, "Y"));
                objBuffer.writeFloat(findPropFloat(struct, "Z"));
            }
            case "Core.Object.Rotator" -> {
                objBuffer.writeInt(findPropInt(struct, "Pitch"));
                objBuffer.writeInt(findPropInt(struct, "Yaw"));
                objBuffer.writeInt(findPropInt(struct, "Roll"));
            }
        }
    }

    private static int findPropInt(List<L2Property> s, String n) {
        return s.stream().filter(p -> p.getName().equalsIgnoreCase(n)).mapToInt(p -> (Integer) p.getAt(0)).findAny().orElse(0);
    }

    private static float findPropFloat(List<L2Property> s, String n) {
        return s.stream().filter(p -> p.getName().equalsIgnoreCase(n)).map(p -> (Float) p.getAt(0)).findAny().orElse(0f);
    }

    public enum Type {
        NONE, BYTE, INT, BOOL, FLOAT, OBJECT, NAME, DELEGATE, CLASS, ARRAY, STRUCT, VECTOR, ROTATOR, STR, MAP, FIXED_ARRAY;
        public java.lang.Class<? extends Property> getPropertyClass() {
            return switch (this) {
                case BYTE -> ByteProperty.class;
                case INT -> IntProperty.class;
                case BOOL -> BoolProperty.class;
                case FLOAT -> FloatProperty.class;
                case OBJECT -> ObjectProperty.class;
                case NAME -> NameProperty.class;
                case ARRAY -> ArrayProperty.class;
                case STRUCT -> StructProperty.class;
                case STR -> StrProperty.class;
                default -> null;
            };
        }
    }

    public static Type getPropertyType(int info) { return Type.values()[info & 0b1111]; }
    public static int getPropertySizeType(int info) { return (info >> 4) & 0b111; }
    public static boolean isArray(int info) { return (info & 0x80) != 0; }

    public static int readPropertySize(int sizeType, L2DataInput in) throws IOException {
        return switch (sizeType) {
            case 0 -> 1; case 1 -> 2; case 2 -> 4; case 3 -> 12; case 4 -> 16;
            case 5 -> in.readUnsignedByte();
            case 6 -> in.readUnsignedShort();
            case 7 -> in.readInt();
            default -> 0;
        };
    }

    public static int getPropertySizeFlag(int size) {
        if (size == 1) return 0; if (size == 2) return 1; if (size == 4) return 2;
        if (size == 12) return 3; if (size == 16) return 4;
        if (size < 0x100) return 5; if (size < 0x10000) return 6;
        return 7;
    }

    public static L2Property getAt(List<L2Property> properties, String name) {
        return properties.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static Stream<Property> getPropertyFields(UnrealSerializerFactory serializer, String structName) {
        Stream<Property> props = Stream.empty();
        String current = structName;
        while (current != null) {
            Struct struct = serializer.getStruct(current).orElse(null);
            if (struct == null) break;
            props = Stream.concat(props, StreamSupport.stream(struct.spliterator(), false)
                    .filter(f -> f instanceof Property).map(f -> (Property) f));
            current = (struct.getEntry() != null && struct.getEntry().getObjectSuperClass() != null)
                    ? struct.getEntry().getObjectSuperClass().getObjectFullName() : null;
        }
        return props;
    }

    private static boolean match(java.lang.Class<? extends Property> clazz, Type type) {
        java.lang.Class<? extends Property> expected = type.getPropertyClass();
        return expected != null && expected.isAssignableFrom(clazz);
    }

    public static Type getType(Property property) {
        for (Type type : Type.values()) {
            if (type.getPropertyClass() != null && type.getPropertyClass().isAssignableFrom(property.getClass())) return type;
        }
        return Type.NONE;
    }

    public static boolean isPrimitive(Property property) {
        return property instanceof ByteProperty || property instanceof IntProperty || property instanceof BoolProperty ||
               property instanceof FloatProperty || property instanceof StrProperty;
    }

    public static List<L2Property> cloneStruct(List<L2Property> struct) {
        return struct.stream().map(L2Property::copy).collect(Collectors.toList());
    }

    public static java.lang.Object defaultValue(Property property, String structName, UnrealSerializerFactory serializer, UnrealPackage up) throws IOException {
        Optional<Class> classOpt = serializer.getStruct(structName)
                .filter(struct -> struct instanceof Class).map(struct -> (Class) struct);

        if (classOpt.isPresent()) {
            final java.lang.Object[] result = new java.lang.Object[1];
            serializer.getStructTree(classOpt.get()).forEach(sc -> sc.getProperties().parallelStream()
                    .filter(p -> p.getTemplate().equals(property)).findAny()
                    .ifPresent(p -> result[0] = p.getAt(0)));
            if (result[0] != null) return isPrimitive(property) ? result[0] : cloneStruct((List<L2Property>) result[0]);
        }
        return defaultValue(property, serializer, up);
    }

    public static java.lang.Object defaultValue(Property property, UnrealSerializerFactory serializer, UnrealPackage up) {
        if (property instanceof ByteProperty || property instanceof IntProperty || property instanceof ObjectProperty) return Integer.valueOf(0);
        if (property instanceof BoolProperty) return Boolean.valueOf(false);
        if (property instanceof FloatProperty) return Float.valueOf(0f);
        if (property instanceof NameProperty) return Integer.valueOf(up.nameReference("None"));
        if (property instanceof StrProperty) return "";
        if (property instanceof ArrayProperty) return new ArrayList<java.lang.Object>();
        if (property instanceof StructProperty sp) {
            String name = sp.getStruct().getEntry().getObjectFullName();
            return getPropertyFields(serializer, name).map(p -> {
                L2Property l2p = new L2Property(p);
                for (int i = 0; i < l2p.getSize(); i++)
					try {
						l2p.putAt(i, defaultValue(p, name, serializer, up));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                return l2p;
            }).collect(Collectors.toList());
        }
        return null;
    }
}