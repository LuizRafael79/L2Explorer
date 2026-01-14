package org.l2explorer.unreal.core;

import org.l2explorer.io.ObjectInput;
import org.l2explorer.io.ObjectOutput;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.io.annotation.ReadMethod;
import org.l2explorer.io.annotation.WriteMethod;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.properties.L2Property;
import org.l2explorer.unreal.properties.PropertiesUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.l2explorer.io.UnrealPackage.ObjectFlag.HasStack;
import static org.l2explorer.io.UnrealPackage.ObjectFlag.getFlags;

/**
 * Base class for all Unreal objects (Opcode/Object level).
 * <p>Handles the core serialization of objects, including StateFrames for 
 * objects with stacks and property lists for non-class assets.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class Object {
    /**
     * Reference to the export entry in the Unreal package.
     */
    protected transient UnrealPackage.ExportEntry entry;

    /**
     * Execution state frame for objects with the 'HasStack' flag.
     */
    protected StateFrame stateFrame;

    /**
     * List of Unreal properties associated with this object.
     */
    protected final List<L2Property> properties = new ArrayList<>();

    /**
     * Buffer for unread data during transition/debugging.
     */
    protected transient byte[] unreadBytes;

    /**
     * Gets the raw bytes that were not parsed by the current object definition.
     * * @return Byte array of unread data.
     */
    public byte[] getUnreadBytes() {
        return unreadBytes;
    }

    /**
     * Sets the raw bytes for data ignored during parsing.
     * * @param unreadBytes Byte array to store.
     */
    public void setUnreadBytes(byte[] unreadBytes) {
        this.unreadBytes = unreadBytes;
    }

    public Object() {
    }

    /**
     * Standard read method for Unreal objects.
     *
     * @param input The object input stream.
     * @throws IOException 
     */
    @ReadMethod
    public final void readObject(ObjectInput<UnrealRuntimeContext> input) throws IOException {
        this.entry = input.getContext().getEntry();

        if (getFlags(this.entry.getObjectFlags()).contains(HasStack)) {
            this.stateFrame = new StateFrame();
            input.getSerializerFactory().forClass(StateFrame.class).readObject(this.stateFrame, input);
        }

        if (!(this instanceof Class)) {
            this.properties.addAll(PropertiesUtil.readProperties(input, this.entry.getFullClassName()));
        }
    }

    /**
     * Standard write method for Unreal objects.
     *
     * @param output The object output stream.
     * @throws IOException 
     */
    @WriteMethod
    public final void writeObject(ObjectOutput<UnrealRuntimeContext> output) throws IOException {
        if (getFlags(output.getContext().getEntry().getObjectFlags()).contains(HasStack)) {
            output.getSerializerFactory().forClass(StateFrame.class).writeObject(this.stateFrame, output);
        }

        if (!(this instanceof Class)) {
            PropertiesUtil.writeProperties(output, this.properties);
        }
    }

    /**
     * Returns the export entry associated with this object.
     * Needed for metadata resolution in Class.java.
     *
     * @return The ExportEntry.
     */
    public UnrealPackage.ExportEntry getEntry() {
        return entry;
    }

    /**
     * Returns the full name of the object as defined in the package.
     *
     * @return String representing the full path (e.g., 'Package.Group.Object').
     */
    public String getFullName() {
        return entry != null ? entry.getObjectFullName() : "None";
    }

    /**
     * Returns the class name of the object.
     *
     * @return String representing the class (e.g., 'StaticMesh', 'Texture').
     */
    public String getClassFullName() {
        return entry != null ? entry.getFullClassName() : "None";
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getFullName(), getClassFullName());
    }

    public List<L2Property> getProperties() {
        return properties;
    }

    // --- Inner Classes for Unreal Types ---

    /**
     * Execution state information for objects with a state stack.
     */
    public static class StateFrame {
        @Compact
        public int node;
        @Compact
        public int stateNode;
        public long probeMask;
        public int latentAction;
        @Compact
        public int offset;
    }

    

    public static class Vector {
        public float x, y, z;
    }

    public static class Sphere extends Vector {
        public float r;
    }

    public static class Plane extends Vector {
        public float w;
    }

    public static class Matrix {
        public Plane xPlane;
        public Plane yPlane;
        public Plane zPlane;
        public Plane wPlane;
    }

    public static class Scale {
        public Vector scale;
        public float sheerRate;
        public byte sheerAxis;
    }

    public static class Box {
        public Vector min;
        public Vector max;
        public byte isValid;
    }

    public static class Color {
        public byte r, g, b, a;
    }
}