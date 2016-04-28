package com.armedia.acm.tools.activemq;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
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

import javax.jms.Message;
import javax.jms.Session;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-mule-activemq.xml"
})
public class ActiveMqIT
{
    public transient final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PooledConnectionFactory connectionFactory;


    @Autowired
    @Qualifier("broker")
    private BrokerService broker;

    @Before
    public void setUp()
    {
        assertNotNull(connectionFactory);

        assertNotNull(broker);
        log.debug("broker class: {}", broker.getClass().getName());

        log.debug("start connection factory");
        connectionFactory.start();
    }

    @After
    public void tearDown()
    {
        assertNotNull(connectionFactory);

        log.debug("stop connection factory");
        connectionFactory.stop();
    }

    @Test
    public void sendMessages() throws Exception
    {
        int maxConnections = connectionFactory.getMaxConnections();
        log.info("maxConnections {}", maxConnections);
        log.info("connection factory: {}", connectionFactory);

        String destination = "testQueue.in";


        JmsTemplate template = new JmsTemplate(connectionFactory);
        JmsTemplate receive = new JmsTemplate(connectionFactory);
        template.setDeliveryPersistent(false);


        template.setReceiveTimeout(500L);
        template.setExplicitQosEnabled(true);
        template.setTimeToLive(500L);
        log.info("Transacted? {}", template.isSessionTransacted());
        template.setSessionTransacted(true);

        receive.setReceiveTimeout(500L);

        log.info("session acknowlege mode: {}", template.getSessionAcknowledgeMode());
        log.info("auto: {}", Session.AUTO_ACKNOWLEDGE);
        log.info("client: {}", Session.CLIENT_ACKNOWLEDGE);
        log.info("transact: {}", Session.SESSION_TRANSACTED);
        log.info("dups ok: {}", Session.DUPS_OK_ACKNOWLEDGE);
        template.setSessionAcknowledgeMode(Session.DUPS_OK_ACKNOWLEDGE);

        template.convertAndSend(destination, "test message");


        Message received = receive.receive(destination);
        log.info("received message: {}", received);

        assertNotNull(received);


        Message second = receive.receive(destination);

        assertNull(second);

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


            assertNotNull(broker.getBroker().getDestinations());


            for (Map.Entry<ActiveMQDestination, Destination> dest : broker.getBroker().getDestinationMap().entrySet())
            {
                if (dest.getValue().getName().equals(destination))
                {
                    log.info("GC idle interval: {}", dest.getValue().getInactiveTimeoutBeforeGC());
                    log.info("Destination queue size: {}", dest.getValue().getDestinationStatistics().getMessages().getCount());
                    log.info("Desigination queue dequeues: {}", dest.getValue().getDestinationStatistics().getDequeues().getCount());
                    log.info("Destination queue expired {}: ", dest.getValue().getDestinationStatistics().getExpired().getCount());
                    log.info("Destinatin queue in flight: {}", dest.getValue().getDestinationStatistics().getInflight().getCount());
                    log.info("Destination queue enqueues: {}", dest.getValue().getDestinationStatistics().getEnqueues().getCount());
                }
            }

//
            Message found = receive.receive(destination);


            Thread.sleep(500);


            assertNotNull(found);
            found.acknowledge();


        }

    }
}
