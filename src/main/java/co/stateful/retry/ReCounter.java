/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.retry;

import co.stateful.Counter;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.RetryOnFailure;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Retriable counter.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(includeFieldNames = false)
@EqualsAndHashCode(of = "origin")
public final class ReCounter implements Counter {

    /**
     * Original object.
     */
    private final transient Counter origin;

    /**
     * Ctor.
     * @param orgn Original object
     */
    public ReCounter(final Counter orgn) {
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
    public void set(final long value) throws IOException {
        this.origin.set(value);
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public long incrementAndGet(final long delta) throws IOException {
        return this.origin.incrementAndGet(delta);
    }
}
