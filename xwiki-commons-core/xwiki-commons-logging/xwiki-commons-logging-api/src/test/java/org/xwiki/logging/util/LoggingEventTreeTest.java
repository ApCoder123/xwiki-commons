/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.logging.util;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.event.LoggingEvent;
import org.xwiki.logging.LoggingEventMessage;

/**
 * Test {@link LoggingEventTree}.
 *
 * @version $Id$
 */
public class LoggingEventTreeTest
{
    @Test
    public void testTwoLevel0()
    {
        LoggingEventTree logTree = new LoggingEventTree();

        logTree.error("message1");
        logTree.error("message2");

        Assert.assertEquals(2, logTree.size(false));
        Assert.assertEquals(2, logTree.size(true));
    }

    @Test
    public void test3Levels()
    {
        LoggingEventTree logTree = new LoggingEventTree();

        logTree.error(LoggingEventMessage.MARKER_BEGIN, "begin1");
        logTree.error("message11");
        logTree.error(LoggingEventMessage.MARKER_BEGIN, "begin12");
        logTree.error("message121");
        logTree.error("message122");
        logTree.error(LoggingEventMessage.MARKER_END, "end12");
        logTree.error(LoggingEventMessage.MARKER_END, "end1");

        logTree.error(LoggingEventMessage.MARKER_BEGIN, "begin2");
        logTree.error("message21");
        logTree.error(LoggingEventMessage.MARKER_BEGIN, "begin22");
        logTree.error("message221");
        logTree.error("message222");
        logTree.error(LoggingEventMessage.MARKER_END, "end22");
        logTree.error(LoggingEventMessage.MARKER_END, "end2");

        Assert.assertEquals(2, logTree.size(false));
        Assert.assertEquals(14, logTree.size(true));

        Iterator<LoggingEvent> iterator0 = logTree.iterator();

        LoggingEventTreeNode node1 = (LoggingEventTreeNode) iterator0.next();

        Assert.assertEquals(3, node1.size(false));
        Assert.assertEquals(6, node1.size(true));

        Iterator<LoggingEvent> iterator1 = node1.iterator();

        iterator1.next();

        LoggingEventTreeNode node11 = (LoggingEventTreeNode) iterator1.next();

        Assert.assertEquals(3, node11.size(false));
        Assert.assertEquals(3, node11.size(true));
    }
}
