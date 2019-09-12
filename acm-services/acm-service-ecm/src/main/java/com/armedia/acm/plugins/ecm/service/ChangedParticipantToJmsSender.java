package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: ECM
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

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.ecm.model.ChangedParticipant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

/**
 * @author ivana.shekerova on 03/04/2019.
 */
public class ChangedParticipantToJmsSender implements InitializingBean
{
    private static final Logger log = LogManager.getLogger(ChangedParticipantToJmsSender.class);
    private ObjectConverter objectConverter;
    private ConnectionFactory jmsConnectionFactory;
    private JmsTemplate jmsTemplate;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        jmsTemplate = new JmsTemplate(getJmsConnectionFactory());
    }

    public void sendChangedParticipant(ChangedParticipant changedParticipant)
    {
        sendToJmsQueue(changedParticipant, "com.armedia.acm.v1.arkcaseParticipantChange.queue");
    }

    private void sendToJmsQueue(ChangedParticipant changedParticipant, String queueName)
    {
        try
        {
            String json = objectConverter.getJsonMarshaller().marshal(changedParticipant);

            getJmsTemplate().convertAndSend(queueName, json);

            log.debug("Changed participant sent to JMS queue: {}. JSON: {}", queueName, json);

        }
        catch (JmsException e)
        {
            log.error("Could not send changed participants to JMS queue: {}, {}", queueName, e.getMessage(), e);
        }
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public ConnectionFactory getJmsConnectionFactory()
    {
        return jmsConnectionFactory;
    }

    public void setJmsConnectionFactory(ConnectionFactory jmsConnectionFactory)
    {
        this.jmsConnectionFactory = jmsConnectionFactory;
    }

    public JmsTemplate getJmsTemplate()
    {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate)
    {
        this.jmsTemplate = jmsTemplate;
    }

}
