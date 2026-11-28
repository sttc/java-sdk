
/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.mock;

import co.stateful.Counter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkCounter}.
 *
 * @since 0.1
 */
final class MkCounterTest {

    @Test
    void updatesCounterOnSameInstance() {
        final Counter counter = new MkCounter();

        counter.set(10L);

        MatcherAssert.assertThat(
            counter.incrementAndGet(5L),
            Matchers.equalTo(15L)
        );
        MatcherAssert.assertThat(
            counter.incrementAndGet(0L),
            Matchers.equalTo(15L)
        );
        MatcherAssert.assertThat(
            counter.incrementAndGet(-3L),
            Matchers.equalTo(12L)
        );
    }
}
