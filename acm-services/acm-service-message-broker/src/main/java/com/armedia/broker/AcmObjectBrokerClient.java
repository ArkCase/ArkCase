package com.armedia.broker;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.JmsException;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Generic JMS object broker client
 * 
 * @author dame.gjorgjievski
 *
 * @param <E>
 */
public abstract class AcmObjectBrokerClient<E> extends DefaultMessageListenerContainer
{
    private static final Logger LOG = LogManager.getLogger(AcmObjectBrokerClient.class);

    protected final Class<E> entityClass;

    protected final Queue outboundQueue;
    protected final Queue inboundQueue;

    protected final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    protected ObjectConverter converter = ObjectConverter.createJSONConverter();
    protected AcmObjectBrokerClientHandler<E> handler;
    protected JmsTemplate producerTemplate;

    public AcmObjectBrokerClient(ConnectionFactory connectionFactory, String outboundQueue, String inboundQueue, Class<E> entityClass)
    {

        this.outboundQueue = outboundQueue != null ? getQueue(outboundQueue) : null;
        this.inboundQueue = inboundQueue != null ? getQueue(inboundQueue) : null;
        this.entityClass = entityClass;

        setConnectionFactory(connectionFactory);
        setMessageListener(new AcmObjectBrokerClientListener<E>(this));

        init();
    }

    /**
     * Initialize async executor, inbound and outbound queues
     */
    private final void init()
    {
        executor.setBeanName("AcmObjectBrokerClientExecutor");
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.initialize();

        if (inboundQueue != null)
        {
            setDestination(inboundQueue);
        }
        if (outboundQueue != null)
        {
            CachingConnectionFactory cachedConnectionFactory = new CachingConnectionFactory(getConnectionFactory());
            producerTemplate = new JmsTemplate(cachedConnectionFactory);
            producerTemplate.setDefaultDestination(outboundQueue);
        }
    }

    /**
     * Send object to default outbound queue(s)
     * 
     * @param entity
     * @throws JsonProcessingException
     * @throws JMSException
     */
    public void sendObject(E entity) throws JsonProcessingException, JMSException
    {
        sendObject(outboundQueue.getQueueName(), entity);
    }

    /**
     * Send object to given outbound queue
     * 
     * @param destination
     * @param entity
     * @throws JsonProcessingException
     */
    public void sendObject(String destination, E entity) throws JsonProcessingException, JmsException
    {
        if (producerTemplate == null)
        {
            throw new IllegalStateException("No outbound queue is specified for sending messages");
        }
        String message = getConverter().getMarshaller().marshal(entity);
        producerTemplate.send(destination, new MessageCreator()
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
     * Set object handler
     * 
     * @param handler
     */
    public void setHandler(AcmObjectBrokerClientHandler<E> handler)
    {
        this.handler = handler;
    }

    /**
     * Get object handler
     * 
     * @param handler
     */
    protected AcmObjectBrokerClientHandler<E> getHandler()
    {
        return handler;
    }

    /**
     * Get object converter
     * 
     * @return
     */
    protected ObjectConverter getConverter()
    {
        return converter;
    }

    /**
     * Get broker client task executor
     * 
     * @return
     */
    public ThreadPoolTaskExecutor getExecutor()
    {
        return executor;
    }

    /**
     * Get queue for given name
     * 
     * @param queueName
     * @return
     */
    private Queue getQueue(String queueName)
    {
        return new Queue()
        {
            @Override
            public String getQueueName() throws JMSException
            {
                return queueName;
            }
        };
    }

}
