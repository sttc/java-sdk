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
@ToString(of = "label", includeFieldNames = false)
@EqualsAndHashCode(of = { "lrequest", "urequest" })
final class RtLock implements Lock {

    /**
     * Its name.
     */
    private final transient String label;

    /**
     * Lock request.
     */
    private final transient Request lrequest;

    /**
     * Unlock request.
     */
    private final transient Request urequest;

    /**
     * Ctor.
     * @param name Name of it
     * @param lreq Lock request
     * @param ureq Unlock request
     */
    RtLock(final String name, final Request lreq, final Request ureq) {
        this.label = name;
        this.lrequest = lreq;
        this.urequest = ureq;
    }

    @Override
    public String name() {
        return this.label;
    }

    @Override
    public boolean lock() throws IOException {
        final String marker = String.format(
            "co.stateful/java-sdk %s/%s; %s; Java %s; %s %s",
            Manifests.read("Sttc-Version"),
            Manifests.read("Sttc-Revision"),
            DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date()),
            System.getProperty("java.version"),
            System.getProperty("os.name"),
            System.getProperty("os.version")
        );
        return this.lrequest
            .body().formParam("label", marker).back()
            .fetch()
            .status() == HttpURLConnection.HTTP_SEE_OTHER;
    }

    @Override
    public void unlock() throws IOException {
        this.urequest
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_SEE_OTHER);
    }

}
