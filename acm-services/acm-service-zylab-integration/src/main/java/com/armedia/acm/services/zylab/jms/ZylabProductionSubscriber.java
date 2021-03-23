package com.armedia.acm.services.zylab.jms;

/*-
 * #%L
 * Tool Integrations: Arkcase ZyLAB Integration
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.tool.zylab.model.ZylabProductionFileIncomingEvent;
import com.armedia.acm.tool.zylab.model.ZylabProductionSyncDTO;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on February, 2021
 */
public class ZylabProductionSubscriber implements ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;
    private ObjectConverter objectConverter;

    /**
     *
     * Subscribes to messages and publishes a ZylabProductionFileIncoming event to be handled by listeners
     *
     * @param message Message from ZyLAB containing matter ID and production key
     */
    @JmsListener(destination = "zylab-arkcase-integration", containerFactory = "jmsListenerContainerFactory")
    public void onNewProductionSync(Message message)
    {
        log.info("New ZyLAB production sync message received, [{}]", message.getPayload());

        String messagePayload = message.getPayload().toString();
        ZylabProductionSyncDTO productionSyncDTO = objectConverter.getJsonUnmarshaller().unmarshall(messagePayload,
                ZylabProductionSyncDTO.class);

        if (productionSyncDTO != null && productionSyncDTO.getMatterId() > 0 && !productionSyncDTO.getProductionKey().isEmpty())
        {
            ZylabProductionFileIncomingEvent event = new ZylabProductionFileIncomingEvent(productionSyncDTO);
            applicationEventPublisher.publishEvent(event);
        }
        else
        {
            log.error("Improper ZyLAB production sync message received, [{}]", message.getPayload());
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}
