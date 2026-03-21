/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtLocks}.
 * @since 0.12.3
 */
final class RtLocksTest {

    @Test
    void checksExistenceWhenLockPresent() throws Exception {
        final String xml = StringUtils.join(
            "<page><locks><lock><name>замок-αβγ</name>",
            "<label>мітка</label></lock></locks></page>"
        );
        final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(xml))
            .start();
        try {
            MatcherAssert.assertThat(
                "should return true when lock exists",
                new RtLocks(new JdkRequest(container.home())).exists("замок-αβγ"),
                Matchers.is(true)
            );
        } finally {
            container.stop();
        }
    }

    @Test
    void checksExistenceWhenLockAbsent() throws Exception {
        final String xml = StringUtils.join(
            "<page><locks><lock><name>другий</name>",
            "<label>інша</label></lock></locks></page>"
        );
        final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(xml))
            .start();
        try {
            MatcherAssert.assertThat(
                "should return false when lock does not exist",
                new RtLocks(new JdkRequest(container.home())).exists("замок-αβγ"),
                Matchers.is(false)
            );
        } finally {
            container.stop();
        }
    }

    @Test
    void checksExistenceWithEmptyLocksPage() throws Exception {
        final String xml = "<page><locks></locks></page>";
        final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(xml))
            .start();
        try {
            MatcherAssert.assertThat(
                "should return false when locks page is empty",
                new RtLocks(new JdkRequest(container.home())).exists("будь-який"),
                Matchers.is(false)
            );
        } finally {
            container.stop();
        }
    }

}
