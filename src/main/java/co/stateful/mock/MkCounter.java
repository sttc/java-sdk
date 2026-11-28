
/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.mock;

import co.stateful.Counter;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.util.concurrent.atomic.AtomicLong;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock counter.
 *
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode
final class MkCounter implements Counter {

    private final AtomicLong value = new AtomicLong();

    @Override
    public String name() {
        return this.getClass().getName();
    }

    @Override
    public void set(final long value) {
        this.value.set(value);
    }

    @Override
    public long incrementAndGet(final long delta) {
        return this.value.addAndGet(delta);
    }
}

