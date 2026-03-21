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
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Locks.
 *
 * @since 0.2
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(of = { })
@EqualsAndHashCode(of = "request")
final class RtLocks implements Locks {

    /**
     * Entry response.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param req Request
     */
    RtLocks(final Request req) {
        this.request = req;
    }

    @Override
    public boolean exists(final String name) throws IOException {
        return !this.request
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .xml()
            .nodes(String.format("/page/locks/lock[name='%s']", name))
            .isEmpty();
    }

    @Override
    public Lock get(final String name) {
        return new RtLock(name, this.request);
    }

}
