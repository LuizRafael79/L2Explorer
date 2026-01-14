package org.l2explorer.unreal.util;

public interface Observable {
    void addListener(InvalidationListener listener);

    void removeListener(InvalidationListener listener);
}
