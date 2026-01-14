/*
 * Copyright (c) 2021 acmi
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
package org.l2explorer.unreal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.l2explorer.io.ObjectInput;
import org.l2explorer.io.ObjectInputStream;
import org.l2explorer.io.ObjectOutput;
import org.l2explorer.io.RandomAccessMemory;
import org.l2explorer.io.ReflectionSerializerFactory;
import org.l2explorer.io.Serializer;
import org.l2explorer.io.SerializerFactory;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.unreal.annotation.Bytecode;
import org.l2explorer.unreal.annotation.NameRef;
import org.l2explorer.unreal.annotation.ObjectRef;
import org.l2explorer.unreal.bytecode.BytecodeContext;
import org.l2explorer.unreal.bytecode.TokenSerializerFactory;
import org.l2explorer.unreal.bytecode.token.Token;
import org.l2explorer.unreal.core.Object;
import org.l2explorer.unreal.core.Struct;
import org.l2explorer.unreal.properties.PropertiesUtil;
import org.l2explorer.unreal.util.InvalidationListener;
import org.l2explorer.unreal.util.ObservableSet;
import org.l2explorer.unreal.util.ObservableSetWrapper;

@SuppressWarnings("unchecked")
public class UnrealSerializerFactory extends ReflectionSerializerFactory<UnrealRuntimeContext> {
    private static final Logger log = Logger.getLogger(UnrealSerializerFactory.class.getName());

    private static final String LOAD_THREAD_NAME = "Unreal loader";
    private static final int LOAD_THREAD_STACK_SIZE = loadThreadStackSize();

    private static int loadThreadStackSize() {
        try {
            return Integer.parseInt(System.getProperty("L2unreal.loadThreadStackSize", "8000000"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static final String unrealClassesPackage = "org.l2explorer.unreal";

    public static final Predicate<String> IS_STRUCT = c -> c.equalsIgnoreCase("Core.Struct") ||
            c.equalsIgnoreCase("Core.Function") ||
            c.equalsIgnoreCase("Core.State") ||
            c.equalsIgnoreCase("Core.Class");

    private final Map<String, Object> objects = new HashMap<>();
    private final Map<Integer, org.l2explorer.unreal.core.Function> nativeFunctions = new HashMap<>();
    private final ObservableSet<String> loaded = new ObservableSetWrapper<>(new HashSet<>());

    private final Env environment;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(r -> new Thread(null, r, LOAD_THREAD_NAME, LOAD_THREAD_STACK_SIZE) {{
        setDaemon(true);
    }});

    public UnrealSerializerFactory(Env environment) {
        this.environment = new EnvironmentWrapper(environment);
    }

    @Override
    protected Function<ObjectInput<UnrealRuntimeContext>, java.lang.Object> createInstantiator(Class<?> clazz) {
        if (Object.class.isAssignableFrom(clazz)) {
            return objectInput -> {
                UnrealRuntimeContext context = objectInput.getContext();
                int objRef = 0;
				try {
					objRef = objectInput.readCompactInt();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                @SuppressWarnings("rawtypes")
				UnrealPackage.Entry entry = context.getUnrealPackage().objectReference(objRef);
                try {
					return getOrCreateObject(entry);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return objectInput;
            };
        }
        return super.createInstantiator(clazz);
    }

    @SuppressWarnings("deprecation")
	public Object getOrCreateObject(@SuppressWarnings("rawtypes") UnrealPackage.Entry packageLocalEntry) throws IOException {
        if (packageLocalEntry == null) {
            return null;
        }

        String key = keyFor(packageLocalEntry);
        if (!objects.containsKey(key)) {
            log.finest(() -> String.format("Loading %s", packageLocalEntry));

            UnrealPackage.ExportEntry entry;
            try {
                entry = resolveExportEntry(packageLocalEntry).orElse(null);
                if (entry != null) {
                    Class<? extends Object> clazz = getClass(entry.getFullClassName());
                    Object obj = clazz.newInstance();
                    objects.put(key, obj);
                    Runnable load = () -> {
						try {
							load(obj, entry);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
                    if (LOAD_THREAD_NAME.equals(Thread.currentThread().getName())) {
                        load.run();
                    } else {
                        executorService.submit(load).get();
                    }
                } else {
                    create(packageLocalEntry.getObjectFullName(), packageLocalEntry.getFullClassName());
                }
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }
        return objects.get(key);
    }

    private String keyFor(@SuppressWarnings("rawtypes") UnrealPackage.Entry entry) {
        return (entry.getObjectFullName() + "_" + entry.getFullClassName()).toLowerCase();
    }

    private void create(String objName, String objClass) {
        log.fine(() -> String.format("Create dummy %s[%s]", objName, objClass));

        @SuppressWarnings("rawtypes")
		UnrealPackage.Entry entry = new UnrealPackage.Entry(null, 0, 0, 0) {
            @Override
            public String getObjectFullName() {
                return objName;
            }

            @Override
            public String getFullClassName() {
                return objClass;
            }

            @Override
            public int getObjectReference() {
                return 0;
            }

            @SuppressWarnings("rawtypes")
			@Override
            public List getTable() {
                return null;
            }
        };
        objects.put(keyFor(entry), new org.l2explorer.unreal.core.Class() {
            @Override
            public String getFullName() {
                return objName;
            }

            @Override
            public String getClassFullName() {
                return objClass;
            }
        });
    }

    /**
     * @throws UnrealException if not found
     */
    public Object getOrCreateObject(String objName, Predicate<String> objClass) throws IOException, UnrealException {
        return getOrCreateObject(getExportEntry(objName, objClass)
                .orElseThrow(() -> new UnrealException("Entry " + objName + " not found")));
    }

    public Optional<UnrealPackage.ExportEntry> resolveExportEntry(@SuppressWarnings("rawtypes") UnrealPackage.Entry entry) throws IOException {
        if (entry == null) {
            return Optional.empty();
        }

        return getExportEntry(entry.getObjectFullName(), clazz -> clazz.equalsIgnoreCase(entry.getFullClassName()));
    }

    private Optional<UnrealPackage.ExportEntry> getExportEntry(String objName, Predicate<String> objClass) throws IOException {
        return environment.getExportEntry(objName, objClass);
    }

    private Class<? extends Object> getClass(String className) throws IOException {
        Class<?> clazz = null;
        try {
            String javaClassName = unrealClassesPackage + "." + unrealClassNameToJavaClassName(className);
            clazz = Class.forName(javaClassName);
            log.finest(() -> String.format("unreal[%s]->jvm[%s]", className, javaClassName));
            return clazz.asSubclass(Object.class);
        } catch (ClassNotFoundException e) {
            log.finer(() -> String.format("Class %s not implemented in java", className));
        } catch (ClassCastException e) {
            Class<?> clazzLocal = clazz;
            log.warning(() -> String.format("%s is not subclass of %s", clazzLocal, Object.class));
        }

        return getClass(getExportEntry(className, c -> c.equalsIgnoreCase("Core.Class"))
                .map(e -> e.getObjectSuperClass().getObjectFullName())
                .orElse("Core.Object"));
//        Object object = getOrCreateObject(className, c -> c.equalsIgnoreCase("Core.Class"));
//        return getClass(object.entry.getObjectSuperClass().getObjectFullName());
    }

    private String unrealClassNameToJavaClassName(String className) {
        String[] path = className.split("\\.");
        return String.format("%s.%s", path[0].toLowerCase(), path[1]);
    }

    @Override
    protected <T> void serializer(@SuppressWarnings("rawtypes") Class type, Function<T, java.lang.Object> getter, @SuppressWarnings("rawtypes") BiConsumer<T, Supplier> setter, Function<Class<? extends Annotation>, Annotation> getAnnotation, List<BiConsumer<T, ObjectInput<UnrealRuntimeContext>>> read, List<BiConsumer<T, ObjectOutput<UnrealRuntimeContext>>> write) {
        if (type == String.class &&
                Objects.nonNull(getAnnotation.apply(NameRef.class))) {
            read.add((object, dataInput) ->
                    setter.accept(object, () ->
                            {
								try {
									return dataInput.getContext().getUnrealPackage().nameReference(dataInput.readCompactInt());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return object;
							}));
            write.add((object, dataOutput) -> {
				try {
					dataOutput.writeCompactInt(dataOutput.getContext().getUnrealPackage().nameReference((String) getter.apply(object)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
        } else if (Object.class.isAssignableFrom(type) &&
                Objects.nonNull(getAnnotation.apply(ObjectRef.class))) {
            read.add((object, dataInput) -> setter.accept(object, () ->
                    {
						try {
							return getOrCreateObject(dataInput.getContext().getUnrealPackage().objectReference(dataInput.readCompactInt()));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return object;
					}));
            write.add((object, dataOutput) -> {
                Object obj = (Object) getter.apply(object);
                try {
					dataOutput.writeCompactInt(obj == null || obj.getEntry() == null ? 0 : dataOutput.getContext().getUnrealPackage().objectReferenceByName(obj.getEntry().getObjectFullName(), c -> c.equalsIgnoreCase(obj.getEntry().getFullClassName())));
				} catch (IOException e) {
					e.printStackTrace();
				}
            });
        } else if (type.isArray() &&
                type.getComponentType() == Token.class &&
                Objects.nonNull(getAnnotation.apply(Bytecode.class))) {
            read.add((object, dataInput) -> {
                BytecodeContext context = new BytecodeContext(dataInput.getContext());
                SerializerFactory<BytecodeContext> serializerFactory = new TokenSerializerFactory();
                ObjectInput<BytecodeContext> input = ObjectInput.objectInput(dataInput, serializerFactory, context);
                int size = 0;
				try {
					size = input.readInt();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                int readSize = 0;
                List<Token> tokens = new ArrayList<>();
                while (readSize < size) {
                    Token token = null;
					try {
						token = input.readObject(Token.class);
					} catch (IOException e) {
						e.printStackTrace();
					}
                    readSize += token.getSize(context);
                    tokens.add(token);
                }
                setter.accept(object, () -> tokens.toArray(new Token[0]));
            });
            write.add((object, dataOutput) -> {
                BytecodeContext context = new BytecodeContext(dataOutput.getContext());
                SerializerFactory<BytecodeContext> serializerFactory = new TokenSerializerFactory();
                ObjectOutput<BytecodeContext> output = ObjectOutput.objectOutput(dataOutput, serializerFactory, context);
                Token[] array = (Token[]) getter.apply(object);
                try {
					output.writeInt(array.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
                for (Token token: array) {
                    try {
						output.write(token);
					} catch (IOException e) {
						e.printStackTrace();
					}
                }
            });
        } else {
            super.serializer(type, getter, setter, getAnnotation, read, write);
        }
    }

    private void load(Object obj, UnrealPackage.ExportEntry entry) throws IOException {
        ObjectInput<UnrealRuntimeContext> input = new ObjectInputStream<UnrealRuntimeContext>(
                new ByteArrayInputStream(entry.getObjectRawDataExternally()),
                entry.getUnrealPackage().getFile().getCharset(),
                entry.getOffset(),
                this,
                new UnrealRuntimeContext(entry, this)
        ) {
            @Override
            public java.lang.Object readObject(@SuppressWarnings("rawtypes") Class clazz) throws IOException {
                if (getSerializerFactory() == null) {
                    throw new IllegalStateException("SerializerFactory is null");
                }

                @SuppressWarnings("rawtypes")
				Serializer serializer = getSerializerFactory().forClass(clazz);
                java.lang.Object obj = serializer.instantiate(this);
                if (!(obj instanceof org.l2explorer.unreal.core.Object)) {
                    throw new RuntimeException("USE input.getSerializerFactory().forClass(class).readObject(object, input);");
                }
                return obj;
            }
        };

        @SuppressWarnings("rawtypes")
		Serializer serializer = forClass(obj.getClass());
        serializer.readObject(obj, input);

        if (obj instanceof org.l2explorer.unreal.core.Function) {
        	org.l2explorer.unreal.core.Function func = (org.l2explorer.unreal.core.Function) obj;
            if (func.getNativeIndex() > 0) {
                nativeFunctions.put(func.getNativeIndex(), func);
            }
        }

        if (entry.getFullClassName().equalsIgnoreCase("Core.Class")) {
            Runnable loadProps = () -> {
                obj.getProperties().addAll(PropertiesUtil.readProperties(input, obj.getFullName()));
                loaded.add(entry.getObjectFullName());
                log.finest(() -> entry.getObjectFullName() + " properties loaded");
            };
            if (entry.getObjectSuperClass() != null) {
                String superClass = entry.getObjectSuperClass().getObjectFullName();

                if (loaded.contains(superClass)) {
                    loadProps.run();
                } else {
                    AtomicInteger ai = new AtomicInteger(0);
                    Consumer<InvalidationListener> c = il -> {
                        if (loaded.contains(superClass) && ai.getAndIncrement() == 0) {
                            loaded.removeListener(il);
                            loadProps.run();
                        }
                    };
                    InvalidationListener il = new InvalidationListener() {
                        @Override
                        public void invalidated(org.l2explorer.unreal.util.Observable observable) {
                            c.accept(this);
                        }
                    };
                    loaded.addListener(il);
                    c.accept(il);
                }
            } else {
                loadProps.run();
            }
        }

        if (!(obj instanceof org.l2explorer.unreal.core.Class)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    baos.write(input.readUnsignedByte());
                }
            } catch (IOException ignore) {
            }

            obj.setUnreadBytes(baos.toByteArray());
            if (obj.getUnreadBytes().length > 0) {
                log.warning(() -> obj + " " + obj.getUnreadBytes().length + " bytes ignored");
            }
        }

        log.finest(() -> entry.getObjectFullName() + " loaded");
    }

    public Optional<Struct> getStruct(String name) {
        try {
            return Optional.of((Struct) getOrCreateObject(name, IS_STRUCT));
        } catch (Exception e) {
            log.log(Level.WARNING, e, () -> "");
            return Optional.empty();
        }
    }

    /**
     * @return superClass or null
     * @throws IOException 
     */
    public String getSuperClass(String clazz) throws IOException {
        Optional<UnrealPackage.ExportEntry> opt = getExportEntry(clazz, IS_STRUCT);
        if (opt.isPresent()) {
            return opt.get().getObjectSuperClass() != null ?
                    opt.get().getObjectSuperClass().getObjectFullName() : null;
        } else {
            try {
                Class<?> javaClass = Class.forName(unrealClassesPackage + "." + unrealClassNameToJavaClassName(clazz));
                Class<?> javaSuperClass = javaClass.getSuperclass();
                String javaSuperClassName = javaSuperClass.getName();
                if (javaSuperClassName.contains(unrealClassesPackage)) {
                    javaSuperClassName = javaSuperClassName.substring(unrealClassesPackage.length() + 1);
                    return javaSuperClassName.substring(0, 1).toUpperCase() + javaSuperClassName.substring(1);
                }
            } catch (ClassNotFoundException ignore) {
            }
        }
        return null;
    }

    private final Map<String, Boolean> isSubclassCache = new HashMap<>();

    public boolean isSubclass(String parent, String child) throws IOException {
        if (parent.equalsIgnoreCase(child)) {
            return true;
        }

        String k = parent + "@" + child;
        synchronized (isSubclassCache) {
            if (!isSubclassCache.containsKey(k)) {
                child = getSuperClass(child);
                isSubclassCache.put(k, child != null && isSubclass(parent, child));
            }
            return isSubclassCache.get(k);
        }
    }

    public <T extends Struct> List<T> getStructTree(T struct) throws IOException {
        List<T> list = new ArrayList<>();

        for (UnrealPackage.ExportEntry entry = struct.getEntry(); entry != null; entry = resolveExportEntry(entry.getObjectSuperClass()).orElse(null)) {
            list.add((T) getOrCreateObject(entry));
        }

        Collections.reverse(list);

        return list;
    }

    public Optional<org.l2explorer.unreal.core.Function> getNativeFunction(int index) {
        return Optional.ofNullable(nativeFunctions.get(index));
    }

    private static class EnvironmentWrapper implements Env {
        private final Env environment;
        private final Map<String, UnrealPackage> adds = new HashMap<>();

        public EnvironmentWrapper(Env environment) {
            this.environment = environment;
        }

        @Override
        public File getStartDir() {
            return environment.getStartDir();
        }

        @Override
        public List<String> getPaths() {
            return environment.getPaths();
        }

        @Override
        public Optional<UnrealPackage> getPackage(File f) {
            return environment.getPackage(f);
        }

        @Override
        public void markInvalid(String pckg) {
            environment.markInvalid(pckg);
        }

        @Override
        public Stream<UnrealPackage> listPackages(String name) throws IOException {
            return appendCustomPackage(environment.listPackages(name), name);
        }

        @Override
        public Stream<File> listFiles() {
            return environment.listFiles();
        }

        @Override
        public Stream<File> getPackage(String name) {
            return environment.getPackage(name);
        }

        @Override
        public Optional<UnrealPackage.ExportEntry> getExportEntry(String fullName, Predicate<String> fullClassName) throws IOException {
            String[] path = fullName.split("\\.");

            Optional<UnrealPackage.ExportEntry> entryOptional = appendCustomPackage(Stream.empty(), path[0])
                    .map(UnrealPackage::getExportTable)
                    .flatMap(Collection::stream)
                    .filter(e -> e.getObjectFullName().equalsIgnoreCase(fullName))
                    .filter(e -> fullClassName.test(e.getFullClassName()))
                    .findAny();
            if (entryOptional.isPresent()) {
                return entryOptional;
            }

            return environment.getExportEntry(fullName, fullClassName);
        }

        private Stream<UnrealPackage> appendCustomPackage(Stream<UnrealPackage> stream, String name) throws IOException {
            if (!name.equalsIgnoreCase("Engine") && !name.equalsIgnoreCase("Core")) {
                return stream;
            }

            name = canonizeName(name);
            if (!adds.containsKey(name)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (InputStream is = getClass().getResourceAsStream("/" + name + ".u")) {
                    byte[] buf = new byte[1024];
                    int r;
                    while ((r = is.read(buf)) != -1) {
                        baos.write(buf, 0, r);
                    }
                } catch (IOException e) {
                    throw new UnrealException(e);
                }

                try (UnrealPackage up = new UnrealPackage(new RandomAccessMemory(name, baos.toByteArray(), UnrealPackage.getDefaultCharset()))) {
                    adds.put(name, up);
                }
            }

            return Stream.concat(Stream.of(adds.get(name)), stream);
        }

        private String canonizeName(String name) {
            return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }
    }
}
