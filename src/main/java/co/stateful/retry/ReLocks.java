/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.retry;

import co.stateful.Lock;
import co.stateful.Locks;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.RetryOnFailure;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Retriable locks.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(includeFieldNames = false)
@EqualsAndHashCode(of = "origin")
public final class ReLocks implements Locks {

    /**
     * Original object.
     */
    private final transient Locks origin;

    /**
     * Ctor.
     * @param orgn Original object
     */
    public ReLocks(final Locks orgn) {
        this.origin = orgn;
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public boolean exists(final String name) throws IOException {
        return this.origin.exists(name);
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public Lock get(final String name) throws IOException {
        return new ReLock(this.origin.get(name));
    }
}
