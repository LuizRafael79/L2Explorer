package org.l2explorer.io;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.l2explorer.io.annotation.*;

public class ReflectionSerializerFactory<C extends Context> implements SerializerFactory<C> {
    @SuppressWarnings("rawtypes")
    protected final Map<Class, Serializer> cache = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Serializer<T, C> forClass(Class<T> clazz) {
        if (!cache.containsKey(clazz)) {
            createForClass(clazz);
        }
        return (Serializer<T, C>) cache.get(clazz);
    }

    protected void createForClass(Class<?> clazz) {
        List<BiConsumer<Object, ObjectInput<C>>> readActions = new ArrayList<>();
        List<BiConsumer<Object, ObjectOutput<C>>> writeActions = new ArrayList<>();

        Serializer<?, C> serializer = createSerializer(clazz, readActions, writeActions);

        cache.put(clazz, serializer);

        buildForClass(clazz, readActions, writeActions);
    }

    protected Serializer<?, C> createSerializer(Class<?> clazz, List<BiConsumer<Object, ObjectInput<C>>> readActions, List<BiConsumer<Object, ObjectOutput<C>>> writeActions) {
        return new SerializerImpl(clazz, readActions, writeActions);
    }

    @SuppressWarnings("unused")
	protected Function<ObjectInput<C>, Object> createInstantiator(Class<?> clazz) {
        return input -> ReflectionUtil.instantiate(clazz);
    }

    protected BiConsumer<Object, ObjectInput<C>> createReader(Class<?> clazz, List<BiConsumer<Object, ObjectInput<C>>> readActions) {
        return (obj, input) -> readActions.forEach(action -> action.accept(obj, input));
    }

    protected BiConsumer<Object, ObjectOutput<C>> createWriter(Class<?> clazz, List<BiConsumer<Object, ObjectOutput<C>>> writeActions) {
        return (obj, output) -> writeActions.forEach(action -> action.accept(obj, output));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <T> void buildForClass(Class<?> clazz, List<BiConsumer<T, ObjectInput<C>>> read, List<BiConsumer<T, ObjectOutput<C>>> write) {
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            Serializer superIO = forClass(clazz.getSuperclass());
            read.add((o, input) -> {
                try {
                    superIO.readObject(o, input);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            write.add((o, output) -> {
                try {
                    superIO.writeObject(o, output);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        List<BiConsumer<T, ObjectInput<C>>> read1 = new ArrayList<>();
        List<BiConsumer<T, ObjectOutput<C>>> write1 = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (!validField(field)) {
                continue;
            }

            handleField(field, read1, write1);
        }

        boolean readMethod = false;
        boolean writeMethod = false;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ReadMethod.class)) {
                read.add((o, dataInput) -> ReflectionUtil.invokeMethod(method, o, dataInput));
                readMethod = true;
            }

            if (method.isAnnotationPresent(WriteMethod.class)) {
                write.add((o, dataOutput) -> ReflectionUtil.invokeMethod(method, o, dataOutput));
                writeMethod = true;
            }
        }
        if (!readMethod) {
            read.addAll(read1);
        }
        if (!writeMethod) {
            write.addAll(write1);
        }
    }

    protected boolean validField(Field field) {
        return !Modifier.isStatic(field.getModifiers()) &&
                !Modifier.isTransient(field.getModifiers()) &&
                !field.isSynthetic();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> void handleField(Field field, List<BiConsumer<T, ObjectInput<C>>> readActions, List<BiConsumer<T, ObjectOutput<C>>> writeActions) {
        field.setAccessible(true);

        Custom custom = field.getAnnotation(Custom.class);
        if (custom != null) {
            if (!cache.containsKey(custom.value())) {
                cache.put(custom.value(), ReflectionUtil.instantiate(custom.value()));
            }
            Serializer customSerializer = cache.get(custom.value());
            readActions.add((object, input) -> {
                try {
                    Object obj = customSerializer.instantiate(input);
                    customSerializer.readObject(obj, input);
                    ReflectionUtil.fieldSet(field, object, obj);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writeActions.add((object, output) -> {
                try {
                    customSerializer.writeObject(ReflectionUtil.fieldGet(field, object), output);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            serializer(field.getType(),
                    object -> ReflectionUtil.fieldGet(field, object),
                    (obj, val) -> ReflectionUtil.fieldSet(field, obj, val.get()),
                    field::getAnnotation,
                    readActions,
                    writeActions);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> void serializer(Class type,
                                  Function<T, Object> getter, BiConsumer<T, Supplier> setter,
                                  Function<Class<? extends Annotation>, Annotation> getAnnotation,
                                  List<BiConsumer<T, ObjectInput<C>>> read,
                                  List<BiConsumer<T, ObjectOutput<C>>> write) {
        
        // --- TRUQUE: Wrapper para IOException dentro de lambdas ---
        // Como BiConsumer não aceita 'throws IOException', usamos try-catch interno.
        
        if (type == Byte.TYPE || type == Byte.class) {
            read.add((object, dataInput) -> {
                try {
                    setter.accept(object, () -> {
                        try { return (byte) dataInput.readUnsignedByte(); } 
                        catch (IOException e) { throw new RuntimeException(e); }
                    });
                } catch (Exception e) { throw new RuntimeException(e); }
            });
            write.add((object, dataOutput) -> {
                try { dataOutput.writeByte(((Byte) getter.apply(object))); } 
                catch (IOException e) { throw new RuntimeException(e); }
            });
        } else if (type == Short.TYPE || type == Short.class) {
            read.add((object, dataInput) -> {
                try { setter.accept(object, () -> {
                    try { return (short) dataInput.readUnsignedShort(); } 
                    catch (IOException e) { throw new RuntimeException(e); }
                }); } catch (Exception e) { throw new RuntimeException(e); }
            });
            write.add((object, dataOutput) -> {
                try { dataOutput.writeShort(((Short) getter.apply(object))); } 
                catch (IOException e) { throw new RuntimeException(e); }
            });
        } else if (type == Integer.TYPE || type == Integer.class) {
            if (getAnnotation.apply(Compact.class) != null) {
                read.add((object, dataInput) -> {
                    try { setter.accept(object, () -> {
                        try {
							return dataInput.readCompactInt();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return object;
                    }); } catch (Exception e) { throw new RuntimeException(e); }
                });
                write.add((object, dataOutput) -> {
                    try { dataOutput.writeCompactInt(((Integer) getter.apply(object))); } 
                    catch (IOException e) { throw new RuntimeException(e); }
                });
            } else {
                read.add((object, dataInput) -> {
                    try { setter.accept(object, () -> {
                        try { return dataInput.readInt(); } 
                        catch (IOException e) { throw new RuntimeException(e); }
                    }); } catch (Exception e) { throw new RuntimeException(e); }
                });
                write.add((object, dataOutput) -> {
                    try { dataOutput.writeInt(((Integer) getter.apply(object))); } 
                    catch (IOException e) { throw new RuntimeException(e); }
                });
            }
        } else if (type == Long.TYPE || type == Long.class) {
            read.add((object, dataInput) -> {
                try { setter.accept(object, () -> {
                    try { return dataInput.readLong(); } 
                    catch (IOException e) { throw new RuntimeException(e); }
                }); } catch (Exception e) { throw new RuntimeException(e); }
            });
            write.add((object, dataOutput) -> {
                try { dataOutput.writeLong(((Long) getter.apply(object))); } 
                catch (IOException e) { throw new RuntimeException(e); }
            });
        } else if (type == Float.TYPE || type == Float.class) {
            read.add((object, dataInput) -> {
                try { setter.accept(object, () -> {
                    try { return dataInput.readFloat(); } 
                    catch (IOException e) { throw new RuntimeException(e); }
                }); } catch (Exception e) { throw new RuntimeException(e); }
            });
            write.add((object, dataOutput) -> {
                try { dataOutput.writeFloat(((Float) getter.apply(object))); } 
                catch (IOException e) { throw new RuntimeException(e); }
            });
        } else if (type == String.class) {
            if (getAnnotation.apply(UTF.class) != null) {
                read.add((object, dataInput) -> {
                    try { setter.accept(object, () -> {
                        try { return dataInput.readUTF(); } 
                        catch (IOException e) { throw new RuntimeException(e); }
                    }); } catch (Exception e) { throw new RuntimeException(e); }
                });
                write.add((object, dataOutput) -> {
                    try { dataOutput.writeUTF(((String) getter.apply(object))); } 
                    catch (IOException e) { throw new RuntimeException(e); }
                });
            } else {
                read.add((object, dataInput) -> {
                    try { setter.accept(object, () -> {
                        try { return dataInput.readLine(); } 
                        catch (IOException e) { throw new RuntimeException(e); }
                    }); } catch (Exception e) { throw new RuntimeException(e); }
                });
                write.add((object, dataOutput) -> {
                    try { dataOutput.writeLine(((String) getter.apply(object))); } 
                    catch (IOException e) { throw new RuntimeException(e); }
                });
            }
        } else if (type.isArray()) {
            Class componentType = type.getComponentType();
            read.add((object, dataInput) -> {
                int len = 0;
				try {
					len = dataInput.readCompactInt();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Object array = Array.newInstance(componentType, len);
				for (int i = 0; i < len; i++) {
				    int ind = i;
				    List<BiConsumer<Object, ObjectInput<C>>> arrayRead = new ArrayList<>();
				    List<BiConsumer<Object, ObjectOutput<C>>> arrayWrite = new ArrayList<>();
				    serializer(componentType, arr -> Array.get(arr, ind), (arr, val) -> Array.set(arr, ind, val.get()), getAnnotation, arrayRead, arrayWrite);
				    for (BiConsumer<Object, ObjectInput<C>> ra : arrayRead) {
				        ra.accept(array, dataInput);
				    }
				}
				setter.accept(object, () -> array);
            });
            write.add((object, dataOutput) -> {
                try {
                    Object array = getter.apply(object);
                    int len = Array.getLength(array);
                    dataOutput.writeCompactInt(len);
                    for (int i = 0; i < len; i++) {
                        int ind = i;
                        List<BiConsumer<Object, ObjectInput<C>>> arrayRead = new ArrayList<>();
                        List<BiConsumer<Object, ObjectOutput<C>>> arrayWrite = new ArrayList<>();
                        serializer(componentType, arr -> Array.get(arr, ind), (arr, val) -> Array.set(arr, ind, val.get()), getAnnotation, arrayRead, arrayWrite);
                        for (BiConsumer<Object, ObjectOutput<C>> wa : arrayWrite) {
                            wa.accept(array, dataOutput);
                        }
                    }
                } catch (IOException e) { throw new RuntimeException(e); }
            });
        } else {
            read.add((object, dataInput) -> {
                try {
                    Serializer typeSerializer = forClass(type);
                    Object obj = typeSerializer.instantiate(dataInput);
                    if (obj != null) {
                        Serializer realTypeSerializer = forClass(obj.getClass());
                        realTypeSerializer.readObject(obj, dataInput);
                    }
                    setter.accept(object, () -> obj);
                } catch (IOException e) { throw new RuntimeException(e); }
            });
            write.add((object, dataOutput) -> {
                try {
                    Object obj = getter.apply(object);
                    if (obj != null) {
                        Serializer realTypeSerializer = forClass(obj.getClass());
                        realTypeSerializer.writeObject(obj, dataOutput);
                    }
                } catch (IOException e) { throw new RuntimeException(e); }
            });
        }
    }

    protected class SerializerImpl implements Serializer<Object, C> {
        protected final Class<?> clazz;
        protected final List<BiConsumer<Object, ObjectInput<C>>> readActions;
        protected final List<BiConsumer<Object, ObjectOutput<C>>> writeActions;

        private Function<ObjectInput<C>, Object> instantiator;
        private BiConsumer<Object, ObjectInput<C>> reader;
        private BiConsumer<Object, ObjectOutput<C>> writer;

        public SerializerImpl(Class<?> clazz, List<BiConsumer<Object, ObjectInput<C>>> readActions, List<BiConsumer<Object, ObjectOutput<C>>> writeActions) {
            this.clazz = clazz;
            this.readActions = readActions;
            this.writeActions = writeActions;
        }

        @Override
        public Object instantiate(ObjectInput<C> input) throws IOException {
            if (instantiator == null) {
                instantiator = createInstantiator(clazz);
            }
            return instantiator.apply(input);
        }

        @Override
        public <S> void readObject(S obj, ObjectInput<C> input) throws IOException {
            if (obj == null) return;
            if (reader == null) {
                reader = createReader(clazz, readActions);
            }
            try {
                reader.accept(obj, input);
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
                throw e;
            }
        }

        @Override
        public <S> void writeObject(S obj, ObjectOutput<C> output) throws IOException {
            if (obj == null) return;
            if (writer == null) {
                writer = createWriter(clazz, writeActions);
            }
            try {
                writer.accept(obj, output);
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
                throw e;
            }
        }

        @Override
        public String toString() {
            return "Serializer[" + clazz + "]";
        }
    }
}