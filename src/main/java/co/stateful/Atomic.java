/**
 * Copyright (c) 2014-2016, stateful.co
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

import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.Tv;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Atomic block of code.
 *
 * <p>This class runs your {@link Callable} in a concurrent thread-safe
 * manner, using a lock from Stateful.co. For example:
 *
 * <pre> Callable&lt;String&gt; origin = new Callable&lt;String&gt;() {
 *   &#64;Override
 *   public String call() {
 *     // fetch it from a thread-critical resource
 *     // and return
 *   }
 * };
 * Lock lock = new RtSttc(new URN("urn:github:12345"), "token")
 *   .locks().lock("test");
 * String result = new Atomic&lt;String&gt;(origin, lock).call();</pre>
 *
 * <p>If you want to use {@link Runnable} instead, try static method
 * {@link java.util.concurrent.Executors#callable(Runnable)}. If you
 * want to avoid checked exceptions, use {@link #callQuietly()}.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.6
 * @param <T> Type of result
 * @link <a href="http://www.yegor256.com/2014/05/18/cloud-autoincrement-counters.html">Atomic Counters at Stateful.co</a>
 * @link <a href="http://www.yegor256.com/2014/12/04/synchronization-between-nodes.html">Synchronization Between Nodes</a>
 */
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "callable", "lock" })
public final class Atomic<T> implements Callable<T> {

    /**
     * Random.
     */
    private static final Random RANDOM = new SecureRandom();

    /**
     * Shutdown hook.
     */
    private final transient Thread hook;

    /**
     * Callable to use.
     */
    private final transient Callable<T> callable;

    /**
     * Encapsulated lock to use.
     */
    private final transient Lock lock;

    /**
     * Maximum waiting time, in milliseconds.
     */
    private final transient long max;

    /**
     * Successfully locked?
     */
    private final transient AtomicBoolean locked;

    /**
     * Label for locking.
     */
    private final transient String label;

    /**
     * Public ctor (default maximum waiting time of five minutes).
     * @param clbl Callable to use
     * @param lck Lock to use
     */
    public Atomic(final Callable<T> clbl, final Lock lck) {
        this(clbl, lck, "");
    }

    /**
     * Public ctor (default maximum waiting time of five minutes).
     * @param clbl Callable to use
     * @param lck Lock to use
     * @param lbl Label to use for locking and unlocking (can be empty)
     */
    public Atomic(final Callable<T> clbl, final Lock lck, final String lbl) {
        this(clbl, lck, lbl, TimeUnit.MINUTES.toMillis((long) Tv.FIVE));
    }

    /**
     * Public ctor.
     * @param clbl Callable to use
     * @param lck Lock to use
     * @param lbl Label to use for locking and unlocking (can be empty)
     * @param maximum Maximum waiting time
     * @since 0.8
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public Atomic(final Callable<T> clbl, final Lock lck, final String lbl,
        final long maximum) {
        this.hook = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        Atomic.this.lock.unlock(lbl);
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
        this.locked = new AtomicBoolean();
        this.callable = clbl;
        this.lock = lck;
        this.max = maximum;
        this.label = lbl;
    }

    @Override
    public T call() throws Exception {
        Runtime.getRuntime().addShutdownHook(this.hook);
        long attempt = 0L;
        final long start = System.currentTimeMillis();
        while (!this.lock.lock(this.label)) {
            final long age = System.currentTimeMillis() - start;
            if (age > this.max) {
                throw new IllegalStateException(
                    Logger.format(
                        // @checkstyle LineLength (1 line)
                        "lock \"%s\" is stale (%d failed attempts in %[ms]s, which is over %[ms]s)",
                        this.lock, attempt, age, this.max
                    )
                );
            }
            ++attempt;
            final long delay = Math.min(
                TimeUnit.MINUTES.toMillis(1L),
                Math.abs(
                    (long) Tv.HUNDRED + (long) Atomic.RANDOM.nextInt(Tv.HUNDRED)
                    // @checkstyle MagicNumber (1 line)
                    + (long) StrictMath.pow(5.0d, (double) attempt + 1.0d)
                )
            );
            Logger.info(
                this,
                // @checkstyle LineLength (1 line)
                "lock \"%s\" is occupied for %[ms]s already, attempt #%d in %[ms]s",
                this.lock, age, attempt, delay
            );
            TimeUnit.MILLISECONDS.sleep(delay);
        }
        this.locked.set(true);
        try {
            return this.callable.call();
        } finally {
            this.unlock();
            Logger.info(
                this,
                // @checkstyle LineLength (1 line)
                "\"%s\" took %[ms]s after %d attempt(s)",
                this.lock, System.currentTimeMillis() - start, attempt + 1L
            );
        }
    }

    /**
     * Call without exception throwing.
     * @return Result
     * @since 0.9
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public T callQuietly() {
        try {
            return this.call();
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Unlock it.
     * @throws IOException If fails
     */
    private void unlock() throws IOException {
        if (this.locked.get()) {
            this.lock.unlock(this.label);
        }
        Runtime.getRuntime().removeShutdownHook(this.hook);
    }

}
