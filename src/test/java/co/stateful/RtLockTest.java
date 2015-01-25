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

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import java.net.HttpURLConnection;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtLock}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.12.3
 */
public final class RtLockTest {

    /**
     * Front XML.
     */
    private static final String XML = StringUtils.join(
        "<page><links><link rel='lock' href='#lock'/>",
        "<link rel='unlock' href='#unlock'/></links></page>"
    );

    /**
     * RtLock can lock.
     * @throws Exception If some problem inside
     */
    @Test
    public void locksViaHttp() throws Exception {
        final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(RtLockTest.XML))
            .next(new MkAnswer.Simple(HttpURLConnection.HTTP_SEE_OTHER, ""))
            .start();
        final Lock lock = new RtLock("", new JdkRequest(container.home()));
        try {
            MatcherAssert.assertThat(lock.lock(), Matchers.equalTo(true));
        } finally {
            container.stop();
        }
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.GET)
        );
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.POST)
        );
    }

    /**
     * RtLock can unlock.
     * @throws Exception If some problem inside
     */
    @Test
    public void unlocksViaHttp() throws Exception {
        final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(RtLockTest.XML))
            .next(new MkAnswer.Simple(HttpURLConnection.HTTP_SEE_OTHER, ""))
            .start();
        final Lock lock = new RtLock("", new JdkRequest(container.home()));
        try {
            lock.unlock();
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
