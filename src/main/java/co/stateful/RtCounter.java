/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
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
