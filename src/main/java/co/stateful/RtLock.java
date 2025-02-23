/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Lock.
 *
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
    public String label() throws IOException {
        final long start = System.currentTimeMillis();
        final String label = this.front("label")
            .uri().queryParam("name", this.lck).back()
            .method(Request.GET)
            .fetch()
            .body();
        if (Logger.isInfoEnabled(this)) {
            Logger.info(
                this, "label of \"%s\" retrieved in %[ms]s: \"%s\"",
                this.lck,
                System.currentTimeMillis() - start,
                label
            );
        }
        return label;
    }

    @Override
    public boolean lock(final String label) throws IOException {
        final long start = System.currentTimeMillis();
        final Response rsp = this.front("lock")
            .body().formParam("label", label)
            .formParam("name", this.lck).back()
            .method(Request.POST)
            .fetch();
        final boolean locked = rsp.status() == HttpURLConnection.HTTP_SEE_OTHER;
        if (Logger.isInfoEnabled(this)) {
            Logger.info(
                this, "lock of \"%s\" is %B in %[ms]s: %s",
                this.lck, locked,
                System.currentTimeMillis() - start,
                rsp.body()
            );
        }
        return locked;
    }

    @Override
    public boolean unlock(final String label) throws IOException {
        final long start = System.currentTimeMillis();
        final Response rsp = this.front("unlock")
            .uri().queryParam("label", label)
            .queryParam("name", this.lck).back()
            .method(Request.GET)
            .fetch();
        final boolean unlocked =
            rsp.status() == HttpURLConnection.HTTP_SEE_OTHER;
        if (Logger.isInfoEnabled(this)) {
            Logger.info(
                this, "unlock of \"%s\" is %B in %[ms]s",
                this.lck, unlocked,
                System.currentTimeMillis() - start
            );
        }
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
            .rel(String.format("/page/links/link[@rel='%s']/@href", label));
    }

}
