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
package org.xwiki.context.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.xwiki.context.Execution;
import org.xwiki.context.ExecutionContext;
import org.xwiki.context.ExecutionContextProperty;
import org.xwiki.context.ExecutionContextException;
import org.xwiki.context.ExecutionContextInitializer;

/**
 * Unit tests for {@link ExecutionContext}.
 * 
 * @version $Id$
 * @since 1.8RC3
 */
@SuppressWarnings("unchecked")
public class DefaultExecutionContextManagerTest extends TestCase
{
    /**
     * Verify we have different objects in the Execution Context after the clone.
     */
    public void testClone() throws Exception
    {
        Execution execution = new DefaultExecution();
        ExecutionContext context = new ExecutionContext();
        execution.setContext(context);

        Map xwikicontext = new HashMap();
        ExecutionContextProperty xwikiContextProperty = new ExecutionContextProperty("xwikicontext");
        xwikiContextProperty.setValue(xwikicontext);
        xwikiContextProperty.setInherited(true);
        context.declareProperty(xwikiContextProperty);
        Map velocitycontext = new HashMap();
        ExecutionContextProperty velocityContextProperty = new ExecutionContextProperty("velocitycontext");
        velocityContextProperty.setValue(xwikicontext);
        velocityContextProperty.setInherited(true);
        velocityContextProperty.setReadonly(true);
        velocityContextProperty.setCloneValue(true);
        context.declareProperty(velocityContextProperty);

        DefaultExecutionContextManager contextManager = new DefaultExecutionContextManager(execution);
        contextManager.addExecutionContextInitializer(new ExecutionContextInitializer() {
            @Override
            public void initialize(ExecutionContext context) throws ExecutionContextException
            {
                context.setProperty("key", Arrays.asList("value"));
            }
        });
        
        ExecutionContext clonedContext = contextManager.clone(context);
        
        assertSame(context, execution.getContext());
        assertEquals("value", ((List<String>) clonedContext.getProperty("key")).get(0));
        assertNotSame(context.getProperty("key"), clonedContext.getProperty("key"));
        assertSame(xwikicontext, clonedContext.getProperty("xwikicontext"));
        assertNotSame(xwikicontext, clonedContext.getProperty("velocitycontext"));
    }
}
