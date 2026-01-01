/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.retry;

import co.stateful.Lock;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.RetryOnFailure;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Retriable lock.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(includeFieldNames = false)
@EqualsAndHashCode(of = "origin")
public final class ReLock implements Lock {

    /**
     * Original object.
     */
    private final transient Lock origin;

    /**
     * Ctor.
     * @param orgn Original object
     */
    public ReLock(final Lock orgn) {
        this.origin = orgn;
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public String name() {
        return this.origin.name();
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public String label() throws IOException {
        return this.origin.label();
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public boolean lock(final String label) throws IOException {
        return this.origin.lock(label);
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public boolean unlock(final String label) throws IOException {
        return this.origin.unlock(label);
    }
}
