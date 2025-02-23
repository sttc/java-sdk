/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Counter.
 *
 * @since 0.1
 */
@Immutable
public interface Counter {

    /**
     * Its name.
     * @return Name of this lock
     * @since 0.8
     */
    String name();

    /**
     * Set specific value.
     * @param value Value to set
     * @throws IOException If some I/O problem
     */
    void set(long value) throws IOException;

    /**
     * Add value to it.
     * @param delta Delta to add (can be zero or negative)
     * @return New value
     * @throws IOException If some I/O problem
     */
    long incrementAndGet(long delta) throws IOException;

}
