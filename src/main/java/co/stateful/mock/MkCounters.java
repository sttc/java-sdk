/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.mock;

import co.stateful.Counter;
import co.stateful.Counters;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.util.Collections;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock counters.
 *
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode
final class MkCounters implements Counters {

    @Override
    public Iterable<String> names() {
        return Collections.emptyList();
    }

    @Override
    public Counter create(final String name) {
        return new MkCounter();
    }

    @Override
    public void delete(final String name) {
        // done
    }

    @Override
    public Counter get(final String name) {
        return new MkCounter();
    }

}
