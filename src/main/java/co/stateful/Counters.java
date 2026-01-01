/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Counters.
 *
 * @since 0.1
 */
@Immutable
public interface Counters {

    /**
     * Get list of them all.
     * @return List of counter names
     * @throws IOException If some I/O problem
     */
    Iterable<String> names() throws IOException;

    /**
     * Create a counter.
     * @param name Name of it
     * @return Counter
     * @throws IOException If some I/O problem
     */
    Counter create(String name) throws IOException;

    /**
     * Delete a counter.
     * @param name Name of it
     * @throws IOException If some I/O problem
     */
    void delete(String name) throws IOException;

    /**
     * Get one counter by name.
     * @param name Name of it
     * @return Counter
     * @throws IOException If some I/O problem
     */
    Counter get(String name) throws IOException;

}
