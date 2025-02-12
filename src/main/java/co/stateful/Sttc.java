/*
 * Copyright (c) 2014-2025, stateful.co
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
import java.io.IOException;

/**
 * Stateful Entry Point.
 *
 * <p>Make an instance of this interface using {@link RtSttc}, for example,
 * if you want to use a cloud lock from
 * <a href="https://www.stateful.co">stateful.co</a>:
 *
 * <pre> Lock lock = new RtSttc(
 *   new URN("urn:github:12345"), "token"
 * ).locks().lock("test");</pre>
 *
 * <p>You need two arguments to instantiate {@link RtSttc}: URN of the user
 * and your secret token. You can get them at the home page of
 * <a href="https://www.stateful.co">stateful.co</a>, right on the top.
 *
 * @since 0.1
 * @see <a href="http://www.yegor256.com/2014/05/18/cloud-autoincrement-counters.html">Atomic Counters at Stateful.co</a>
 * @see <a href="http://www.yegor256.com/2014/12/04/synchronization-between-nodes.html">Synchronization Between Nodes</a>
 */
@Immutable
public interface Sttc {

    /**
     * Counters.
     * @return Counters
     * @throws IOException If some I/O problem
     */
    Counters counters() throws IOException;

    /**
     * Locks.
     * @return Locks
     * @throws IOException If some I/O problem
     */
    Locks locks() throws IOException;

}
