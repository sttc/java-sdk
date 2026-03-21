/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.log.Logger;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.HttpURLConnection;
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
        this.front("set").method(Request.PUT)
            .uri().queryParam("value", value).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
        Logger.info(this, "counter \"%s\" set to %d", this.label, value);
    }

    @Override
    public long incrementAndGet(final long delta) throws IOException {
        final long value = Long.parseLong(
            this.front("increment").method(Request.GET)
                .uri().queryParam("value", delta).back()
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN)
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .body()
        );
        Logger.info(
            this, "counter \"%s\" incremented by %d to %d",
            this.label, delta, value
        );
        return value;
    }

    /**
     * Get front request.
     * @param ops Operation
     * @return Request
     * @throws IOException If fails
     */
    private Request front(final String ops) throws IOException {
        return this.request
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .fetch()
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
