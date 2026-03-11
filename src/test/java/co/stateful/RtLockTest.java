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
import java.net.HttpURLConnection;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtLock}.
 * @since 0.12.3
 */
final class RtLockTest {

    /**
     * Front XML.
     */
    private static final String XML = StringUtils.join(
        "<page><links><link rel='lock' href='#lock'/>",
        "<link rel='unlock' href='#unlock'/></links></page>"
    );

    @Test
    void locksViaHttp() throws Exception {
        final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(RtLockTest.XML))
            .next(new MkAnswer.Simple(HttpURLConnection.HTTP_SEE_OTHER, ""))
            .start();
        try {
            MatcherAssert.assertThat(
                new RtLock("foo", new JdkRequest(container.home())).lock(""),
                Matchers.equalTo(true)
            );
        } finally {
            container.stop();
        }
    }

    @Test
    void unlocksViaHttp() throws Exception {
        final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(RtLockTest.XML))
            .next(new MkAnswer.Simple(HttpURLConnection.HTTP_SEE_OTHER, ""))
            .start();
        try {
            MatcherAssert.assertThat(
                new RtLock("", new JdkRequest(container.home())).unlock(""),
                Matchers.equalTo(true)
            );
        } finally {
            container.stop();
        }
    }

    @Test
    void locksViaPost() throws Exception {
        final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(RtLockTest.XML))
            .next(new MkAnswer.Simple(HttpURLConnection.HTTP_SEE_OTHER, ""))
            .start();
        try {
            new RtLock("foo", new JdkRequest(container.home())).lock("");
        } finally {
            container.stop();
        }
        container.take();
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.is(Request.POST)
        );
    }

    @Test
    void locksSendsName() throws Exception {
        final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(RtLockTest.XML))
            .next(new MkAnswer.Simple(HttpURLConnection.HTTP_SEE_OTHER, ""))
            .start();
        try {
            new RtLock("foo", new JdkRequest(container.home())).lock("");
        } finally {
            container.stop();
        }
        container.take();
        MatcherAssert.assertThat(
            container.take().body(),
            Matchers.containsString("name=foo")
        );
    }

}

