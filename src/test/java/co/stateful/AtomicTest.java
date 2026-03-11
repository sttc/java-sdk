/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import co.stateful.mock.MkSttc;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Atomic}.
 * @since 0.6
 */
final class AtomicTest {

    @Test
    void runsCallable() throws Exception {
        MatcherAssert.assertThat(
            new Atomic<>(
                () -> "hello, world",
                new MkSttc().locks().get("test")
            ).callQuietly(),
            Matchers.containsString("hello")
        );
    }

    @Test
    void unlocksWhenCrashed() throws Exception {
        final Lock lock = Mockito.mock(Lock.class);
        Mockito.doReturn(true).when(lock).lock(Mockito.anyString());
        org.junit.jupiter.api.Assertions.assertThrows(
            IOException.class,
            () -> new Atomic<>(
                () -> {
                    throw new IOException("expected one");
                },
                lock
            ).call(),
            "exception was not thrown as expected"
        );
    }

    @Test
    void callsUnlockAfterException() throws Exception {
        final Lock lock = Mockito.mock(Lock.class);
        Mockito.doReturn(true).when(lock).lock(Mockito.anyString());
        final Atomic<String> atomic = new Atomic<>(
            () -> {
                throw new IOException("expected two");
            },
            lock
        );
        org.junit.jupiter.api.Assertions.assertAll(
            () -> org.junit.jupiter.api.Assertions.assertThrows(
                IOException.class,
                atomic::call
            ),
            () -> Mockito.verify(lock).unlock(Mockito.anyString())
        );
    }

}

