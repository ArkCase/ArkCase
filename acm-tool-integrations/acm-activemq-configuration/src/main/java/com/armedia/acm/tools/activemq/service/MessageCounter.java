package com.armedia.acm.tools.activemq.service;

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
