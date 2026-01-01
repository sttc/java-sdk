/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */

/**
 * Mock of SDK.
 *
 * <p>This packages gives you an ability to use {@code Sttc} in
 * unit tests, without mocking frameworks. Just make an instance
 * of {@link MkSttc} class and work with it as if it was a normal
 * adapter of <a href="http://www.statefu.co">stateful.co</a>:
 *
 * <pre>public class FooTest {
 *   &#64;Test
 *   public void worksWithCounter() {
 *     Sttc sttc = new MkSttc();
 *     new Foo(sttc.counters().get("my-counter")).doIt();
 *   }
 * }</pre>
 *
 * @since 0.1
 */
package co.stateful.mock;
