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
package org.l2explorer.unreal.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.l2explorer.io.annotation.UShort;

/**
 * Represents a State in the Unreal Engine.
 * <p>States allow objects to have different behavior based on their current 
 * condition. They contain masks for ignored events and offsets for 
 * execution labels.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class State extends Struct {
    /**
     * Bitmask of events (probes) enabled in this state.
     */
    private long probeMask;

    /**
     * Bitmask of events to be explicitly ignored.
     */
    private long ignoreMask;

    /**
     * Offset into the label table for the state's entry point.
     */
    @UShort
    private int labelTableOffset;

    /**
     * Numerical flags defining state behavior.
     */
    private int stateFlags;

    public State() {
    }

    // --- Getters and Setters ---

    public long getProbeMask() {
        return probeMask;
    }

    public void setProbeMask(long probeMask) {
        this.probeMask = probeMask;
    }

    public long getIgnoreMask() {
        return ignoreMask;
    }

    public void setIgnoreMask(long ignoreMask) {
        this.ignoreMask = ignoreMask;
    }

    public int getLabelTableOffset() {
        return labelTableOffset;
    }

    public void setLabelTableOffset(int labelTableOffset) {
        this.labelTableOffset = labelTableOffset;
    }

    public int getStateFlags() {
        return stateFlags;
    }

    public void setStateFlags(int stateFlags) {
        this.stateFlags = stateFlags;
    }

    /**
     * Enum representing the possible flags for an Unreal State.
     */
    public enum Flags {
        /**
         * State should be user-selectable in UnrealEd.
         */
        Editable(0x00000001),
        /**
         * State is automatic (the default state).
         */
        Auto(0x00000002),
        /**
         * State executes on the client side.
         */
        Simulated(0x00000004);

        private final int mask;

        Flags(int mask) {
            this.mask = mask;
        }

        public int getMask() {
            return mask;
        }

        /**
         * Returns a collection of Flags present in the given bitmask.
         *
         * @param flags The bitmask to analyze.
         * @return A collection of active Flags.
         */
        public static Collection<Flags> getFlags(int flags) {
            return Arrays.stream(values())
                    .filter(e -> (e.getMask() & flags) != 0)
                    .collect(Collectors.toList());
        }

        /**
         * Combines multiple Flags into a single integer bitmask.
         *
         * @param flags The flags to combine.
         * @return The resulting bitmask.
         */
        public static int getFlags(Flags... flags) {
            int v = 0;
            for (Flags flag : flags) {
                v |= flag.getMask();
            }
            return v;
        }

        @Override
        public String toString() {
            return "STATE_" + name();
        }
    }
}