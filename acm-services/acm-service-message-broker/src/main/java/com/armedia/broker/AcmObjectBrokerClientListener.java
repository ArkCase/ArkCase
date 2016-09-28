package com.armedia.broker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import java.io.IOException;
import java.io.Serializable;

/**
 * ACM Object Message Listener
 * 
 * @author dame.gjorgjievski
 *
 */
public class AcmObjectBrokerClientListener<E extends Serializable> implements MessageListener
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
            LOG.info("Consumed entity object: " + msg.getText());
            E entity = broker.getMapper().readValue(msg.getText(), broker.getEntityClass());

            if (broker.getHandler().handleObject(entity))
            {
                message.acknowledge();
            }

        } catch (JMSException | IOException e)
        {
            LOG.error("Failed to consume/deserialize message " + message, e);
        }
    }

}
