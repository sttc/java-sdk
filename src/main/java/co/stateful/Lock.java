/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Lock.
 *
 * @since 0.3
 */
@Immutable
public interface Lock {

    /**
     * Its name.
     * @return Name of this lock
     * @since 0.8
     */
    String name();

    /**
     * Read label (or empty text if lock doesn't exist).
     * @return Label of this lock (if the lock exists), or empty string
     * @throws IOException If any problem inside
     * @since 0.15
     */
    String label() throws IOException;

    /**
     * Lock with label.
     * @param label Label to attach
     * @return TRUE if success, FALSE otherwise
     * @throws IOException If any problem inside
     * @since 0.11
     */
    boolean lock(String label) throws IOException;

    /**
     * Unlock, if label matches.
     * @param label Label to attach
     * @return TRUE if success, FALSE otherwise (label doesn't match)
     * @throws IOException If any problem inside
     * @since 0.11
     */
    boolean unlock(String label) throws IOException;

}
