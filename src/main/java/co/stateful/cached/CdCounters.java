/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.cached;

import co.stateful.Counter;
import co.stateful.Counters;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Cached counters.
 *
 * @since 0.7
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(includeFieldNames = false)
@EqualsAndHashCode(of = "origin")
public final class CdCounters implements Counters {

    /**
     * Original object.
     */
    private final transient Counters origin;

    /**
     * Ctor.
     * @param orgn Original object
     */
    public CdCounters(final Counters orgn) {
        this.origin = orgn;
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public Iterable<String> names() throws IOException {
        return this.origin.names();
    }

    @Override
    @Cacheable.FlushAfter
    public Counter create(final String name) throws IOException {
        return this.origin.create(name);
    }

    @Override
    @Cacheable.FlushAfter
    public void delete(final String name) throws IOException {
        this.origin.delete(name);
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public Counter get(final String name) throws IOException {
        return this.origin.get(name);
    }
}
