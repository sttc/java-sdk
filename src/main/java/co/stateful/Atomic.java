/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.aspects.Loggable;
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
 * @since 0.6
 * @param <T> Type of result
 * @see <a href="http://www.yegor256.com/2014/05/18/cloud-autoincrement-counters.html">Atomic Counters at Stateful.co</a>
 * @see <a href="http://www.yegor256.com/2014/12/04/synchronization-between-nodes.html">Synchronization Between Nodes</a>
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
        this(clbl, lck, lbl, TimeUnit.MINUTES.toMillis(5L));
    }

    // @checkstyle ParameterNumberCheck (15 lines)
    /**
     * Public ctor.
     * @param clbl Callable to use
     * @param lck Lock to use
     * @param lbl Label to use for locking and unlocking (can be empty)
     * @param maximum Maximum waiting time
     * @since 0.8
     */
    public Atomic(final Callable<T> clbl, final Lock lck, final String lbl,
        final long maximum) {
        this.locked = new AtomicBoolean();
        this.callable = clbl;
        this.lock = lck;
        this.max = maximum;
        this.label = lbl;
    }

    @Override
    public T call() throws Exception {
        final Thread hook = new Thread(
            () -> {
                try {
                    this.lock.unlock(this.label);
                } catch (final IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        );
        Runtime.getRuntime().addShutdownHook(hook);
        long attempt = 0L;
        final long start = System.currentTimeMillis();
        while (!this.lock.lock(this.label)) {
            final long age = System.currentTimeMillis() - start;
            if (age > this.max) {
                throw new IllegalStateException(
                    Logger.format(
                        "lock \"%s\" is stale (%d failed attempts in %[ms]s, which is over %[ms]s)",
                        this.lock, attempt, age, this.max
                    )
                );
            }
            ++attempt;
            final long delay = Math.min(
                TimeUnit.MINUTES.toMillis(1L),
                Math.abs(
                    100L + (long) Atomic.RANDOM.nextInt(100)
                    + (long) StrictMath.pow(5.0d, (double) attempt + 1.0d)
                )
            );
            Logger.info(
                this,
                "lock \"%s\" is occupied for %[ms]s already, attempt #%d in %[ms]s",
                this.lock, age, attempt, delay
            );
            TimeUnit.MILLISECONDS.sleep(delay);
        }
        this.locked.set(true);
        try {
            return this.callable.call();
        } finally {
            if (this.locked.get()) {
                this.lock.unlock(this.label);
            }
            Runtime.getRuntime().removeShutdownHook(hook);
            if (Logger.isInfoEnabled(this)) {
                Logger.info(
                    this,
                    "\"%s\" took %[ms]s after %d attempt(s)",
                    this.lock, System.currentTimeMillis() - start, attempt + 1L
                );
            }
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

}
