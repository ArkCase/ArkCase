package com.armedia.acm.ocr.service;

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

import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRActionType;
import com.armedia.acm.ocr.model.OCRCancelledEvent;
import com.armedia.acm.ocr.model.OCRCompiledEvent;
import com.armedia.acm.ocr.model.OCRCompletedEvent;
import com.armedia.acm.ocr.model.OCRConstants;
import com.armedia.acm.ocr.model.OCRCreatedEvent;
import com.armedia.acm.ocr.model.OCRFailedEvent;
import com.armedia.acm.ocr.model.OCRProcessingEvent;
import com.armedia.acm.ocr.model.OCRProviderFailedEvent;
import com.armedia.acm.ocr.model.OCRQueuedEvent;
import com.armedia.acm.ocr.model.OCRRollbackEvent;
import com.armedia.acm.ocr.model.OCRUpdatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCREventPublisher implements ApplicationEventPublisherAware
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void publishOCRCreatedEvent(OCR source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : OCRConstants.OCR_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        OCRCreatedEvent createdEventParentRoot = new OCRCreatedEvent(source);
        createdEventParentRoot.setUserId(user);
        createdEventParentRoot.setIpAddress(ipAddress);
        createdEventParentRoot.setParentObjectId(source.getEcmFileVersion().getFile().getContainer().getContainerObjectId());
        createdEventParentRoot.setParentObjectType(source.getEcmFileVersion().getFile().getContainer().getContainerObjectType());
        createdEventParentRoot.setParentObjectName(source.getEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        createdEventParentRoot.setSucceeded(succeeded);

        OCRCreatedEvent createdEventParentFile = new OCRCreatedEvent(source);
        createdEventParentFile.setUserId(user);
        createdEventParentFile.setIpAddress(ipAddress);
        createdEventParentFile.setParentObjectId(source.getEcmFileVersion().getFile().getId());
        createdEventParentFile.setParentObjectType(source.getEcmFileVersion().getFile().getObjectType());
        createdEventParentFile.setParentObjectName(source.getEcmFileVersion().getFile().getFileName());
        createdEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(createdEventParentRoot);
        getApplicationEventPublisher().publishEvent(createdEventParentFile);
    }

    public void publishOCRUpdatedEvent(OCR source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : OCRConstants.OCR_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        OCRUpdatedEvent updatedEventParentRoot = new OCRUpdatedEvent(source);
        updatedEventParentRoot.setUserId(user);
        updatedEventParentRoot.setIpAddress(ipAddress);
        updatedEventParentRoot.setParentObjectId(source.getEcmFileVersion().getFile().getContainer().getContainerObjectId());
        updatedEventParentRoot.setParentObjectType(source.getEcmFileVersion().getFile().getContainer().getContainerObjectType());
        updatedEventParentRoot.setParentObjectName(source.getEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        updatedEventParentRoot.setSucceeded(succeeded);

        OCRUpdatedEvent updatedEventParentFile = new OCRUpdatedEvent(source);
        updatedEventParentFile.setUserId(user);
        updatedEventParentFile.setIpAddress(ipAddress);
        updatedEventParentFile.setParentObjectId(source.getEcmFileVersion().getFile().getId());
        updatedEventParentFile.setParentObjectType(source.getEcmFileVersion().getFile().getObjectType());
        updatedEventParentFile.setParentObjectName(source.getEcmFileVersion().getFile().getFileName());
        updatedEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(updatedEventParentRoot);
        getApplicationEventPublisher().publishEvent(updatedEventParentFile);
    }

    public void publishOCRQueuedEvent(OCR source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : OCRConstants.OCR_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        OCRQueuedEvent queuedEventParentRoot = new OCRQueuedEvent(source);
        queuedEventParentRoot.setUserId(user);
        queuedEventParentRoot.setIpAddress(ipAddress);
        queuedEventParentRoot.setParentObjectId(source.getEcmFileVersion().getFile().getContainer().getContainerObjectId());
        queuedEventParentRoot.setParentObjectType(source.getEcmFileVersion().getFile().getContainer().getContainerObjectType());
        queuedEventParentRoot.setParentObjectName(source.getEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        queuedEventParentRoot.setSucceeded(succeeded);

        OCRQueuedEvent queuedEventParentFile = new OCRQueuedEvent(source);
        queuedEventParentFile.setUserId(user);
        queuedEventParentFile.setIpAddress(ipAddress);
        queuedEventParentFile.setParentObjectId(source.getEcmFileVersion().getFile().getId());
        queuedEventParentFile.setParentObjectType(source.getEcmFileVersion().getFile().getObjectType());
        queuedEventParentFile.setParentObjectName(source.getEcmFileVersion().getFile().getFileName());
        queuedEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(queuedEventParentRoot);
        getApplicationEventPublisher().publishEvent(queuedEventParentFile);
    }

    public void publishOCRProcessingEvent(OCR source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : OCRConstants.OCR_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        OCRProcessingEvent processingEventParentRoot = new OCRProcessingEvent(source);
        processingEventParentRoot.setUserId(user);
        processingEventParentRoot.setIpAddress(ipAddress);
        processingEventParentRoot.setParentObjectId(source.getEcmFileVersion().getFile().getContainer().getContainerObjectId());
        processingEventParentRoot.setParentObjectType(source.getEcmFileVersion().getFile().getContainer().getContainerObjectType());
        processingEventParentRoot.setParentObjectName(source.getEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        processingEventParentRoot.setSucceeded(succeeded);

        OCRProcessingEvent processingEventParentFile = new OCRProcessingEvent(source);
        processingEventParentFile.setUserId(user);
        processingEventParentFile.setIpAddress(ipAddress);
        processingEventParentFile.setParentObjectId(source.getEcmFileVersion().getFile().getId());
        processingEventParentFile.setParentObjectType(source.getEcmFileVersion().getFile().getObjectType());
        processingEventParentFile.setParentObjectName(source.getEcmFileVersion().getFile().getFileName());
        processingEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(processingEventParentRoot);
        getApplicationEventPublisher().publishEvent(processingEventParentFile);
    }

    public void publishOCRCompletedEvent(OCR source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : OCRConstants.OCR_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        OCRCompletedEvent completedEventParentRoot = new OCRCompletedEvent(source);
        completedEventParentRoot.setUserId(user);
        completedEventParentRoot.setIpAddress(ipAddress);
        completedEventParentRoot.setParentObjectId(source.getEcmFileVersion().getFile().getContainer().getContainerObjectId());
        completedEventParentRoot.setParentObjectType(source.getEcmFileVersion().getFile().getContainer().getContainerObjectType());
        completedEventParentRoot.setParentObjectName(source.getEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        completedEventParentRoot.setSucceeded(succeeded);

        OCRCompletedEvent completedEventParentFile = new OCRCompletedEvent(source);
        completedEventParentFile.setUserId(user);
        completedEventParentFile.setIpAddress(ipAddress);
        completedEventParentFile.setParentObjectId(source.getEcmFileVersion().getFile().getId());
        completedEventParentFile.setParentObjectType(source.getEcmFileVersion().getFile().getObjectType());
        completedEventParentFile.setParentObjectName(source.getEcmFileVersion().getFile().getFileName());
        completedEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(completedEventParentRoot);
        getApplicationEventPublisher().publishEvent(completedEventParentFile);
    }

    public void publishOCRFailedEvent(OCR source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : OCRConstants.OCR_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        OCRFailedEvent failedEventParentRoot = new OCRFailedEvent(source);
        failedEventParentRoot.setUserId(user);
        failedEventParentRoot.setIpAddress(ipAddress);
        failedEventParentRoot.setParentObjectId(source.getEcmFileVersion().getFile().getContainer().getContainerObjectId());
        failedEventParentRoot.setParentObjectType(source.getEcmFileVersion().getFile().getContainer().getContainerObjectType());
        failedEventParentRoot.setParentObjectName(source.getEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        failedEventParentRoot.setSucceeded(succeeded);

        OCRFailedEvent failedEventParentFile = new OCRFailedEvent(source);
        failedEventParentFile.setUserId(user);
        failedEventParentFile.setIpAddress(ipAddress);
        failedEventParentFile.setParentObjectId(source.getEcmFileVersion().getFile().getId());
        failedEventParentFile.setParentObjectType(source.getEcmFileVersion().getFile().getObjectType());
        failedEventParentFile.setParentObjectName(source.getEcmFileVersion().getFile().getFileName());
        failedEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(failedEventParentRoot);
        getApplicationEventPublisher().publishEvent(failedEventParentFile);
    }

    public void publishOCRCancelledEvent(OCR source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : OCRConstants.OCR_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        OCRCancelledEvent cancelledEventParentRoot = new OCRCancelledEvent(source);
        cancelledEventParentRoot.setUserId(user);
        cancelledEventParentRoot.setIpAddress(ipAddress);
        cancelledEventParentRoot.setParentObjectId(source.getEcmFileVersion().getFile().getContainer().getContainerObjectId());
        cancelledEventParentRoot.setParentObjectType(source.getEcmFileVersion().getFile().getContainer().getContainerObjectType());
        cancelledEventParentRoot.setParentObjectName(source.getEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        cancelledEventParentRoot.setSucceeded(succeeded);

        OCRCancelledEvent cancelledEventParentFile = new OCRCancelledEvent(source);
        cancelledEventParentFile.setUserId(user);
        cancelledEventParentFile.setIpAddress(ipAddress);
        cancelledEventParentFile.setParentObjectId(source.getEcmFileVersion().getFile().getId());
        cancelledEventParentFile.setParentObjectType(source.getEcmFileVersion().getFile().getObjectType());
        cancelledEventParentFile.setParentObjectName(source.getEcmFileVersion().getFile().getFileName());
        cancelledEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(cancelledEventParentRoot);
        getApplicationEventPublisher().publishEvent(cancelledEventParentFile);
    }

    public void publishOCRCompiledEvent(OCR source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : OCRConstants.OCR_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        OCRCompiledEvent compiledEventParentRoot = new OCRCompiledEvent(source);
        compiledEventParentRoot.setUserId(user);
        compiledEventParentRoot.setIpAddress(ipAddress);
        compiledEventParentRoot.setParentObjectId(source.getEcmFileVersion().getFile().getContainer().getContainerObjectId());
        compiledEventParentRoot.setParentObjectType(source.getEcmFileVersion().getFile().getContainer().getContainerObjectType());
        compiledEventParentRoot.setParentObjectName(source.getEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        compiledEventParentRoot.setSucceeded(succeeded);

        OCRCompiledEvent compiledEventParentFile = new OCRCompiledEvent(source);
        compiledEventParentFile.setUserId(user);
        compiledEventParentFile.setIpAddress(ipAddress);
        compiledEventParentFile.setParentObjectId(source.getEcmFileVersion().getFile().getId());
        compiledEventParentFile.setParentObjectType(source.getEcmFileVersion().getFile().getObjectType());
        compiledEventParentFile.setParentObjectName(source.getEcmFileVersion().getFile().getFileName());
        compiledEventParentRoot.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(compiledEventParentRoot);
        getApplicationEventPublisher().publishEvent(compiledEventParentFile);
    }

    public void publishOCRProviderFailedEvent(OCR source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : OCRConstants.OCR_SYSTEM_USER;

        // No need to track history on Parent object (Case/Complaint/DocRepo) and parent File. For that reason just fire
        // event with closest parent (version of the file)

        OCRProviderFailedEvent providerFailedEvent = new OCRProviderFailedEvent(source);
        providerFailedEvent.setUserId(user);
        providerFailedEvent.setIpAddress(ipAddress);
        providerFailedEvent.setParentObjectId(source.getEcmFileVersion().getId());
        providerFailedEvent.setParentObjectType(source.getEcmFileVersion().getObjectType());
        providerFailedEvent.setParentObjectName(source.getEcmFileVersion().getId().toString());
        providerFailedEvent.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(providerFailedEvent);
    }

    public void publishOCRRollbackEvent(OCR source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : OCRConstants.OCR_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same)
        // events with these parent information

        OCRRollbackEvent rollbackEventParentRoot = new OCRRollbackEvent(source);
        rollbackEventParentRoot.setUserId(user);
        rollbackEventParentRoot.setIpAddress(ipAddress);
        rollbackEventParentRoot.setParentObjectId(source.getEcmFileVersion().getFile().getContainer().getContainerObjectId());
        rollbackEventParentRoot.setParentObjectType(source.getEcmFileVersion().getFile().getContainer().getContainerObjectType());
        rollbackEventParentRoot.setParentObjectName(source.getEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        rollbackEventParentRoot.setSucceeded(succeeded);

        OCRRollbackEvent rollbackEventParentFile = new OCRRollbackEvent(source);
        rollbackEventParentFile.setUserId(user);
        rollbackEventParentFile.setIpAddress(ipAddress);
        rollbackEventParentFile.setParentObjectId(source.getEcmFileVersion().getFile().getId());
        rollbackEventParentFile.setParentObjectType(source.getEcmFileVersion().getFile().getObjectType());
        rollbackEventParentFile.setParentObjectName(source.getEcmFileVersion().getFile().getFileName());
        rollbackEventParentRoot.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(rollbackEventParentRoot);
        getApplicationEventPublisher().publishEvent(rollbackEventParentFile);
    }

    public void publish(OCR ocr, String action)
    {
        OCRActionType ocrActionType = null;
        try
        {
            ocrActionType = ocrActionType.valueOf(action);
        }
        catch (IllegalArgumentException e)
        {
            String availableActions = Arrays.asList(ocrActionType.values()).stream().map(t -> t.toString())
                    .collect(Collectors.joining(", "));
            LOG.error("Could not found available ACTION=[{}]. Available actions are: [{}]", action, availableActions);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                : null;

        switch (ocrActionType)
        {
        case CREATED:
            publishOCRCreatedEvent(ocr, authentication, OCRConstants.OCR_SYSTEM_IP_ADDRESS, true);
            break;

        case UPDATED:
            publishOCRUpdatedEvent(ocr, authentication, OCRConstants.OCR_SYSTEM_IP_ADDRESS, true);
            break;

        case QUEUED:
            publishOCRQueuedEvent(ocr, authentication, OCRConstants.OCR_SYSTEM_IP_ADDRESS, true);
            break;

        case PROCESSING:
            publishOCRProcessingEvent(ocr, authentication, OCRConstants.OCR_SYSTEM_IP_ADDRESS, true);
            break;

        case COMPLETED:
            publishOCRCompletedEvent(ocr, authentication, OCRConstants.OCR_SYSTEM_IP_ADDRESS, true);
            break;

        case FAILED:
            publishOCRFailedEvent(ocr, authentication, OCRConstants.OCR_SYSTEM_IP_ADDRESS, true);
            break;

        case CANCELLED:
            publishOCRCancelledEvent(ocr, authentication, OCRConstants.OCR_SYSTEM_IP_ADDRESS, true);
            break;

        case COMPILED:
            publishOCRCompiledEvent(ocr, authentication, OCRConstants.OCR_SYSTEM_IP_ADDRESS, true);
            break;

        case ROLLBACK:
            publishOCRRollbackEvent(ocr, authentication, OCRConstants.OCR_SYSTEM_IP_ADDRESS, true);
            break;

        case PROVIDER_FAILED:
            publishOCRProviderFailedEvent(ocr, authentication, OCRConstants.OCR_SYSTEM_IP_ADDRESS, true);
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
