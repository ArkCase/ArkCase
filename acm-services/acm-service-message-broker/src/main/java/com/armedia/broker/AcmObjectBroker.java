package com.armedia.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import org.springframework.jms.JmsException;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.io.Serializable;

/**
 * Generic ACM ActiveMQ object broker
 * 
 * @author dame.gjorgjievski
 *
 * @param <E>
 */
public abstract class AcmObjectBroker<E extends Serializable> extends DefaultMessageListenerContainer
{
    private static final Logger LOG = Logger.getLogger(AcmObjectBroker.class);

    protected final Class<E> entityClass;
    protected final ActiveMQQueue outboundQueue;
    protected final ActiveMQQueue inboundQueue;

    protected final AcmObjectBrokerListener<E> listener;
    protected AcmObjectBrokerHandler<E> handler;

    protected final ActiveMQConnectionFactory connectionFactory;
    protected JmsTemplate producerTemplate;

    public AcmObjectBroker(ActiveMQConnectionFactory connectionFactory, String outboundQueue, String inboundQueue, Class<E> entityClass)
    {
        this.connectionFactory = connectionFactory;
        this.outboundQueue = outboundQueue != null ? new ActiveMQQueue(outboundQueue) : null;
        this.inboundQueue = inboundQueue != null ? new ActiveMQQueue(inboundQueue) : null;
        this.entityClass = entityClass;
        this.listener = new AcmObjectBrokerListener<E>(this);

        init();
    }

    /**
     * Initialize inbound and outbound queues
     */
    private final void init()
    {
        setConnectionFactory(connectionFactory);
        setMessageListener(listener);

        if (inboundQueue != null)
        {
            setDestination(inboundQueue);
        }
        if (outboundQueue != null)
        {
            CachingConnectionFactory cachedConnectionFactory = new CachingConnectionFactory(connectionFactory);
            producerTemplate = new JmsTemplate(cachedConnectionFactory);
            producerTemplate.setDefaultDestination(outboundQueue);
        }
    }

    /**
     * Send object to outbound queue
     * 
     * @param entity
     * @throws JsonProcessingException
     */
    public void sendObject(Object entity) throws JsonProcessingException, JmsException
    {
        if (producerTemplate == null)
        {
            throw new RuntimeException("No outbound queue is specified for sending messages");
        }
        String message = new ObjectMapper().writeValueAsString(entity);
        producerTemplate.send(new MessageCreator()
        {
            @Override
            public Message createMessage(Session session) throws JMSException
            {
                TextMessage msg = session.createTextMessage(message);
                LOG.info("Sending message " + message);
                return msg;
            }
        });
    }

    /**
     * Get entity class associated with broker
     * 
     * @return
     */
    public Class<E> getEntityClass()
    {
        return entityClass;
    }

    /**
     * Get current broker URL
     * 
     * @return
     */
    public String getBrokerURL()
    {
        return connectionFactory.getBrokerURL();
    }

    /**
     * Set object handler
     * 
     * @param handler
     */
    public void setHandler(AcmObjectBrokerHandler<E> handler)
    {
        this.handler = handler;
    }

    /**
     * Get object handler
     * 
     * @param handler
     */
    protected AcmObjectBrokerHandler<E> getHandler()
    {
        return handler;
    }

}
