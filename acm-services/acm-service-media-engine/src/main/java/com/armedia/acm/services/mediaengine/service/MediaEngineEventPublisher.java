package com.armedia.acm.services.mediaengine.service;

/*-
 * #%L
 * ACM Service: MediaEngine
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

import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineActionType;
import com.armedia.acm.services.mediaengine.model.MediaEngineConstants;
import com.armedia.acm.services.mediaengine.model.events.MediaEngineCancelledEvent;
import com.armedia.acm.services.mediaengine.model.events.MediaEngineCompiledEvent;
import com.armedia.acm.services.mediaengine.model.events.MediaEngineCompletedEvent;
import com.armedia.acm.services.mediaengine.model.events.MediaEngineCreatedEvent;
import com.armedia.acm.services.mediaengine.model.events.MediaEngineFailedEvent;
import com.armedia.acm.services.mediaengine.model.events.MediaEngineProcessingEvent;
import com.armedia.acm.services.mediaengine.model.events.MediaEngineProviderFailedEvent;
import com.armedia.acm.services.mediaengine.model.events.MediaEngineQueuedEvent;
import com.armedia.acm.services.mediaengine.model.events.MediaEngineRollbackEvent;
import com.armedia.acm.services.mediaengine.model.events.MediaEngineUpdatedEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class MediaEngineEventPublisher implements ApplicationEventPublisherAware
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void publishMediaEngineCreatedEvent(MediaEngine source, Authentication authentication, String ipAddress, boolean succeeded,
            String service)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : MediaEngineConstants.SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        MediaEngineCreatedEvent createdEventParentRoot = new MediaEngineCreatedEvent(source, service);
        createdEventParentRoot.setUserId(user);
        createdEventParentRoot.setIpAddress(ipAddress);
        createdEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        createdEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        createdEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        createdEventParentRoot.setSucceeded(succeeded);

        MediaEngineCreatedEvent createdEventParentFile = new MediaEngineCreatedEvent(source, service);
        createdEventParentFile.setUserId(user);
        createdEventParentFile.setIpAddress(ipAddress);
        createdEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        createdEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        createdEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        createdEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(createdEventParentRoot);
        getApplicationEventPublisher().publishEvent(createdEventParentFile);
    }

    public void publishMediaEngineUpdatedEvent(MediaEngine source, Authentication authentication, String ipAddress, boolean succeeded,
            String service)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : MediaEngineConstants.SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        MediaEngineUpdatedEvent updatedEventParentRoot = new MediaEngineUpdatedEvent(source, service);
        updatedEventParentRoot.setUserId(user);
        updatedEventParentRoot.setIpAddress(ipAddress);
        updatedEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        updatedEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        updatedEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        updatedEventParentRoot.setSucceeded(succeeded);

        MediaEngineUpdatedEvent updatedEventParentFile = new MediaEngineUpdatedEvent(source, service);
        updatedEventParentFile.setUserId(user);
        updatedEventParentFile.setIpAddress(ipAddress);
        updatedEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        updatedEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        updatedEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        updatedEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(updatedEventParentRoot);
        getApplicationEventPublisher().publishEvent(updatedEventParentFile);
    }

    public void publishMediaEngineQueuedEvent(MediaEngine source, Authentication authentication, String ipAddress, boolean succeeded,
            String service)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : MediaEngineConstants.SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        MediaEngineQueuedEvent queuedEventParentRoot = new MediaEngineQueuedEvent(source, service);
        queuedEventParentRoot.setUserId(user);
        queuedEventParentRoot.setIpAddress(ipAddress);
        queuedEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        queuedEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        queuedEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        queuedEventParentRoot.setSucceeded(succeeded);

        MediaEngineQueuedEvent queuedEventParentFile = new MediaEngineQueuedEvent(source, service);
        queuedEventParentFile.setUserId(user);
        queuedEventParentFile.setIpAddress(ipAddress);
        queuedEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        queuedEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        queuedEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        queuedEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(queuedEventParentRoot);
        getApplicationEventPublisher().publishEvent(queuedEventParentFile);
    }

    public void publishMediaEngineProcessingEvent(MediaEngine source, Authentication authentication, String ipAddress, boolean succeeded,
            String service)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : MediaEngineConstants.SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        MediaEngineProcessingEvent processingEventParentRoot = new MediaEngineProcessingEvent(source, service);
        processingEventParentRoot.setUserId(user);
        processingEventParentRoot.setIpAddress(ipAddress);
        processingEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        processingEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        processingEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        processingEventParentRoot.setSucceeded(succeeded);

        MediaEngineProcessingEvent processingEventParentFile = new MediaEngineProcessingEvent(source, service);
        processingEventParentFile.setUserId(user);
        processingEventParentFile.setIpAddress(ipAddress);
        processingEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        processingEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        processingEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        processingEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(processingEventParentRoot);
        getApplicationEventPublisher().publishEvent(processingEventParentFile);
    }

    public void publishMediaEngineCompletedEvent(MediaEngine source, Authentication authentication, String ipAddress, boolean succeeded,
            String service)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : MediaEngineConstants.SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        MediaEngineCompletedEvent completedEventParentRoot = new MediaEngineCompletedEvent(source, service);
        completedEventParentRoot.setUserId(user);
        completedEventParentRoot.setIpAddress(ipAddress);
        completedEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        completedEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        completedEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        completedEventParentRoot.setSucceeded(succeeded);

        MediaEngineCompletedEvent completedEventParentFile = new MediaEngineCompletedEvent(source, service);
        completedEventParentFile.setUserId(user);
        completedEventParentFile.setIpAddress(ipAddress);
        completedEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        completedEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        completedEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        completedEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(completedEventParentRoot);
        getApplicationEventPublisher().publishEvent(completedEventParentFile);
    }

    public void publishMediaEngineFailedEvent(MediaEngine source, Authentication authentication, String ipAddress, boolean succeeded,
            String service, String message)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : MediaEngineConstants.SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        MediaEngineFailedEvent failedEventParentRoot = new MediaEngineFailedEvent(source, service);
        failedEventParentRoot.setUserId(user);
        failedEventParentRoot.setIpAddress(ipAddress);
        failedEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        failedEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        failedEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        failedEventParentRoot.setSucceeded(succeeded);

        MediaEngineFailedEvent failedEventParentFile = new MediaEngineFailedEvent(source, service);
        failedEventParentFile.setUserId(user);
        failedEventParentFile.setIpAddress(ipAddress);
        failedEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        failedEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        failedEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        failedEventParentFile.setSucceeded(succeeded);
        failedEventParentFile.setEventDescription(message);

        getApplicationEventPublisher().publishEvent(failedEventParentRoot);
        getApplicationEventPublisher().publishEvent(failedEventParentFile);
    }

    public void publishMediaEngineCancelledEvent(MediaEngine source, Authentication authentication, String ipAddress, boolean succeeded,
            String service)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : MediaEngineConstants.SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        MediaEngineCancelledEvent cancelledEventParentRoot = new MediaEngineCancelledEvent(source, service);
        cancelledEventParentRoot.setUserId(user);
        cancelledEventParentRoot.setIpAddress(ipAddress);
        cancelledEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        cancelledEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        cancelledEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        cancelledEventParentRoot.setSucceeded(succeeded);

        MediaEngineCancelledEvent cancelledEventParentFile = new MediaEngineCancelledEvent(source, service);
        cancelledEventParentFile.setUserId(user);
        cancelledEventParentFile.setIpAddress(ipAddress);
        cancelledEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        cancelledEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        cancelledEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        cancelledEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(cancelledEventParentRoot);
        getApplicationEventPublisher().publishEvent(cancelledEventParentFile);
    }

    public void publishMediaEngineCompiledEvent(MediaEngine source, Authentication authentication, String ipAddress, boolean succeeded,
            String service)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : MediaEngineConstants.SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        MediaEngineCompiledEvent compiledEventParentRoot = new MediaEngineCompiledEvent(source, service);
        compiledEventParentRoot.setUserId(user);
        compiledEventParentRoot.setIpAddress(ipAddress);
        compiledEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        compiledEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        compiledEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        compiledEventParentRoot.setSucceeded(succeeded);

        MediaEngineCompiledEvent compiledEventParentFile = new MediaEngineCompiledEvent(source, service);
        compiledEventParentFile.setUserId(user);
        compiledEventParentFile.setIpAddress(ipAddress);
        compiledEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        compiledEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        compiledEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        compiledEventParentRoot.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(compiledEventParentRoot);
        getApplicationEventPublisher().publishEvent(compiledEventParentFile);
    }

    public void publishMediaEngineProviderFailedEvent(MediaEngine source, Authentication authentication, String ipAddress,
            boolean succeeded, String service, String message)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : MediaEngineConstants.SYSTEM_USER;

        // No need to track history on Parent object (Case/Complaint/DocRepo) and parent File. For that reason just fire
        // event with closest parent (version of the file)

        MediaEngineProviderFailedEvent providerFailedEvent = new MediaEngineProviderFailedEvent(source, service);
        providerFailedEvent.setUserId(user);
        providerFailedEvent.setIpAddress(ipAddress);
        providerFailedEvent.setParentObjectId(source.getMediaEcmFileVersion().getId());
        providerFailedEvent.setParentObjectType(source.getMediaEcmFileVersion().getObjectType());
        providerFailedEvent.setParentObjectName(source.getMediaEcmFileVersion().getId().toString());
        providerFailedEvent.setSucceeded(succeeded);
        providerFailedEvent.setEventDescription(message);

        getApplicationEventPublisher().publishEvent(providerFailedEvent);
    }

    public void publishMediaEngineRollbackEvent(MediaEngine source, Authentication authentication, String ipAddress, boolean succeeded,
            String service)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : MediaEngineConstants.SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        MediaEngineRollbackEvent rollbackEventParentRoot = new MediaEngineRollbackEvent(source, service);
        rollbackEventParentRoot.setUserId(user);
        rollbackEventParentRoot.setIpAddress(ipAddress);
        rollbackEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        rollbackEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        rollbackEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        rollbackEventParentRoot.setSucceeded(succeeded);

        MediaEngineRollbackEvent rollbackEventParentFile = new MediaEngineRollbackEvent(source, service);
        rollbackEventParentFile.setUserId(user);
        rollbackEventParentFile.setIpAddress(ipAddress);
        rollbackEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        rollbackEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        rollbackEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        rollbackEventParentRoot.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(rollbackEventParentRoot);
        getApplicationEventPublisher().publishEvent(rollbackEventParentFile);
    }

    public void publish(MediaEngine mediaEngine, String action, String service, String message)
    {
        MediaEngineActionType mediaEngineActionType = null;
        try
        {
            mediaEngineActionType = mediaEngineActionType.valueOf(action);
        }
        catch (IllegalArgumentException e)
        {
            String availableActions = Arrays.asList(mediaEngineActionType.values()).stream().map(t -> t.toString())
                    .collect(Collectors.joining(", "));
            LOG.error("Could not found available ACTION=[{}]. Available actions are: [{}]", action, availableActions);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                : null;

        switch (mediaEngineActionType)
        {
        case CREATED:
            publishMediaEngineCreatedEvent(mediaEngine, authentication, MediaEngineConstants.SYSTEM_IP_ADDRESS, true, service);
            break;

        case UPDATED:
            publishMediaEngineUpdatedEvent(mediaEngine, authentication, MediaEngineConstants.SYSTEM_IP_ADDRESS, true, service);
            break;

        case QUEUED:
            publishMediaEngineQueuedEvent(mediaEngine, authentication, MediaEngineConstants.SYSTEM_IP_ADDRESS, true, service);
            break;

        case PROCESSING:
            publishMediaEngineProcessingEvent(mediaEngine, authentication, MediaEngineConstants.SYSTEM_IP_ADDRESS, true, service);
            break;

        case COMPLETED:
            publishMediaEngineCompletedEvent(mediaEngine, authentication, MediaEngineConstants.SYSTEM_IP_ADDRESS, true, service);
            break;

        case FAILED:
            publishMediaEngineFailedEvent(mediaEngine, authentication, MediaEngineConstants.SYSTEM_IP_ADDRESS, true, service, message);
            break;

        case CANCELLED:
            publishMediaEngineCancelledEvent(mediaEngine, authentication, MediaEngineConstants.SYSTEM_IP_ADDRESS, true, service);
            break;

        case COMPILED:
            publishMediaEngineCompiledEvent(mediaEngine, authentication, MediaEngineConstants.SYSTEM_IP_ADDRESS, true, service);
            break;

        case ROLLBACK:
            publishMediaEngineRollbackEvent(mediaEngine, authentication, MediaEngineConstants.SYSTEM_IP_ADDRESS, true, service);
            break;

        case PROVIDER_FAILED:
            publishMediaEngineProviderFailedEvent(mediaEngine, authentication, MediaEngineConstants.SYSTEM_IP_ADDRESS, true, service,
                    message);
            break;
        }
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
