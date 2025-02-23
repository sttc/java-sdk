/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.aspects.Parallel;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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

    /**
     * Region rule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    public final transient SttcRule srule = new SttcRule();

    @Test
    void locksAndUnlocks() throws Exception {
        final Sttc sttc = this.srule.get();
        final Lock lock = sttc.locks().get(
            String.format(
                "test-%s", RtLocksITCase.RANDOM.nextInt(Integer.MAX_VALUE)
            )
        );
        final AtomicInteger sum = new AtomicInteger();
        final Collection<Integer> deltas = new CopyOnWriteArrayList<>();
        final Callable<Void> atomic = new Atomic<>(
            () -> {
                final int start = sum.get();
                final int delta = RtLocksITCase.RANDOM.nextInt();
                TimeUnit.MILLISECONDS.sleep(
                    RtLocksITCase.RANDOM.nextInt(1000)
                );
                sum.set(start + delta);
                deltas.add(delta);
                return null;
            },
            lock
        );
        new Callable<Void>() {
            @Override
            @Parallel(threads = 5)
            public Void call() throws Exception {
                atomic.call();
                return null;
            }
        } .call();
        int total = 0;
        for (final int delta : deltas) {
            total += delta;
        }
        MatcherAssert.assertThat(
            sum.get(),
            Matchers.equalTo(total)
        );
    }

    @Test
    void locksAndUnlocksInOneThread() throws Exception {
        final Sttc sttc = this.srule.get();
        final Locks locks = sttc.locks();
        final String name = String.format(
            "test2-%s", RtLocksITCase.RANDOM.nextInt(Integer.MAX_VALUE)
        );
        MatcherAssert.assertThat(locks.exists(name), Matchers.is(false));
        final Lock lock = locks.get(name);
        lock.lock("test");
        try {
            MatcherAssert.assertThat(locks.exists(name), Matchers.is(true));
            lock.unlock("");
            MatcherAssert.assertThat(locks.exists(name), Matchers.is(false));
        } finally {
            lock.unlock("");
        }
    }

    @Test
    void rejectsIncorrectLockName() {
        Assumptions.assumeFalse(System.getProperty("sttc.urn").isEmpty());
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> {
                final Sttc sttc = this.srule.get();
                final Locks locks = sttc.locks();
                locks.get("invalid name with spaces").lock("test-3");
            }
        );
    }

}
