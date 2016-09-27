package com.armedia.broker;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;

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
public class AcmObjectBrokerListener<E extends Serializable> implements MessageListener
{

    private static final Logger LOG = Logger.getLogger(AcmObjectBrokerListener.class);

    private final AcmObjectBroker<E> broker;

    public AcmObjectBrokerListener(AcmObjectBroker<E> broker)
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

            E entity = new ObjectMapper().readValue(msg.getText(), broker.getEntityClass());
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
