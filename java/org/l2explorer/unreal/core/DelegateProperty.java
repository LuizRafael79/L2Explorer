package org.l2explorer.unreal.core;

import org.l2explorer.unreal.annotation.ObjectRef;

/**
 * Represents a Delegate Property in the Unreal Engine.
 * <p>Delegates are used to store references to functions that can be 
 * invoked as events or callbacks.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class DelegateProperty extends Property {
    /**
     * The function (event) that this delegate points to.
     */
    @ObjectRef
    private Function event;

    public DelegateProperty() {
    }

    /**
     * Gets the function assigned to this delegate.
     *
     * @return The Function object.
     */
    public Function getEvent() {
        return event;
    }

    /**
     * Sets the function assigned to this delegate.
     *
     * @param event The Function object to set.
     */
    public void setEvent(Function event) {
        this.event = event;
    }
}