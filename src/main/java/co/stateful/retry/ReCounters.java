/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.retry;

import co.stateful.Counter;
import co.stateful.Counters;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.RetryOnFailure;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Retriable counters.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(includeFieldNames = false)
@EqualsAndHashCode(of = "origin")
public final class ReCounters implements Counters {

    /**
     * Original object.
     */
    private final transient Counters origin;

    /**
     * Ctor.
     * @param orgn Original object
     */
    public ReCounters(final Counters orgn) {
        this.origin = orgn;
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public Iterable<String> names() throws IOException {
        return this.origin.names();
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public Counter create(final String name) throws IOException {
        return this.origin.create(name);
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public void delete(final String name) throws IOException {
        this.origin.delete(name);
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public Counter get(final String name) throws IOException {
        return new ReCounter(this.origin.get(name));
    }
}
