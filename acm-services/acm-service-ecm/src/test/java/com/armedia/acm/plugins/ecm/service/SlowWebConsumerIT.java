package com.armedia.acm.plugins.ecm.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.ConnectionFactory;

import java.util.stream.IntStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-slow-web-consumer.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml" })

/*
 * "/spring/spring-mule-activemq.xml",
 * "/spring/spring-mule-activemq-test.xml",
 * "/spring/spring-library-property-file-manager.xml",
 * "/spring/spring-library-acm-encryption.xml"
 */
public class SlowWebConsumerIT
{
    private transient final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SlowWebConsumer slowWebConsumer;

    @Autowired
    @Qualifier("jmsConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Test
    public void slowWebConsumer() throws Exception
    {
        assertNotNull(slowWebConsumer);
        assertNotNull(connectionFactory);

        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);

        IntStream.rangeClosed(1, 10).forEach(i -> {
            jmsTemplate.convertAndSend("slowConsumerQueue.in", "message " + i);
            // try
            // {
            // logger.info("Sending message {}", i);
            // //slowWebConsumer.sendToSolr("Message " + i);
            //
            // }
            // catch (SlowConsumerException e)
            // {
            // logger.error("Got a slow consumer exception: {}", e.getMessage(), e);
            // }
        });

        logger.info("Waiting 60 seconds for messages to finish.");
        Thread.sleep(60000);
    }
}
