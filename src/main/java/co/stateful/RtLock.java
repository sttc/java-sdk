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
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.log.Logger;
import com.jcabi.manifests.Manifests;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Lock.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.3
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(of = "lck", includeFieldNames = false)
@EqualsAndHashCode(of = { "lck", "request" })
final class RtLock implements Lock {

    /**
     * Its name.
     */
    private final transient String lck;

    /**
     * Locks home.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param name Name of it
     * @param req Lock request
     */
    RtLock(final String name, final Request req) {
        this.lck = name;
        this.request = req;
    }

    @Override
    public String name() {
        return this.lck;
    }

    @Override
    public boolean lock() throws IOException {
        return this.lock(
            String.format(
                "co.stateful/java-sdk %s/%s; %s; Java %s; %s %s",
                Manifests.read("Sttc-Version"),
                Manifests.read("Sttc-Revision"),
                DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date()),
                System.getProperty("java.version"),
                System.getProperty("os.name"),
                System.getProperty("os.version")
            )
        );
    }

    @Override
    public void unlock() throws IOException {
        this.unlock("");
    }

    @Override
    public boolean lock(final String label) throws IOException {
        final long start = System.currentTimeMillis();
        final boolean locked = this.front("lock")
            .body().formParam("label", label).back()
            .fetch()
            .status() == HttpURLConnection.HTTP_SEE_OTHER;
        Logger.info(
            this, "lock of \"%s\" is %s in %[ms]s",
            this.lck, Boolean.toString(locked),
            System.currentTimeMillis() - start
        );
        return locked;
    }

    @Override
    public boolean unlock(final String label) throws IOException {
        final long start = System.currentTimeMillis();
        final boolean unlocked = this.front("unlock")
            .uri().queryParam("label", label).back()
            .fetch()
            .status() == HttpURLConnection.HTTP_SEE_OTHER;
        Logger.info(
            this, "unlocked \"%s\" in %[ms]s: %B",
            this.lck, System.currentTimeMillis() - start, unlocked
        );
        return unlocked;
    }

    /**
     * Get front request.
     * @param label Label
     * @return Request
     * @throws IOException If fails
     */
    private Request front(final String label) throws IOException {
        return this.request
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .rel(String.format("/page/links/link[@rel='%s']/@href", label))
            .method(Request.GET)
            .uri().queryParam("name", this.lck).back();
    }

}
