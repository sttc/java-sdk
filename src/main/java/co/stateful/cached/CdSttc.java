/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.cached;

import co.stateful.Counters;
import co.stateful.Locks;
import co.stateful.Sttc;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Cached Sttc.
 *
 * @since 0.7
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(includeFieldNames = false)
@EqualsAndHashCode(of = "origin")
public final class CdSttc implements Sttc {

    /**
     * Original object.
     */
    private final transient Sttc origin;

    /**
     * Ctor.
     * @param orgn Original object
     */
    public CdSttc(final Sttc orgn) {
        this.origin = orgn;
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public Counters counters() throws IOException {
        return new CdCounters(this.origin.counters());
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public Locks locks() throws IOException {
        return new CdLocks(this.origin.locks());
    }
}
