/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.cached;

import co.stateful.Lock;
import co.stateful.Locks;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Cached locks.
 *
 * @since 0.7
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(includeFieldNames = false)
@EqualsAndHashCode(of = "origin")
public final class CdLocks implements Locks {

    /**
     * Original object.
     */
    private final transient Locks origin;

    /**
     * Ctor.
     * @param orgn Original object
     */
    public CdLocks(final Locks orgn) {
        this.origin = orgn;
    }

    @Override
    public boolean exists(final String name) throws IOException {
        return this.origin.exists(name);
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public Lock get(final String name) throws IOException {
        return this.origin.get(name);
    }
}
