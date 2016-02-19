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

import com.jcabi.aspects.Tv;
import java.security.SecureRandom;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link RtCounters}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
public final class RtCountersITCase {

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
     * RtCounters can manage counters.
     * @throws Exception If some problem inside
     */
    @Test
    public void incrementsAndSets() throws Exception {
        final Sttc sttc = this.srule.get();
        final Counters counters = sttc.counters();
        final String name = String.format(
            "test-%s", RtCountersITCase.RANDOM.nextInt(Integer.MAX_VALUE)
        );
        final Counter counter = counters.create(name);
        try {
            MatcherAssert.assertThat(
                counters.names(),
                Matchers.hasItem(name)
            );
            final long start = RtCountersITCase.RANDOM.nextLong();
            counter.set(start);
            MatcherAssert.assertThat(
                counter.incrementAndGet(0L),
                Matchers.equalTo(start)
            );
            final long delta = (long) Tv.FIVE;
            MatcherAssert.assertThat(
                counter.incrementAndGet(delta),
                Matchers.equalTo(start + delta)
            );
        } finally {
            counters.delete(name);
        }
    }

}
