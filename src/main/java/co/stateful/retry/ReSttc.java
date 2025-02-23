/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.retry;

import co.stateful.Counters;
import co.stateful.Locks;
import co.stateful.Sttc;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.RetryOnFailure;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Retriable Sttc.
 *
 * <p>Just wrap your original {@link Sttc} object with this
 * decorator and all requests to stateful.co server will be
 * retried a few times before giving up and throwing a runtime
 * exception. It is highly recommended to use this decorator
 * in production environment. The Internet is not a stable environment,
 * and connection failures is a regular event.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(includeFieldNames = false)
@EqualsAndHashCode(of = "origin")
public final class ReSttc implements Sttc {

    /**
     * Original object.
     */
    private final transient Sttc origin;

    /**
     * Ctor.
     * @param orgn Original object
     */
    public ReSttc(final Sttc orgn) {
        this.origin = orgn;
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public Counters counters() throws IOException {
        return new ReCounters(this.origin.counters());
    }

    @Override
    @RetryOnFailure
        (
            verbose = false, attempts = 20,
            delay = 20, unit = TimeUnit.SECONDS
        )
    public Locks locks() throws IOException {
        return new ReLocks(this.origin.locks());
    }
}
