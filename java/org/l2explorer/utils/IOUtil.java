package org.l2explorer.utils;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IOUtil {
    public static String readString(InputStream buffer) throws IOException {
        return StreamsHelper.readString(buffer);
    }

    public static int readCompactInt(InputStream buffer) throws IOException {
        return StreamsHelper.readCompactInt(buffer);
    }

    @SuppressWarnings("resource")
	public static int readInt(InputStream buffer) throws IOException {
        return Integer.reverseBytes(new DataInputStream(buffer).readInt());
    }

    /**
     * Adaptado para java.awt.Color
     * O AWT usa (r, g, b, a) onde alpha é 0-255
     */
    public static Color intToARGB(int val) {
        int a = (val >> 24) & 0xff;
        int r = (val >> 16) & 0xff;
        int g = (val >> 8) & 0xff;
        int b = val & 0xff;
        return new Color(r, g, b, a);
    }

    public static Color readColor(InputStream buffer) throws IOException {
        return intToARGB(readInt(buffer));
    }

    public static Color intToRGBA(int val) {
        int r = val & 0xff;
        int g = (val >> 8) & 0xff;
        int b = (val >> 16) & 0xff;
        int a = (val >> 24) & 0xff;
        return new Color(r, g, b, a);
    }

    public static Color readRGBA(InputStream buffer) throws IOException {
        return intToRGBA(readInt(buffer));
    }

    public static Boolean intToBool(int val) {
        switch (val) {
            case 0: return false;
            case 1: return true;
            default: return null;
        }
    }

    public static Boolean readBoolean(InputStream buffer) throws IOException {
        return intToBool(readInt(buffer));
    }

    public static float readFloat(InputStream buffer) throws IOException {
        return Float.intBitsToFloat(Integer.reverseBytes(new DataInputStream(buffer).readInt()));
    }

    public static UIEntity readUIEntity(InputStream buffer, String pckg, ClassLoader classLoader) throws IOException {
        String objClass = readString(buffer);
        try {
            Class<? extends UIEntity> clazz = Class.forName(pckg + "." + objClass, true, classLoader).asSubclass(UIEntity.class);
            return readIOEntity(buffer, clazz);
        } catch (ReflectiveOperationException e) {
            throw new IOException(e);
        }
    }

    @SuppressWarnings("deprecation")
	public static <T extends IOEntity> T readIOEntity(InputStream buffer, Class<?> genericClass) throws IOException {
        T object;
        try {
            object = (T) genericClass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IOException("Couldn't instantiate " + genericClass, e);
        }
        object.read(buffer);
        return object;
    }

    public static <T extends Enum<T>> T readEnum(InputStream buffer, Class<T> enumType) throws IOException {
        if (StringValue.class.isAssignableFrom(enumType))
            return Enum.valueOf(enumType, readString(buffer));

        int val = readInt(buffer);
        T[] constants = enumType.getEnumConstants();
        if (IntValue.class.isAssignableFrom(enumType)) {
            return Arrays.stream(constants)
                    .filter(c -> ((IntValue) c).intValue() == val)
                    .findAny()
                    .orElseThrow(() -> new IOException(enumType.getName() + ": invalid enum value: " + val));
        } else {
            if (val < 0 || val >= constants.length)
                throw new IOException(enumType.getName() + ": invalid enum index: " + val);
            return constants[val];
        }
    }

    /**
     * Removido FXCollections.observableList
     */
    @SuppressWarnings({ "unchecked" })
	public static <T extends IOEntity> List<T> readList(InputStream buffer, Class<?> genericClass, ArrayLength lengthType) throws IOException {
        int len;
        switch (lengthType) {
            case COMPACT_INT:
                len = readCompactInt(buffer);
                break;
            case INT:
            default:
                len = readInt(buffer);
        }
        List<T> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            T obj;
            if (UIEntity.class.isAssignableFrom(genericClass)) {
                obj = (T) readUIEntity(buffer, genericClass.getPackage().getName(), genericClass.getClassLoader());
            } else {
                obj = readIOEntity(buffer, genericClass);
            }
            list.add(obj);
        }
        return list; // Retorna ArrayList pura
    }

    public static <T extends IOEntity> List<T> readList(InputStream buffer, Class<?> genericClass) throws IOException {
        return readList(buffer, genericClass, ArrayLength.INT);
    }

    public static void writeString(OutputStream buffer, String val) throws IOException {
        StreamsHelper.writeString(buffer, val);
    }

    public static void writeCompactInt(OutputStream buffer, int val) throws IOException {
        StreamsHelper.writeCompactInt(buffer, val);
    }

    @SuppressWarnings("resource")
	public static void writeInt(OutputStream buffer, int val) throws IOException {
        new DataOutputStream(buffer).writeInt(Integer.reverseBytes(val));
    }

    public static int colorToARGB(Color color) {
        if (color == null) return 0;
        return (color.getBlue()) |
                (color.getGreen() << 8) |
                (color.getRed() << 16) |
                (color.getAlpha() << 24);
    }

    public static void writeColor(OutputStream buffer, Color color) throws IOException {
        writeInt(buffer, colorToARGB(color));
    }

    public static int colorToRGBA(Color color) {
        if (color == null) return 0;
        return (color.getRed()) |
                (color.getGreen() << 8) |
                (color.getBlue() << 16) |
                (color.getAlpha() << 24);
    }

    public static void writeRGBA(OutputStream buffer, Color color) throws IOException {
        writeInt(buffer, colorToRGBA(color));
    }

    public static int boolToInt(Boolean val) {
        return val == null ? -1 : val ? 1 : 0;
    }

    public static void writeBoolean(OutputStream buffer, Boolean val) throws IOException {
        new DataOutputStream(buffer).writeInt(Integer.reverseBytes(boolToInt(val)));
    }

    public static void writeFloat(OutputStream buffer, float val) throws IOException {
        new DataOutputStream(buffer).writeInt(Integer.reverseBytes(Float.floatToIntBits(val)));
    }

    public static void writeUIEntity(OutputStream buffer, UIEntity val) throws IOException {
        writeString(buffer, val.getClass().getSimpleName());
        writeIOEntity(buffer, val);
    }

    public static void writeIOEntity(OutputStream buffer, IOEntity val) throws IOException {
        val.write(buffer);
    }

    public static <T extends Enum<T>> void writeEnum(OutputStream buffer, Enum<?> value) throws IOException {
        if (value instanceof IntValue)
            writeInt(buffer, ((IntValue) value).intValue());
        else if (value instanceof StringValue)
            writeString(buffer, value.toString());
        else
            writeInt(buffer, value.ordinal());
    }

    @SuppressWarnings("unchecked")
	public static void writeList(OutputStream buffer, @SuppressWarnings("rawtypes") List list) throws IOException {
        writeList(buffer, list, ArrayLength.INT);
    }

    public static <T extends IOEntity> void writeList(OutputStream buffer, List<T> list, ArrayLength lengthType) throws IOException {
        if (list == null) {
            if (lengthType == ArrayLength.COMPACT_INT) writeCompactInt(buffer, 0);
            else writeInt(buffer, 0);
            return;
        }
        
        switch (lengthType) {
            case COMPACT_INT:
                writeCompactInt(buffer, list.size());
                break;
            case INT:
            default:
                writeInt(buffer, list.size());
        }
        for (T obj : list) {
            if (obj instanceof UIEntity)
                writeUIEntity(buffer, (UIEntity) obj);
            else
                writeIOEntity(buffer, obj);
        }
    }
}