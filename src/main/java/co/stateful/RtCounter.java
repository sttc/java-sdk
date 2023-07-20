/*
 * Copyright (c) 2014-2023, stateful.co
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
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Counter.
 *
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(of = "label", includeFieldNames = false)
@EqualsAndHashCode(of = { "label", "request" })
final class RtCounter implements Counter {

    /**
     * Its name.
     */
    private final transient String label;

    /**
     * Home request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param name Name of it
     * @param req Home page request
     */
    RtCounter(final String name, final Request req) {
        this.label = name;
        this.request = req;
    }

    @Override
    public String name() {
        return this.label;
    }

    @Override
    public void set(final long value) throws IOException {
        final long start = System.currentTimeMillis();
        this.front("set").method(Request.PUT)
            .uri().queryParam("value", value).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
        if (Logger.isInfoEnabled(this)) {
            Logger.info(
                this, "counter \"%s\" set to %d in %[ms]s",
                this.label, value,
                System.currentTimeMillis() - start
            );
        }
    }

    @Override
    public long incrementAndGet(final long delta) throws IOException {
        final long start = System.currentTimeMillis();
        final long value = Long.parseLong(
            this.front("increment").method(Request.GET)
                .uri().queryParam("value", delta).back()
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN)
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .body()
        );
        if (Logger.isInfoEnabled(this)) {
            Logger.info(
                this, "counter \"%s\" incremented by %d to %d in %[ms]s",
                this.label, delta, value,
                System.currentTimeMillis() - start
            );
        }
        return value;
    }

    /**
     * Get front request.
     * @param ops Operation
     * @return Request
     * @throws IOException If fails
     */
    private Request front(final String ops) throws IOException {
        return this.request.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .rel(
                String.format(
                    // @checkstyle LineLength (1 line)
                    "/page/counters/counter[name='%s']/links/link[@rel='%s']/@href",
                    this.label, ops
                )
            );
    }

}
