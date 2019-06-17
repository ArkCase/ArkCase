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

import com.armedia.acm.objectonverter.AcmMarshaller;
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

    private Class entityClass;

    private final Queue outboundQueue;
    private final Queue inboundQueue;

    private ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    // private ObjectConverter converter = ObjectConverter.createJSONConverter();
    private AcmMarshaller jsonMarshaller = ObjectConverter.createJSONMarshallerForTests();

    private AcmObjectBrokerClientHandler<E> handler;
    private JmsTemplate producerTemplate;

    public AcmObjectBrokerClient(ConnectionFactory connectionFactory, String outboundQueue, String inboundQueue)
    {
        this.outboundQueue = outboundQueue != null ? getQueue(outboundQueue) : null;
        this.inboundQueue = inboundQueue != null ? getQueue(inboundQueue) : null;

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
        String message = jsonMarshaller.marshal(object);
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
        this.entityClass = getEntity() != null ? getEntity().getClass() : null;
        return entityClass;
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
     * Set object handler
     *
     * @param handler
     */
    public void setHandler(AcmObjectBrokerClientHandler<E> handler)
    {
        this.handler = handler;
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

    // Dynamically get the necessary entity define in spring-extension-library-message-broker.xml
    // through <beans:bean id="portalEntity" class="gov.foia.model.PortalFOIARequest"/>
    public E getEntity()
    {
        return null;
    }

}
