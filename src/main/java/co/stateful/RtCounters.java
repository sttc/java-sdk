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
 * Counters.
 *
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(of = { })
@EqualsAndHashCode(of = "request")
final class RtCounters implements Counters {

    /**
     * Entry request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param req Request
     */
    RtCounters(final Request req) {
        this.request = req;
    }

    @Override
    public Iterable<String> names() throws IOException {
        return this.request
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .xml()
            .xpath("/page/counters/counter/name/text()");
    }

    @Override
    public Counter create(final String name) throws IOException {
        this.request
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .rel("/page/links/link[@rel='add']/@href")
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .method(Request.POST)
            .body().formParam("name", name).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_SEE_OTHER);
        Logger.info(this, "counter \"%s\" created", name);
        return this.get(name);
    }

    @Override
    public void delete(final String name) throws IOException {
        this.request
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .rel(
                String.format(
                    // @checkstyle LineLength (1 line)
                    "/page/counters/counter[name='%s']/links/link[@rel='delete']/@href",
                    name
                )
            )
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .uri().queryParam("name", name).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_SEE_OTHER);
        Logger.info(this, "counter \"%s\" deleted", name);
    }

    @Override
    public Counter get(final String name) throws IOException {
        return new RtCounter(name, this.request);
    }
}
