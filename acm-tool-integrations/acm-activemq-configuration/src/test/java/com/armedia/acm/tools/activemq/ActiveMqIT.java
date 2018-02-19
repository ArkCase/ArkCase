package com.armedia.acm.tools.activemq;

import static org.junit.Assert.assertNotNull;

import org.apache.activemq.EnhancedConnection;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-mule-activemq.xml",
        "/spring/spring-mule-activemq-test.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
})
/**
 * To see ActiveMQ flow control happening in this test, ensure the queue memory limit is set to "1 mb" in the
 * spring-mule-activemq.xml file. Then you will see output pause, and after a few seconds restart.
 */
public class ActiveMqIT
{
    public transient final Logger log = LoggerFactory.getLogger(getClass());

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
                catch (JMSException e)
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

        for (int a = 1; a < 500; a++)
        {

            log.info("Sending message # {}", a);
            template.convertAndSend(destination, largeMessage);
        }

    }

    private void createTopicListener(Session session, Topic topic) throws JMSException
    {
        MessageConsumer consumerAdvisory = session.createConsumer(topic);
        consumerAdvisory.setMessageListener(message -> {
            log.debug("Got a message on an advisory topic");
            if (message instanceof ActiveMQMessage)
            {
                ActiveMQMessage activeMQMessage = (ActiveMQMessage) message;
                log.debug("Destination: {}, data structure: {}",
                        activeMQMessage.getDestination().getPhysicalName(),
                        activeMQMessage.getDataStructure() == null ? "null" : activeMQMessage.getDataStructure().getClass().getName());

            }

        });
    }

}
