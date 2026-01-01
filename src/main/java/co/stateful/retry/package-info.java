/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */

/**
 * SDK that retries on failures.
 *
 * <p>It is recommended to use decorators from this package in
 * production environment. They will provide some guarantee that in
 * case of accidental network outage your code will make a few
 * honest retries before throwing a runtime exception. Just
 * wrap your actual {@code Sttc} implemntation into
 * {@link ReSttc} and that's it:</p>
 *
 * <pre>Sttc sttc = new ReSttc(
 *   new RtSttc(new URN("urn:github:12345"), "auth-key")
 * );</pre>
 *
 * @since 0.5
 */
package co.stateful.retry;
