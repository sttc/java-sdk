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

import com.jcabi.aspects.Immutable;
import java.util.concurrent.Callable;

/**
 * Lock.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.3
 */
@Immutable
@SuppressWarnings("PMD.DoNotUseThreads")
public interface Lock {

    /**
     * Call in a synchronized manner.
     * @param callable Callable to execute
     * @return Value
     * @param <T> Type of result
     * @throws Exception If any problem inside
     */
    <T> T call(Callable<T> callable) throws Exception;

    /**
     * Quiet.
     * @since 0.3
     */
    @Immutable
    final class Quiet {
        /**
         * Original lock.
         */
        private final transient Lock lock;
        /**
         * Ctor.
         * @param lck Lock
         */
        public Quiet(final Lock lck) {
            this.lock = lck;
        }
        /**
         * Call this callable code.
         * @param callable Code to call
         * @param <T> Type of result
         * @return Result
         */
        @SuppressWarnings("PMD.AvoidCatchingGenericException")
        public <T> T call(final Callable<T> callable) {
            final T result;
            try {
                result = this.lock.call(callable);
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final Exception ex) {
                throw new IllegalStateException(ex);
            }
            return result;
        }
        /**
         * Run this code.
         * @param runnable Runnable code
         */
        public void run(final Runnable runnable) {
            this.call(new Lock.Quiet.Wrap(runnable));
        }
        /**
         * Wrapper of runnable.
         */
        private static final class Wrap implements Callable<Void> {
            /**
             * Original runnable.
             */
            private final transient Runnable runnable;
            /**
             * Ctor.
             * @param rnbl Original
             */
            private Wrap(final Runnable rnbl) {
                this.runnable = rnbl;
            }
            @Override
            public Void call() throws Exception {
                this.runnable.run();
                return null;
            }
        }
    }

}
