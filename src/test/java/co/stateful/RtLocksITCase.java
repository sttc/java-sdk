/**
 * Copyright (c) 2014, stateful.co
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the stateful.co nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package co.stateful;

import com.jcabi.aspects.Parallel;
import com.jcabi.aspects.Tv;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link RtLocks}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.2
 */
public final class RtLocksITCase {

    /**
     * Random.
     */
    private static final Random RANDOM = new SecureRandom();

    /**
     * Region rule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    public final transient SttcRule srule = new SttcRule();

    /**
     * RtLocks can manage locks.
     * @throws Exception If some problem inside
     */
    @Test
    public void locksAndUnlocks() throws Exception {
        final Sttc sttc = this.srule.get();
        final Lock lock = sttc.locks().get(
            String.format(
                "test-%s", RtLocksITCase.RANDOM.nextInt(Integer.MAX_VALUE)
            )
        );
        final AtomicInteger sum = new AtomicInteger();
        final Collection<Integer> deltas = new CopyOnWriteArrayList<Integer>();
        final Callable<Void> atomic = new Atomic<Void>(
            new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    final int start = sum.get();
                    final int delta = RtLocksITCase.RANDOM.nextInt();
                    TimeUnit.MILLISECONDS.sleep(
                        (long) RtLocksITCase.RANDOM.nextInt(Tv.THOUSAND)
                    );
                    sum.set(start + delta);
                    deltas.add(delta);
                    return null;
                }
            },
            lock
        );
        new Callable<Void>() {
            @Override
            @Parallel(threads = Tv.FIVE)
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

    /**
     * RtLocks can manage locks in one simple thread.
     * @throws Exception If some problem inside
     */
    @Test
    public void locksAndUnlocksInOneThread() throws Exception {
        final Sttc sttc = this.srule.get();
        final Locks locks = sttc.locks();
        final String name = String.format(
            "test2-%s", RtLocksITCase.RANDOM.nextInt(Integer.MAX_VALUE)
        );
        MatcherAssert.assertThat(locks.exists(name), Matchers.is(false));
        final Lock lock = locks.get(name);
        lock.lock();
        try {
            MatcherAssert.assertThat(locks.exists(name), Matchers.is(true));
            lock.unlock();
            MatcherAssert.assertThat(locks.exists(name), Matchers.is(false));
        } finally {
            lock.unlock();
        }
    }

    /**
     * RtLocks can reject an incorrect name of a lock.
     * @throws Exception If some problem inside
     */
    @Test(expected = IllegalArgumentException.class)
    public void rejectsIncorrectLockName() throws Exception {
        final Sttc sttc = this.srule.get();
        final Locks locks = sttc.locks();
        locks.get("invalid name with spaces").lock();
    }

}
