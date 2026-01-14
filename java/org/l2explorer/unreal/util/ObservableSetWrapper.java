/*
 * Copyright (c) 2021 acmi
 * Copyright (c) 2026 Galagard/L2Explorer
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
package org.l2explorer.unreal.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

/**
 * Implementation of an observable wrapper for a {@link Set}.
 * <p>Notifies registered {@link InvalidationListener}s whenever the 
 * backing set is modified through any of the standard Set methods.</p>
 *
 * @param <E> the type of elements maintained by this set
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class ObservableSetWrapper<E> implements ObservableSet<E> {
    private final Set<E> backingSet;
    private final Collection<InvalidationListener> listeners = new CopyOnWriteArraySet<>();

    /**
     * Constructs a new ObservableSetWrapper.
     *
     * @param backingSet The set to be wrapped and observed.
     * @throws NullPointerException if backingSet is null.
     */
    public ObservableSetWrapper(Set<E> backingSet) {
        if (backingSet == null) {
            throw new NullPointerException("backingSet cannot be null");
        }
        this.backingSet = backingSet;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners that the set has been invalidated/modified.
     */
    private void callObservers() {
        for (InvalidationListener listener : listeners) {
            listener.invalidated(this);
        }
    }

    @Override
    public int size() {
        return backingSet.size();
    }

    @Override
    public boolean isEmpty() {
        return backingSet.isEmpty();
    }

    @Override
    public boolean contains(java.lang.Object o) {
        return backingSet.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<E> backingIt = backingSet.iterator();

            @Override
            public boolean hasNext() {
                return backingIt.hasNext();
            }

            @Override
            public E next() {
                return backingIt.next();
            }

            @Override
            public void remove() {
                backingIt.remove();
                callObservers();
            }
        };
    }

    @Override
    public java.lang.Object[] toArray() {
        return backingSet.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return backingSet.toArray(a);
    }

    @Override
    public boolean add(E e) {
        boolean ret = backingSet.add(e);
        if (ret) {
            callObservers();
        }
        return ret;
    }

    @Override
    public boolean remove(java.lang.Object o) {
        boolean ret = backingSet.remove(o);
        if (ret) {
            callObservers();
        }
        return ret;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return backingSet.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean ret = backingSet.addAll(c);
        if (ret) {
            callObservers();
        }
        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean ret = backingSet.retainAll(c);
        if (ret) {
            callObservers();
        }
        return ret;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean ret = backingSet.removeAll(c);
        if (ret) {
            callObservers();
        }
        return ret;
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        boolean ret = backingSet.removeIf(filter);
        if (ret) {
            callObservers();
        }
        return ret;
    }

    @Override
    public void clear() {
        if (backingSet.isEmpty()) {
            return;
        }
        backingSet.clear();
        callObservers();
    }

    @Override
    public Spliterator<E> spliterator() {
        return backingSet.spliterator();
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        return backingSet.equals(o);
    }

    @Override
    public int hashCode() {
        return backingSet.hashCode();
    }

    @Override
    public String toString() {
        return backingSet.toString();
    }
}