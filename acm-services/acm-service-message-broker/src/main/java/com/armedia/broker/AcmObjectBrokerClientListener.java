package com.armedia.broker;

/*-
 * #%L
 * ACM Service: JMS Message Broker
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

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import java.io.IOException;

/**
 * ACM Object Message Listener
 * 
 * @author dame.gjorgjievski
 *
 */
public class AcmObjectBrokerClientListener<E> implements MessageListener
{

    private static final Logger LOG = LogManager.getLogger(AcmObjectBrokerClientListener.class);
    private final AcmObjectBrokerClient<E> broker;
    private AcmUnmarshaller jsonUnmarshaller = ObjectConverter.createJSONUnmarshallerForTests();

    public AcmObjectBrokerClientListener(AcmObjectBrokerClient<E> broker)
    {
        this.broker = broker;
    }

    @Override
    public void onMessage(Message message)
    {
        try
        {
            TextMessage msg = (TextMessage) message;

            if (msg.getText().isEmpty())
            {
                throw new IOException("Cannot consume empty message");
            }

            E entity = jsonUnmarshaller.unmarshall(msg.getText(), broker.getEntityClass());

            if (entity == null)
            {
                throw new IOException("Failed to deserialize object from " + msg.getText().substring(0, 100));
            }

            LOG.debug("Consumed entity object: " + msg.getText().substring(0, 100));

            broker.getExecutor().execute(createEntityHandlerTask(broker, entity, message));

        }
        catch (JMSException | IOException e)
        {
            LOG.error("Failed to consume/deserialize message " + message, e);
        }
    }

    /**
     * Create entity handler task
     * 
     * @param broker
     * @param entity
     * @param message
     * @return
     */
    private Runnable createEntityHandlerTask(AcmObjectBrokerClient<E> broker, E entity, Message message)
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                if (broker == null || message == null || entity == null)
                {
                    return;
                }

                if (broker.getHandler().handleObject(entity))
                {
                    try
                    {
                        message.acknowledge();

                    }
                    catch (JMSException e)
                    {
                        LOG.error("Failed to aknowledge message " + message, e);
                    }
                }
            }
        };
    }

}
