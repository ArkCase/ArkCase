package com.armedia.acm.services.zylab.service;

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
import org.springframework.security.core.Authentication;

import com.armedia.acm.services.zylab.model.ZylabMatterCreatedEvent;
import com.armedia.acm.services.zylab.model.ZylabProductionSyncEvent;
import com.armedia.acm.services.zylab.model.ZylabProductionSyncStatus;
import com.armedia.acm.tool.zylab.model.ZylabProductionSyncDTO;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on February, 2021
 */
public class ZylabEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void publishMatterCreatedEvent(ZylabMatterCreatedEvent zylabMatterCreatedEvent)
    {
        log.debug("Publishing ZylabMatterCreated event");
        applicationEventPublisher.publishEvent(zylabMatterCreatedEvent);
    }

    public void publishProductionSucceededEvent(Object source, Long objectId, String objectType, Long matterId, String productionKey,
            Authentication auth)
    {
        log.debug("Publishing ZylabProductionSync succeeded event");

        ZylabProductionSyncDTO zylabProductionSyncDTO = new ZylabProductionSyncDTO();

        zylabProductionSyncDTO.setProductionKey(productionKey);
        zylabProductionSyncDTO.setMatterId(matterId);
        zylabProductionSyncDTO.setStatus(ZylabProductionSyncStatus.INGESTED.name());

        ZylabProductionSyncEvent zylabProductionSyncEvent = new ZylabProductionSyncEvent(source, objectId, objectType,
                zylabProductionSyncDTO, true, auth);
        applicationEventPublisher.publishEvent(zylabProductionSyncEvent);
    }

    public void publishProductionFailedEvent(Object source, Long objectId, String objectType, Long matterId, String productionKey,
            String error,
            Authentication auth)
    {
        log.debug("Publishing ZylabProductionSync failed event");

        ZylabProductionSyncDTO zylabProductionSyncDTO = new ZylabProductionSyncDTO();

        zylabProductionSyncDTO.setProductionKey(productionKey);
        zylabProductionSyncDTO.setMatterId(matterId);
        zylabProductionSyncDTO.setStatus(ZylabProductionSyncStatus.FAILED.name());
        zylabProductionSyncDTO.setError(error);

        ZylabProductionSyncEvent zylabProductionSyncEvent = new ZylabProductionSyncEvent(source, objectId, objectType,
                zylabProductionSyncDTO, false, auth);
        applicationEventPublisher.publishEvent(zylabProductionSyncEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

}
