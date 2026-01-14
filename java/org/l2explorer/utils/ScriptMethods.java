package org.l2explorer.utils;

// Substituindo JavaFX por AWT
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptMethods {
    
    // Classe interna simples para substituir o javafx.util.Pair
    private static class EntryPair<K, V> {
        private final K key;
        private final V value;
        public EntryPair(K key, V value) { this.key = key; this.value = value; }
        public K getKey() { return key; }
        public V getValue() { return value; }
    }

    public static Named getAt(List<? extends Named> list, String name) {
        return list.parallelStream()
                .filter(e -> String.valueOf(e.getName()).equalsIgnoreCase(String.valueOf(name)))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(name + " not found"));
    }

    /**
     * Adaptado para java.awt.Color
     */
    public static String constructorString(Object object) {
        if (object instanceof Color) {
            Color color = (Color) object;
            // AWT usa 0-255 diretamente
            return String.format("Color.decode(\"0x%02x%02x%02x%02x\")", 
                    color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
        } else if (object instanceof Enum) {
            Enum<?> enumObject = (Enum<?>) object;
            return String.format("%s.%s", getClassName(enumObject.getDeclaringClass()), enumObject.name());
        } else if (object instanceof CharSequence) {
            return String.format("\"%s\"", object);
        }

        return String.valueOf(object);
    }

    public static String getClassName(Class<?> clazz) {
        String className = clazz.getName();
        return className.substring(className.indexOf('.') + 1).replaceAll("\\$", ".");
    }

    public static String toPropertyString(Object obj) {
        return toPropertyString(obj, false, true, false);
    }

    public static String toPropertyString(Object obj, boolean includeNames) {
        return toPropertyString(obj, includeNames, true, false);
    }

    public static String toPropertyString(Object obj, boolean includeNames, boolean includeParent) {
        return toPropertyString(obj, includeNames, includeParent, false);
    }

    @SuppressWarnings("unchecked")
    public static String toPropertyString(Object obj, boolean includeNames, boolean includeParent, boolean listContent) {
        // Usando nossa classe interna EntryPair
        List<EntryPair<String, String>> properties = new ArrayList<>();

        @SuppressWarnings("rawtypes")
		LinkedList<Class> list = new LinkedList<>();
        Class<?> c = obj.getClass();

        if (includeParent)
            while (c != Object.class && c != null) {
                list.add(c);
                c = c.getSuperclass();
            }
        else
            list.add(c);

        while (!list.isEmpty()) {
            c = list.pollLast();
            for (Field f : c.getDeclaredFields()) {
                if (f.isSynthetic())
                    continue;

                f.setAccessible(true);
                try {
                    Object o = f.get(obj);

                    String s;
                    if (o instanceof List) {
                        if (listContent)
                            s = "[" + ((List<Object>) o).stream()
                                    .map(i -> i instanceof IOEntity ? toPropertyString(i, includeNames, includeParent, true) : Objects.toString(i))
                                    .collect(Collectors.joining("\t")) + "]";
                        else
                            s = "[..]";
                    } else {
                        s = Objects.toString(o);
                    }

                    properties.add(new EntryPair<>(f.getName(), s));
                } catch (IllegalAccessException ignore) {
                }
            }
        }

        return '[' + properties.stream()
                .map(p -> includeNames ? p.getKey() + "=" + p.getValue() : p.getValue())
                .collect(Collectors.joining("\t")) + ']';
    }

    public static void removeByName(Collection<? extends Named> collection, String name) {
        collection.removeIf(o -> Objects.nonNull(o) && Objects.equals(o.getName(), name));
    }
}