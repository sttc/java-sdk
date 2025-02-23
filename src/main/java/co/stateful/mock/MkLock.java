/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.mock;

import co.stateful.Lock;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock lock.
 *
 * @since 0.3
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode
final class MkLock implements Lock {

    @Override
    public String name() {
        return this.getClass().getName();
    }

    @Override
    public String label() {
        return "label";
    }

    @Override
    public boolean lock(final String label) {
        return true;
    }

    @Override
    public boolean unlock(final String label) {
        return true;
    }
}
