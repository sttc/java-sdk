/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import java.security.SecureRandom;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link RtCounters}.
 * @since 0.1
 */
final class RtCountersITCase {

    /**
     * Random.
     */
    private static final Random RANDOM = new SecureRandom();

    /**
     * Region rule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    public final transient SttcRule srule = new SttcRule();

    @Test
    void incrementsAndSets() throws Exception {
        final Counters counters = this.srule.get().counters();
        final String name = String.format(
            "test-%s", RtCountersITCase.RANDOM.nextInt(Integer.MAX_VALUE)
        );
        final Counter counter = counters.create(name);
        try {
            counter.set(0L);
            MatcherAssert.assertThat(
                counter.incrementAndGet(5L),
                Matchers.equalTo(5L)
            );
        } finally {
            counters.delete(name);
        }
    }

}
