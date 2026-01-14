package org.l2explorer.utils;

import javax.swing.JComponent;

/**
 * Interface adaptada para Swing.
 * T agora estende JComponent em vez de Region do JavaFX.
 */
public interface ComponentFactory<T extends JComponent> {
    T create();

    void initProperties(T component);

    default T getComponent() {
        T component = create();
        initProperties(component);
        return component;
    }
}