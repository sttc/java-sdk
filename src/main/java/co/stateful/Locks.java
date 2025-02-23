/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Locks.
 *
 * @since 0.2
 */
@Immutable
public interface Locks {

    /**
     * Does it exist?
     * @param name Name of lock
     * @return TRUE if the lock exists
     * @throws IOException If fails
     * @since 0.10
     */
    boolean exists(String name) throws IOException;

    /**
     * Get one lock by name.
     * @param name Name of lock
     * @return Lock
     * @throws IOException If fails
     */
    Lock get(String name) throws IOException;

}
