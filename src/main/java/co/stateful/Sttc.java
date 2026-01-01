/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
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
