package com.armedia.acm.tools.activemq.service;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTempQueue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueSession;
import javax.jms.Session;

import java.util.Enumeration;

public class MessageCounter
{

    public long countMessages(ActiveMQConnection connection, String destinationType, String destinationName) throws JMSException
    {

        if ("Queue".equals(destinationType))
        {
            for (ActiveMQQueue queue : connection.getDestinationSource().getQueues())
            {
                if (queue.getQueueName().equals(destinationName))
                {
                    return countFromQueue(connection, queue);
                }
            }

        }
        else if ("TempQueue".equals(destinationType))
        {

            for (ActiveMQTempQueue queue : connection.getDestinationSource().getTemporaryQueues())
            {
                if (queue.getQueueName().equals(destinationName))
                {
                    return countFromQueue(connection, queue);

                }
            }
        }

        return 0L;
    }

    public long countFromQueue(ActiveMQConnection connection, Queue queue) throws JMSException
    {
        QueueSession queueSession = null;
        QueueBrowser browser = null;
        try
        {

            queueSession = connection.createQueueSession(true,
                    Session.SESSION_TRANSACTED);
            browser = queueSession.createBrowser(queue);
            long count = 0;
            Enumeration<Message> messageEnumeration = browser.getEnumeration();
            while (messageEnumeration.hasMoreElements())
            {
                count++;
                messageEnumeration.nextElement();
            }
            return count;
        }
        finally
        {
            if (browser != null)
            {
                browser.close();
            }
            if (queueSession != null)
            {
                queueSession.close();
            }
        }

    }

}
