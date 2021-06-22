package com.armedia.acm.services.zylab.jms;

/*-
 * #%L
 * ACM Service: Arkcase ZyLAB Integration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.tool.zylab.model.ZylabProductionSyncDTO;

public class ZylabProductionSyncStatusToJmsSender implements InitializingBean
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private ObjectConverter objectConverter;
    private ConnectionFactory jmsConnectionFactory;
    private JmsTemplate jmsTemplate;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        jmsTemplate = new JmsTemplate(getJmsConnectionFactory());
    }

    public void sendProductionSyncStatus(ZylabProductionSyncDTO zylabProductionSyncDTO)
    {
        sendToJmsQueue(zylabProductionSyncDTO, "arkcase-zylab-queue");
    }

    private void sendToJmsQueue(ZylabProductionSyncDTO zylabProductionSyncDTO, String queueName)
    {
        try
        {
            String json = objectConverter.getJsonMarshaller().marshal(zylabProductionSyncDTO);

            getJmsTemplate().convertAndSend(queueName, json);

            log.debug("Production sync status sent to JMS queue: {}. JSON: {}", queueName, json);
        }
        catch (JmsException e)
        {
            log.error("Could not send production sync status to JMS queue: {}, {}", queueName, e.getMessage(), e);
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
