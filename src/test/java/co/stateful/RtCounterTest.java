/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtCounter}.
 * @since 0.12.3
 */
final class RtCounterTest {

    /**
     * Front XML.
     */
    private static final String XML = StringUtils.join(
        "<page><counters><counter><name></name><links>",
        "<link rel='set' href='#set'/>",
        "<link rel='increment' href='#increment'/></links>",
        "</counter></counters></page>"
    );

    @Test
    void setsViaHttp() throws Exception {
        final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(RtCounterTest.XML))
            .next(new MkAnswer.Simple(""))
            .start();
        final Counter cnt = new RtCounter("", new JdkRequest(container.home()));
        try {
            cnt.set(1L);
        } finally {
            container.stop();
        }
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.GET)
        );
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.PUT)
        );
    }

    @Test
    void incrementsViaHttp() throws Exception {
        final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(RtCounterTest.XML))
            .next(new MkAnswer.Simple("1"))
            .start();
        final Counter cnt = new RtCounter("", new JdkRequest(container.home()));
        try {
            MatcherAssert.assertThat(
                cnt.incrementAndGet(1L), Matchers.equalTo(1L)
            );
        } finally {
            container.stop();
        }
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.GET)
        );
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.GET)
        );
    }

}
