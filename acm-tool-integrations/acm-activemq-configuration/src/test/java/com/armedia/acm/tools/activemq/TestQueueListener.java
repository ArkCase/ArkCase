package com.armedia.acm.tools.activemq;

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

    private AtomicInteger received = new AtomicInteger(0);

    @Override
    @JmsListener(destination = "testQueue.in")
    public void onMessage(Message message)
    {
        Integer sofar = received.incrementAndGet();
        log.info("got message # {}", sofar);

    }
}
