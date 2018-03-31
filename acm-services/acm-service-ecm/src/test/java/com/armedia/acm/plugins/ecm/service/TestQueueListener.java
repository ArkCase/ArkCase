package com.armedia.acm.plugins.ecm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.Message;
import javax.jms.MessageListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dmiller on 5/5/16.
 */
public class TestQueueListener implements MessageListener
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private SlowWebConsumer slowWebConsumer;

    private AtomicInteger received = new AtomicInteger(0);

    @Override
    @JmsListener(destination = "slowConsumerQueue.in")
    public void onMessage(Message message)
    {
        Integer sofar = received.incrementAndGet();
        log.info("got message # {}", sofar);

        try
        {
            slowWebConsumer.sendToSolr("message " + sofar);
        }
        catch (SlowConsumerException e)
        {
            log.error("Slow consumer {}", e.getMessage(), e);
        }
    }

    public SlowWebConsumer getSlowWebConsumer()
    {
        return slowWebConsumer;
    }

    public void setSlowWebConsumer(SlowWebConsumer slowWebConsumer)
    {
        this.slowWebConsumer = slowWebConsumer;
    }
}
