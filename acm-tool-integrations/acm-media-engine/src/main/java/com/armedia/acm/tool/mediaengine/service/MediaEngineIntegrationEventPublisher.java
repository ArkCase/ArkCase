package com.armedia.acm.tool.mediaengine.service;

/*-
 * #%L
 * ACM Service: OCR
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

import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.mediaengine.model.MediaEngineIntegrationFailedEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class MediaEngineIntegrationEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishFailedEvent(MediaEngineDTO source, String user, String ipAddress, boolean succeeded,
            String service, String message)
    {

        MediaEngineIntegrationFailedEvent failedEventParentRoot = new MediaEngineIntegrationFailedEvent(source);
        failedEventParentRoot.setUserId(user);
        failedEventParentRoot.setIpAddress(ipAddress);
        failedEventParentRoot.setParentObjectId(Long.valueOf(source.getProperties().get("containerObjectId")));
        failedEventParentRoot.setParentObjectType(source.getProperties().get("containerObjectType"));
        failedEventParentRoot.setParentObjectName(source.getProperties().get("containerObjectTitle"));
        failedEventParentRoot.setSucceeded(succeeded);
        failedEventParentRoot.setServiceName(service);

        MediaEngineIntegrationFailedEvent failedEventParentFile = new MediaEngineIntegrationFailedEvent(source);
        failedEventParentFile.setUserId(user);
        failedEventParentFile.setIpAddress(ipAddress);
        failedEventParentFile.setParentObjectId(Long.valueOf(source.getProperties().get("fileId")));
        failedEventParentFile.setParentObjectType(source.getProperties().get("fileObjectType"));
        failedEventParentFile.setParentObjectName(source.getProperties().get("fileName"));
        failedEventParentFile.setSucceeded(succeeded);
        failedEventParentFile.setServiceName(service);
        failedEventParentFile.setEventDescription(message);

        getApplicationEventPublisher().publishEvent(failedEventParentRoot);
        getApplicationEventPublisher().publishEvent(failedEventParentFile);
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
