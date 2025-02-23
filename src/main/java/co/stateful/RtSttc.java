/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import co.stateful.mock.MkSttc;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.http.wire.CachingWire;
import com.jcabi.http.wire.OneMinuteWire;
import com.jcabi.http.wire.RetryWire;
import com.jcabi.http.wire.VerboseWire;
import com.jcabi.urn.URN;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Stateful Entry Point.
 *
 * <p>Make an instance of this class and start from there, for example,
 * if you want to use a cloud lock from
 * <a href="https://www.stateful.co">stateful.co</a>:
 *
 * <pre> Lock lock = new RtSttc(
 *   new URN("urn:github:12345"), "token"
 * ).locks().lock("test");</pre>
 *
 * <p>You need two arguments to instantiate this class: URN of the user
 * and your secret token. You can get them at the home page of
 * <a href="https://www.stateful.co">stateful.co</a>, right on the top.
 *
 * <p>It is highly recommended to use
 * {@link co.stateful.retry.ReSttc} decorator, in production
 * environment.</p>
 *
 * <p>It is also highly recommended to use
 * {@link co.stateful.cached.CdSttc} decorator, in production
 * environment.</p>
 *
 * @since 0.1
 * @see <a href="http://www.yegor256.com/2014/05/18/cloud-autoincrement-counters.html">Atomic Counters at Stateful.co</a>
 * @see <a href="http://www.yegor256.com/2014/12/04/synchronization-between-nodes.html">Synchronization Between Nodes</a>
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(of = { })
@EqualsAndHashCode(of = "request")
public final class RtSttc implements Sttc {

    /**
     * Entry request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param urn Owner URN
     * @param token Security token
     */
    public RtSttc(final URN urn, final String token) {
        this.request = new JdkRequest("https://www.stateful.co")
            .through(OneMinuteWire.class)
            .through(RetryWire.class)
            .through(VerboseWire.class)
            .through(CachingWire.class, "((POST|PUT|PATCH) .*|.*\\?.*)")
            .header("X-Sttc-URN", urn.toString())
            .header("X-Sttc-Token", token)
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .header(HttpHeaders.USER_AGENT, "java-sdk.stateful.co");
    }

    /**
     * Make an instance of it.
     * @param urn Owner URN
     * @param token Security token
     * @return Sttc
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Sttc make(final URN urn, final String token) {
        final Sttc sttc;
        if (token.matches("[A-F0-9\\-]{19}")) {
            sttc = new RtSttc(urn, token);
        } else {
            sttc = new MkSttc();
        }
        return sttc;
    }

    @Override
    public Counters counters() throws IOException {
        return new RtCounters(
            this.request
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(XmlResponse.class)
                .rel("/page/links/link[@rel='menu:counters']/@href")
        );
    }

    @Override
    public Locks locks() throws IOException {
        return new RtLocks(
            this.request
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(XmlResponse.class)
                .rel("/page/links/link[@rel='menu:locks']/@href")
        );
    }
}
