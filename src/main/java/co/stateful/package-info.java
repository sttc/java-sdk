/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, stateful.co
 * SPDX-License-Identifier: MIT
 */

/**
 * Stateful.co SDK.
 *
 * <p>First, read the documentation at
 * <a href="https://java-sdk.stateful.co">java-sdk.stateful.co</a>. Then,
 * register an account at <a href="https://www.stateful.co">stateful.co</a>.
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
 * <a href="https://www.stateful.co">stateful.co</a>, right on the top.
 *
 * @since 0.1
 * @see <a href="http://www.yegor256.com/2014/05/18/cloud-autoincrement-counters.html">Atomic Counters at Stateful.co</a>
 * @see <a href="http://www.yegor256.com/2014/12/04/synchronization-between-nodes.html">Synchronization Between Nodes</a>
 */
package co.stateful;
