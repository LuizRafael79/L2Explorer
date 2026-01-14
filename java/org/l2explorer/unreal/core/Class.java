package org.l2explorer.unreal.core;

import org.l2explorer.io.ObjectOutput;
import org.l2explorer.io.annotation.Custom;
import org.l2explorer.io.annotation.WriteMethod;
import org.l2explorer.unreal.UUIDSerializer;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.NameRef;
import org.l2explorer.unreal.annotation.ObjectRef;
import org.l2explorer.unreal.properties.PropertiesUtil;

import java.io.IOException;
import java.util.UUID;

import static org.l2explorer.io.ByteUtil.uuidToBytes;

/**
 * Represents a Class definition in the Unreal Engine (inherits from State).
 * <p>A Class defines the properties, behavior (functions), and dependencies 
 * of Unreal objects. It includes flags, a unique UUID, and category information.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class Class extends State {
    private int classFlags;

    @Custom(UUIDSerializer.class)
    private UUID classUuid;

    private Dependency[] dependencies;

    @NameRef
    private String[] packageImports;

    @ObjectRef
    private Object classWithin;

    @NameRef
    private String classConfigName;

    @NameRef
    private String[] hideCategories;

    /**
     * Inner class representing a Class dependency.
     */
    public static class Dependency {
        @ObjectRef
        private Class clazz;
        private int deep;
        private int scriptTextCRC;

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }

        public int getDeep() {
            return deep;
        }

        public void setDeep(int deep) {
            this.deep = deep;
        }

        public int getScriptTextCRC() {
            return scriptTextCRC;
        }

        public void setScriptTextCRC(int scriptTextCRC) {
            this.scriptTextCRC = scriptTextCRC;
        }
    }

    /**
     * Writes the Class metadata to the output stream.
     *
     * @param output The output context for the Unreal runtime.
     */
    @WriteMethod
    public final void writeClass(ObjectOutput<UnrealRuntimeContext> output) throws IOException {
        output.writeInt(classFlags);
        output.writeBytes(uuidToBytes(classUuid));

        // 1. Dependencies
        Dependency[] deps = (dependencies != null) ? dependencies : new Dependency[0];
        output.writeCompactInt(deps.length);
        for (Dependency dep : deps) {
            output.write(dep);
        }

        // 2. Package Imports
        String[] pkgImports = (packageImports != null) ? packageImports : new String[0];
        output.writeCompactInt(pkgImports.length);
        for (String name : pkgImports) {
            output.writeCompactInt(output.getContext().getUnrealPackage().nameReference(name));
        }

        // 3. Class Within - Fixed Inference Error
        int withinRef = 0;
        if (this.classWithin != null) {
            // Accessing entry directly from the Object class
            var entry = this.classWithin.getEntry();
            withinRef = output.getContext().getUnrealPackage().objectReferenceByName(
                    entry.getObjectFullName(), 
                    c -> c.equalsIgnoreCase(entry.getFullClassName())
            );
        }
        output.writeCompactInt(withinRef);

        // 4. Config Name
        output.writeCompactInt(output.getContext().getUnrealPackage().nameReference(classConfigName));

        // 5. Hide Categories - Fixed Stream IOException
        String[] hidden = (this.hideCategories != null) ? this.hideCategories : new String[0];
        output.writeCompactInt(hidden.length);
        for (String cat : hidden) {
            int nameIdx = output.getContext().getUnrealPackage().nameReference(cat);
            output.writeCompactInt(nameIdx);
        }

        // 6. Properties - Using the field directly since getProperties() is missing
        PropertiesUtil.writeProperties(output, this.properties);
    }

    // Getters and Setters
    public int getClassFlags() {
        return classFlags;
    }

    public void setClassFlags(int classFlags) {
        this.classFlags = classFlags;
    }

    public UUID getClassUuid() {
        return classUuid;
    }

    public void setClassUuid(UUID classUuid) {
        this.classUuid = classUuid;
    }

    public Dependency[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(Dependency[] dependencies) {
        this.dependencies = dependencies;
    }

    public String[] getPackageImports() {
        return packageImports;
    }

    public void setPackageImports(String[] packageImports) {
        this.packageImports = packageImports;
    }

    public Object getClassWithin() {
        return classWithin;
    }

    public void setClassWithin(Object classWithin) {
        this.classWithin = classWithin;
    }

    public String getClassConfigName() {
        return classConfigName;
    }

    public void setClassConfigName(String classConfigName) {
        this.classConfigName = classConfigName;
    }

    public String[] getHideCategories() {
        return hideCategories;
    }

    public void setHideCategories(String[] hideCategories) {
        this.hideCategories = hideCategories;
    }
}