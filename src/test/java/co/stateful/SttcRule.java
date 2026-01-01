/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import co.stateful.cached.CdSttc;
import co.stateful.retry.ReSttc;
import com.jcabi.urn.URN;
import org.junit.jupiter.api.Assumptions;

/**
 * Sttc test rule.
 * @since 0.1
 */
final class SttcRule {

    /**
     * URN of stateful.co.
     */
    private final transient String user;

    /**
     * Token of stateful.co.
     */
    private final transient String token;

    /**
     * Ctor.
     */
    SttcRule() {
        this(
            System.getProperty("sttc.urn"),
            System.getProperty("sttc.token")
        );
    }

    /**
     * Ctor.
     * @param urn User URN
     * @param tkn Token
     */
    SttcRule(final String urn, final String tkn) {
        this.user = urn;
        this.token = tkn;
    }

    /**
     * Get Sttc.
     * @return Sttc
     */
    Sttc get() {
        Assumptions.assumeFalse(this.user == null);
        Assumptions.assumeFalse(this.user.isEmpty());
        return new ReSttc(
            new CdSttc(
                new RtSttc(
                    URN.create(this.user),
                    this.token
                )
            )
        );
    }

}
