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
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.Tv;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.manifests.Manifests;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Locks.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.2
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "request")
public final class RtLocks implements Locks {

    /**
     * Entry request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param req Request
     */
    public RtLocks(final Request req) {
        this.request = req;
    }

    @Override
    public <T> T call(final String name, final Callable<T> callable)
        throws Exception {
        while (!this.lock(name)) {
            TimeUnit.MILLISECONDS.sleep((long) Tv.HUNDRED);
        }
        try {
            return callable.call();
        } finally {
            this.unlock(name);
        }
    }

    /**
     * Lock.
     * @param name Name of the lock
     * @return TRUE if locked
     * @throws IOException If fails
     */
    private boolean lock(final String name) throws IOException {
        final String label = String.format(
            "co.stateful/java-sdk %s/%s; %s; Java %s; %s %s",
            Manifests.read("Sttc-Version"),
            Manifests.read("Sttc-Revision"),
            DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date()),
            System.getProperty("java.version"),
            System.getProperty("os.name"),
            System.getProperty("os.version")
        );
        return this.request.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .rel("/page/links/link[@rel='lock']/@href")
            .method(Request.POST)
            .body().formParam("name", name).formParam("label", label).back()
            .fetch()
            .status() == HttpURLConnection.HTTP_SEE_OTHER;
    }

    /**
     * UnLock.
     * @param name Name of the lock
     * @throws IOException If fails
     */
    private void unlock(final String name) throws IOException {
        this.request.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .rel("/page/links/link[@rel='unlock']/@href")
            .method(Request.GET)
            .uri().queryParam("name", name).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_SEE_OTHER);
    }

}
