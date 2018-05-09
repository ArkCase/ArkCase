
package org.mule.module.cmis.process;

/*-
 * #%L
 * ACM Mule CMIS Connector
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.NestedProcessor;
import org.mule.api.context.MuleContextAware;
import org.mule.api.processor.MessageProcessor;

import javax.annotation.Generated;

import java.util.Map;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class NestedProcessorChain implements NestedProcessor, MuleContextAware
{

    /**
     * Mule Context
     * 
     */
    private MuleContext muleContext;
    /**
     * Chain that will be executed upon calling process
     * 
     */
    private MessageProcessor chain;
    /**
     * Event that will be cloned for dispatching
     * 
     */
    private MuleEvent event;

    public NestedProcessorChain(MuleEvent event, MuleContext muleContext, MessageProcessor chain)
    {
        this.event = event;
        this.chain = chain;
        this.muleContext = muleContext;
    }

    /**
     * Sets muleContext
     * 
     * @param value
     *            Value to set
     */
    public void setMuleContext(MuleContext value)
    {
        this.muleContext = value;
    }

    /**
     * Sets chain
     * 
     * @param value
     *            Value to set
     */
    public void setChain(MessageProcessor value)
    {
        this.chain = value;
    }

    /**
     * Sets event
     * 
     * @param value
     *            Value to set
     */
    public void setEvent(MuleEvent value)
    {
        this.event = value;
    }

    public Object process()
            throws Exception
    {
        MuleEvent muleEvent;
        muleEvent = new DefaultMuleEvent(event.getMessage(), event);
        return chain.process(muleEvent).getMessage().getPayload();
    }

    public Object process(Object payload)
            throws Exception
    {
        MuleMessage muleMessage;
        muleMessage = new DefaultMuleMessage(payload, muleContext);
        MuleEvent muleEvent;
        muleEvent = new DefaultMuleEvent(muleMessage, event);
        return chain.process(muleEvent).getMessage().getPayload();
    }

    public Object processWithExtraProperties(Map<String, Object> properties)
            throws Exception
    {
        MuleMessage muleMessage;
        muleMessage = event.getMessage();
        for (String property : properties.keySet())
        {
            muleMessage.setInvocationProperty(property, properties.get(property));
        }
        MuleEvent muleEvent;
        muleEvent = new DefaultMuleEvent(muleMessage, event);
        return chain.process(muleEvent).getMessage().getPayload();
    }

    public Object process(Object payload, Map<String, Object> properties)
            throws Exception
    {
        MuleMessage muleMessage;
        muleMessage = new DefaultMuleMessage(payload, muleContext);
        for (String property : properties.keySet())
        {
            muleMessage.setInvocationProperty(property, properties.get(property));
        }
        MuleEvent muleEvent;
        muleEvent = new DefaultMuleEvent(muleMessage, event);
        return chain.process(muleEvent).getMessage().getPayload();
    }

}
