package org.l2explorer.unreal.core;

import org.l2explorer.unreal.annotation.ObjectRef;

/**
 * Represents a Class Property in the Unreal Engine.
 * <p>This property type is used to store a reference to a Class object,
 * optionally restricted to a specific meta-class.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class ClassProperty extends ObjectProperty {
    /**
     * The meta-class that this property is restricted to.
     */
    @ObjectRef
    private Class clazz;

    public ClassProperty() {
    }

    /**
     * Gets the meta-class restriction.
     *
     * @return The Class object reference.
     */
    public Class getClazz() {
        return clazz;
    }

    /**
     * Sets the meta-class restriction.
     *
     * @param clazz The Class object reference to set.
     */
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}