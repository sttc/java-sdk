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
        return this.request.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .xml()
            .xpath("/page/counters/counter/name/text()");
    }

    @Override
    public Counter create(final String name) throws IOException {
        final long start = System.currentTimeMillis();
        this.request.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .rel("/page/links/link[@rel='add']/@href")
            .method(Request.POST)
            .body().formParam("name", name).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_SEE_OTHER);
        if (Logger.isInfoEnabled(this)) {
            Logger.info(
                this, "counter \"%s\" created in %[ms]s",
                name, System.currentTimeMillis() - start
            );
        }
        return this.get(name);
    }

    @Override
    public void delete(final String name) throws IOException {
        final long start = System.currentTimeMillis();
        this.request.fetch()
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
            .uri().queryParam("name", name).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_SEE_OTHER);
        if (Logger.isInfoEnabled(this)) {
            Logger.info(
                this, "counter \"%s\" deleted in %[ms]s",
                name, System.currentTimeMillis() - start
            );
        }
    }

    @Override
    public Counter get(final String name) throws IOException {
        return new RtCounter(name, this.request);
    }
}
