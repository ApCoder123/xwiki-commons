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
package org.xwiki.job.event.status;

import org.xwiki.observation.event.Event;

/**
 * Indicate to the progress listener that a new step level is starting.
 * 
 * @version $Id$
 * @since 4.0M1
 */
public class PushLevelProgressEvent implements Event
{
    /**
     * Number of sub steps.
     */
    private int steps;

    /**
     * Matches any {@link PushLevelProgressEvent}.
     */
    public PushLevelProgressEvent()
    {

    }

    /**
     * @param steps the number of sub steps.
     */
    public PushLevelProgressEvent(int steps)
    {
        this.steps = steps;
    }

    @Override
    public boolean matches(Object arg0)
    {
        return true;
    }

    /**
     * @return the number of sub steps
     */
    public int getSteps()
    {
        return steps;
    }
}
