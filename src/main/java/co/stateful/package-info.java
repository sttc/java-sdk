/**
 * Copyright (c) 2014, stateful.co
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

/**
 * Stateful.co SDK.
 *
 * <p>First, read the documentation at
 * <a href="http://java-sdk.stateful.co">java-sdk.stateful.co</a>. Then,
 * register an account at <a href="http://www.stateful.co">stateful.co</a>.
 * Then, you can try to use, for example, counters:
 *
 * <pre> public class Main {
 *   public static void main(String... args) {
 *     Sttc sttc = new RtSttc(
 *       new URN("urn:github:526301"),
 *       "9FF3-41E0-73FB-F900"
 *     );
 *     String name = "test-123";
 *     Counters counters = sttc.counters();
 *     Counter counter = counters.create(name);
 *     long value = counter.incrementAndGet(1L);
 *     System.out.println("new value: " + value);
 *     counters.delete(name);
 *   }
 * }</pre>
 *
 * <p>You need two arguments to instantiate {@link RtSttc}: URN of the user
 * and your secret token. You can get them at the home page of
 * <a href="http://www.stateful.co">stateful.co</a>, right on the top.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @link <a href="http://www.yegor256.com/2014/05/18/cloud-autoincrement-counters.html">Atomic Counters at Stateful.co</a>
 * @link <a href="http://www.yegor256.com/2014/12/04/synchronization-between-nodes.html">Synchronization Between Nodes</a>
 */
package co.stateful;
