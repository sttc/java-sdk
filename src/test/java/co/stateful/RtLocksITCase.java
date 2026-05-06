/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import java.security.SecureRandom;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link RtLocks}.
 * @since 0.2
 */
final class RtLocksITCase {

    /**
     * Random.
     */
    private static final Random RANDOM = new SecureRandom();

    @Test
    void locksAndUnlocks() throws Exception {
        MatcherAssert.assertThat(
            new Atomic<>(
                () -> "done",
                SttcRule.fromProperties().get().locks().get(
                    String.format(
                        "lck-%s",
                        RtLocksITCase.RANDOM.nextInt(Integer.MAX_VALUE)
                    )
                )
            ).call(),
            Matchers.equalTo("done")
        );
    }

    @Test
    void locksAndUnlocksInOneThread() throws Exception {
        final Locks locks = SttcRule.fromProperties().get().locks();
        final String name = String.format(
            "test2-%s", RtLocksITCase.RANDOM.nextInt(Integer.MAX_VALUE)
        );
        locks.get(name).lock("test");
        try {
            MatcherAssert.assertThat(
                locks.exists(name),
                Matchers.is(true)
            );
        } finally {
            locks.get(name).unlock("");
        }
    }

    @Test
    void rejectsIncorrectLockName() {
        Assumptions.assumeFalse(System.getProperty("sttc.urn").isEmpty());
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> SttcRule.fromProperties().get().locks()
                .get("invalid name with spaces").lock("test-3")
        );
    }
}
