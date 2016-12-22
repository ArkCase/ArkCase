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
public class AcmObjectBrokerClient<E> extends DefaultMessageListenerContainer
{
    private static final Logger LOG = LogManager.getLogger(AcmObjectBrokerClient.class);

    private final Class<E> entityClass;

    private final Queue outboundQueue;
    private final Queue inboundQueue;

    private ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    private ObjectConverter converter = ObjectConverter.createJSONConverter();
    private AcmObjectBrokerClientHandler<E> handler;
    private JmsTemplate producerTemplate;

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
        if (inboundQueue != null)
        {
            setDestination(inboundQueue);
        }

        CachingConnectionFactory cachedConnectionFactory = new CachingConnectionFactory(getConnectionFactory());
        producerTemplate = new JmsTemplate(cachedConnectionFactory);
        if (outboundQueue != null)
        {
            producerTemplate.setDefaultDestination(outboundQueue);
        }

        executor.setBeanName("AcmObjectBrokerClientExecutor");
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.initialize();
    }

    /**
     * Send entity to default outbound queue(s)
     * 
     * @param entity
     * @throws JsonProcessingException
     * @throws JMSException
     */
    public void sendEntity(E entity) throws JsonProcessingException, JMSException
    {
        sendObject(entity);
    }

    /**
     * Send object to default outbound queue(s)
     * 
     * @param entity
     * @throws JsonProcessingException
     * @throws JMSException
     */
    public void sendObject(Object object) throws JsonProcessingException, JMSException
    {
        if (outboundQueue == null)
        {
            throw new IllegalStateException("No default destination queue is specified");
        }
        sendObject(outboundQueue.getQueueName(), object);
    }

    /**
     * Send entity to given outbound queue
     * 
     * @param destination
     * @param entity
     * @throws JsonProcessingException
     */
    public void sendEntity(String destination, E entity) throws JsonProcessingException, JmsException
    {
        sendObject(destination, entity);
    }

    /**
     * Send object to given outbound queue
     * 
     * @param destination
     * @param object
     * @throws JsonProcessingException
     */
    public void sendObject(String destination, Object object) throws JsonProcessingException, JmsException
    {
        String message = getConverter().getMarshaller().marshal(object);
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
     * Set object converter
     * 
     * @return
     */
    public void setConverter(ObjectConverter converter)
    {
        this.converter = converter;
    }

    /**
     * Get broker client task executor
     * 
     * @return
     */
    protected ThreadPoolTaskExecutor getExecutor()
    {
        return executor;
    }

    /**
     * Set broker client task executor
     * 
     * @param executor
     */
    public void setExecutor(ThreadPoolTaskExecutor executor)
    {
        if (this.executor != null)
        {
            this.executor.shutdown();
        }
        this.executor = executor;
    }

    /**
     * Set maximum number of concurrent worker threads
     * 
     * @param num
     */
    public void setMaxConcurrentWorkers(int num)
    {
        if (executor != null)
        {
            executor.setCorePoolSize(num);
        }
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
