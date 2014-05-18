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

import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.Tv;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
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
 * want to avoid checked exceptions, try {@code Callable}
 * from tempusfugitlibrary.org.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.6
 * @see <a href="http://tempusfugitlibrary.org/documentation/callables/custom/">tempusfugitlibrary.org</a>
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
     * Public ctor.
     * @param clbl Callable to use
     * @param lck Lock to use
     */
    public Atomic(final Callable<T> clbl, final Lock lck) {
        this.callable = clbl;
        this.lock = lck;
    }

    @Override
    public T call() throws Exception {
        while (!this.lock.lock()) {
            TimeUnit.MILLISECONDS.sleep(
                (long) Tv.HUNDRED + (long) Atomic.RANDOM.nextInt(Tv.HUNDRED)
            );
        }
        try {
            return this.callable.call();
        } finally {
            this.lock.unlock();
        }
    }

}
