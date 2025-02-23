/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.mock;

import co.stateful.Lock;
import co.stateful.Locks;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock locks.
 *
 * @since 0.2
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode
final class MkLocks implements Locks {

    @Override
    public boolean exists(final String name) {
        return false;
    }

    @Override
    public Lock get(final String name) {
        return new MkLock();
    }

}
