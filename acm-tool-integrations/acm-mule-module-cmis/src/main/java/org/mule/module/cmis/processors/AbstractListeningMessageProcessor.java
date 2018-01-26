
package org.mule.module.cmis.processors;

import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.MessageExchangePattern;
import org.mule.RequestContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.callback.SourceCallback;
import org.mule.api.processor.MessageProcessor;

import javax.annotation.Generated;

import java.util.Map;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public abstract class AbstractListeningMessageProcessor<O>
        extends AbstractMessageProcessor<O>
        implements SourceCallback
{

    /**
     * Message processor that will get called for processing incoming events
     * 
     */
    private MessageProcessor messageProcessor;

    /**
     * Retrieves messageProcessor
     * 
     */
    public MessageProcessor getMessageProcessor()
    {
        return this.messageProcessor;
    }

    /**
     * Sets the message processor that will "listen" the events generated by this message source
     * 
     * @param listener
     *            Message processor
     */
    public void setListener(MessageProcessor listener)
    {
        this.messageProcessor = listener;
    }

    /**
     * Implements {@link SourceCallback#process(org.mule.api.MuleEvent)}. This message source will be passed on to the
     * actual pojo's method as a callback mechanism.
     * 
     */
    public Object process(Object message)
            throws Exception
    {
        MuleMessage muleMessage;
        muleMessage = new DefaultMuleMessage(message, getMuleContext());
        MuleEvent muleEvent;
        muleEvent = new DefaultMuleEvent(muleMessage, MessageExchangePattern.ONE_WAY, getFlowConstruct());
        try
        {
            MuleEvent responseEvent;
            responseEvent = messageProcessor.process(muleEvent);
            if ((responseEvent != null) && (responseEvent.getMessage() != null))
            {
                return responseEvent.getMessage().getPayload();
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        return null;
    }

    /**
     * Implements {@link SourceCallback#process(org.mule.api.MuleEvent)}. This message source will be passed on to the
     * actual pojo's method as a callback mechanism.
     * 
     */
    public Object process(Object message, Map<String, Object> properties)
            throws Exception
    {
        MuleMessage muleMessage;
        muleMessage = new DefaultMuleMessage(message, properties, null, null, getMuleContext());
        MuleEvent muleEvent;
        muleEvent = new DefaultMuleEvent(muleMessage, MessageExchangePattern.ONE_WAY, getFlowConstruct());
        try
        {
            MuleEvent responseEvent;
            responseEvent = messageProcessor.process(muleEvent);
            if ((responseEvent != null) && (responseEvent.getMessage() != null))
            {
                return responseEvent.getMessage().getPayload();
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        return null;
    }

    /**
     * Implements {@link SourceCallback#process()}. This message source will be passed on to the actual pojo's method as
     * a callback mechanism.
     * 
     */
    public Object process()
            throws Exception
    {
        try
        {
            MuleEvent responseEvent;
            responseEvent = messageProcessor.process(RequestContext.getEvent());
            if ((responseEvent != null) && (responseEvent.getMessage() != null))
            {
                return responseEvent.getMessage().getPayload();
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        return null;
    }

    /**
     * Implements {@link SourceCallback#processEvent(org.mule.api.MuleEvent)}. This message source will be passed on to
     * the actual pojo's method as a callback mechanism.
     * 
     */
    public MuleEvent processEvent(MuleEvent event)
            throws MuleException
    {
        return messageProcessor.process(event);
    }

}
