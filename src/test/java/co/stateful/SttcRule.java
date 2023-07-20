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
