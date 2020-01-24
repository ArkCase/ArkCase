package com.armedia.acm.tools.activemq;

/*-
 * #%L
 * Tool Integrations: ActiveMQ Configuration
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

import static org.junit.Assert.assertNotNull;

import com.armedia.acm.tools.activemq.service.MessageCounter;

import org.apache.activemq.EnhancedConnection;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
})
/**
 * To see ActiveMQ flow control happening in this test, ensure the queue memory limit is set to "1 mb" in the
 * spring-library-activemq.xml file. Then you will see output pause, and after a few seconds restart.
 */
public class ActiveMqIT
{
    public transient final Logger log = LogManager.getLogger(getClass());

    private final MessageCounter messageCounter = new MessageCounter();

    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    @Autowired
    @Qualifier("jmsConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Before
    public void setUp()
    {
        assertNotNull(connectionFactory);

        log.debug("start connection factory");
        ((PooledConnectionFactory) connectionFactory).start();
    }

    @After
    public void tearDown()
    {
        assertNotNull(connectionFactory);

        log.debug("stop connection factory");
        ((PooledConnectionFactory) connectionFactory).stop();
    }

    @Test
    public void sendMessages() throws Exception
    {
        String destination = "testQueue.in";

        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDeliveryPersistent(true);
        template.setReceiveTimeout(500L);
        template.setExplicitQosEnabled(true);
        template.setTimeToLive(500L);

        Connection c = connectionFactory.createConnection();
        Session session = c.createSession(true, Session.SESSION_TRANSACTED);
        Queue testQueue = session.createQueue(destination);

        c.start();

        if (c instanceof EnhancedConnection)
        {
            EnhancedConnection amqConn = (EnhancedConnection) c;

            log.info("# of queues: {}, # of topics: {}, # of temp queues: {}, # of temp topics: {}",
                    amqConn.getDestinationSource().getQueues().size(),
                    amqConn.getDestinationSource().getTemporaryQueues().size(),
                    amqConn.getDestinationSource().getTopics().size(),
                    amqConn.getDestinationSource().getTemporaryTopics().size());
            amqConn.getDestinationSource().getQueues().forEach(q -> {
                try
                {
                    ActiveMQTopic fastTopic = AdvisorySupport.getFastProducerAdvisoryTopic(q);
                    createTopicListener(session, fastTopic);

                    ActiveMQTopic fulltopic = AdvisorySupport.getFullAdvisoryTopic(q);
                    createTopicListener(session, fulltopic);

                    ActiveMQTopic slowTopic = AdvisorySupport.getSlowConsumerAdvisoryTopic(q);
                    createTopicListener(session, slowTopic);

                    log.info("created fast producer, slow consumer, and full advisory topics for queue: {}",
                            q.getQueueName());
                }
                catch (Exception e)
                {
                    log.error("Could not get queue name, {}", e.getMessage(), e);
                }
            });
            amqConn.getDestinationSource().getTemporaryQueues().forEach(q -> {
                try
                {
                    log.info("temp queue: " + q.getQueueName());
                }
                catch (JMSException e)
                {
                    log.error("Could not get temp queue name, {}", e.getMessage(), e);
                }
            });

        }
        else
        {
            log.info("Connection is not an EnhancedConnection, but is {}", c.getClass().getName());
        }

        String base = "Grateful Dead";
        String kb500message = base;
        for (int a = 0; a < ((1024 * 1024) / 24); a++)
        {
            kb500message += base;
        }

        log.info("Size of message: {}", kb500message.length());

        final String largeMessage = kb500message;

        for (int a = 1; a < 100; a++)
        {

            log.info("Sending message # {}", a);
            template.convertAndSend(destination, largeMessage);
        }

    }

    private void createTopicListener(Session session, Topic topic) throws Exception
    {
        MessageConsumer consumerAdvisory = session.createConsumer(topic);
        final Pattern p = Pattern.compile("ActiveMQ\\.Advisory\\.([A-Za-z]+)\\.([A-Za-z]+)\\.(.*)");
        consumerAdvisory.setMessageListener(message -> {
            log.debug("Got a message on an advisory topic");
            if (message instanceof ActiveMQMessage)
            {
                ActiveMQMessage activeMQMessage = (ActiveMQMessage) message;
                log.debug("Destination: {}, data structure: {}, # of properties: {}",
                        activeMQMessage.getDestination().getPhysicalName(),
                        activeMQMessage.getDataStructure() == null ? "null" : activeMQMessage.getDataStructure().getClass().getName(),
                        activeMQMessage.getDestination().getProperties().size());

                final String destination = activeMQMessage.getDestination().getPhysicalName();
                log.debug("destination: {}", destination);
                final Matcher matcher = p.matcher("ActiveMQ.Advisory.FULL.Queue.testQueue.in");

                if (matcher.matches())
                {

                    final String advisoryType = matcher.group(1);
                    final String destinationType = matcher.group(2);
                    final String targetName = matcher.group(3);

                    log.debug("Advisory: {}, destination: {}, target: {}", advisoryType, destinationType, targetName);

                    long count = 0;
                    try
                    {
                        count = messageCounter.countMessages(activeMQMessage.getConnection(), destinationType, targetName);
                    }
                    catch (Exception e)
                    {
                        log.error("could not count messages: {}", e.getMessage(), e);
                    }
                    log.debug("# of messages in queue now: {}", count);
                }
            }

        });
    }

}
