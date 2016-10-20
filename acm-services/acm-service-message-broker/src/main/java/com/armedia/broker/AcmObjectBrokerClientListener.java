package com.armedia.broker;

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

            E entity = broker.getConverter().getUnmarshaller().unmarshall(msg.getText(), broker.getEntityClass());

            if (entity == null)
            {
                throw new IOException("Failed to deserialize object from " + msg.getText().substring(0, 100));
            }

            LOG.debug("Consumed entity object: " + msg.getText().substring(0, 100));

            broker.getExecutor().execute(createEntityHandlerTask(broker, entity, message));

        } catch (JMSException | IOException e)
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

                    } catch (JMSException e)
                    {
                        LOG.error("Failed to aknowledge message " + message, e);
                    }
                }
            }
        };
    }

}
