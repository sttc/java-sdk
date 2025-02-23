/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.mock;

import co.stateful.Counters;
import co.stateful.Locks;
import co.stateful.Sttc;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock.
 *
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode
public final class MkSttc implements Sttc {

    @Override
    public Counters counters() {
        return new MkCounters();
    }

    @Override
    public Locks locks() {
        return new MkLocks();
    }
}
