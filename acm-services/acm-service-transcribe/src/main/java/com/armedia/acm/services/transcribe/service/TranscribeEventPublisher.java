package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.services.transcribe.model.*;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.http.auth.AUTH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 04/02/2018
 */
public class TranscribeEventPublisher implements ApplicationEventPublisherAware
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void publishTranscribeCreatedEvent(Transcribe source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName() : TranscribeConstants.TRANSCRIBE_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same) events with these parent information

        TranscribeCreatedEvent createdEventParentRoot = new TranscribeCreatedEvent(source);
        createdEventParentRoot.setUserId(user);
        createdEventParentRoot.setIpAddress(ipAddress);
        createdEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        createdEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        createdEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        createdEventParentRoot.setSucceeded(succeeded);

        TranscribeCreatedEvent createdEventParentFile = new TranscribeCreatedEvent(source);
        createdEventParentFile.setUserId(user);
        createdEventParentFile.setIpAddress(ipAddress);
        createdEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        createdEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        createdEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        createdEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(createdEventParentRoot);
        getApplicationEventPublisher().publishEvent(createdEventParentFile);
    }

    public void publishTranscribeUpdatedEvent(Transcribe source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName() : TranscribeConstants.TRANSCRIBE_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same) events with these parent information

        TranscribeUpdatedEvent updatedEventParentRoot = new TranscribeUpdatedEvent(source);
        updatedEventParentRoot.setUserId(user);
        updatedEventParentRoot.setIpAddress(ipAddress);
        updatedEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        updatedEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        updatedEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        updatedEventParentRoot.setSucceeded(succeeded);

        TranscribeUpdatedEvent updatedEventParentFile = new TranscribeUpdatedEvent(source);
        updatedEventParentFile.setUserId(user);
        updatedEventParentFile.setIpAddress(ipAddress);
        updatedEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        updatedEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        updatedEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        updatedEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(updatedEventParentRoot);
        getApplicationEventPublisher().publishEvent(updatedEventParentFile);
    }

    public void publishTranscribeQueuedEvent(Transcribe source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName() : TranscribeConstants.TRANSCRIBE_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same) events with these parent information

        TranscribeQueuedEvent queuedEventParentRoot = new TranscribeQueuedEvent(source);
        queuedEventParentRoot.setUserId(user);
        queuedEventParentRoot.setIpAddress(ipAddress);
        queuedEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        queuedEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        queuedEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        queuedEventParentRoot.setSucceeded(succeeded);

        TranscribeQueuedEvent queuedEventParentFile = new TranscribeQueuedEvent(source);
        queuedEventParentFile.setUserId(user);
        queuedEventParentFile.setIpAddress(ipAddress);
        queuedEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        queuedEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        queuedEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        queuedEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(queuedEventParentRoot);
        getApplicationEventPublisher().publishEvent(queuedEventParentFile);
    }

    public void publishTranscribeProcessingEvent(Transcribe source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName() : TranscribeConstants.TRANSCRIBE_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same) events with these parent information

        TranscribeProcessingEvent processingEventParentRoot = new TranscribeProcessingEvent(source);
        processingEventParentRoot.setUserId(user);
        processingEventParentRoot.setIpAddress(ipAddress);
        processingEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        processingEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        processingEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        processingEventParentRoot.setSucceeded(succeeded);

        TranscribeProcessingEvent processingEventParentFile = new TranscribeProcessingEvent(source);
        processingEventParentFile.setUserId(user);
        processingEventParentFile.setIpAddress(ipAddress);
        processingEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        processingEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        processingEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        processingEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(processingEventParentRoot);
        getApplicationEventPublisher().publishEvent(processingEventParentFile);
    }

    public void publishTranscribeCompletedEvent(Transcribe source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName() : TranscribeConstants.TRANSCRIBE_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same) events with these parent information

        TranscribeCompletedEvent completedEventParentRoot = new TranscribeCompletedEvent(source);
        completedEventParentRoot.setUserId(user);
        completedEventParentRoot.setIpAddress(ipAddress);
        completedEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        completedEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        completedEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        completedEventParentRoot.setSucceeded(succeeded);

        TranscribeCompletedEvent completedEventParentFile = new TranscribeCompletedEvent(source);
        completedEventParentFile.setUserId(user);
        completedEventParentFile.setIpAddress(ipAddress);
        completedEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        completedEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        completedEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        completedEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(completedEventParentRoot);
        getApplicationEventPublisher().publishEvent(completedEventParentFile);
    }

    public void publishTranscribeFailedEvent(Transcribe source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName() : TranscribeConstants.TRANSCRIBE_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same) events with these parent information

        TranscribeFailedEvent failedEventParentRoot = new TranscribeFailedEvent(source);
        failedEventParentRoot.setUserId(user);
        failedEventParentRoot.setIpAddress(ipAddress);
        failedEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        failedEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        failedEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        failedEventParentRoot.setSucceeded(succeeded);

        TranscribeFailedEvent failedEventParentFile = new TranscribeFailedEvent(source);
        failedEventParentFile.setUserId(user);
        failedEventParentFile.setIpAddress(ipAddress);
        failedEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        failedEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        failedEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        failedEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(failedEventParentRoot);
        getApplicationEventPublisher().publishEvent(failedEventParentFile);
    }

    public void publishTranscribeCancelledEvent(Transcribe source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName() : TranscribeConstants.TRANSCRIBE_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same) events with these parent information

        TranscribeCancelledEvent cancelledEventParentRoot = new TranscribeCancelledEvent(source);
        cancelledEventParentRoot.setUserId(user);
        cancelledEventParentRoot.setIpAddress(ipAddress);
        cancelledEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        cancelledEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        cancelledEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        cancelledEventParentRoot.setSucceeded(succeeded);

        TranscribeCancelledEvent cancelledEventParentFile = new TranscribeCancelledEvent(source);
        cancelledEventParentFile.setUserId(user);
        cancelledEventParentFile.setIpAddress(ipAddress);
        cancelledEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        cancelledEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        cancelledEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        cancelledEventParentFile.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(cancelledEventParentRoot);
        getApplicationEventPublisher().publishEvent(cancelledEventParentFile);
    }

    public void publishTranscribeCompiledEvent(Transcribe source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName() : TranscribeConstants.TRANSCRIBE_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same) events with these parent information

        TranscribeCompiledEvent compiledEventParentRoot = new TranscribeCompiledEvent(source);
        compiledEventParentRoot.setUserId(user);
        compiledEventParentRoot.setIpAddress(ipAddress);
        compiledEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        compiledEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        compiledEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        compiledEventParentRoot.setSucceeded(succeeded);

        TranscribeCompiledEvent compiledEventParentFile = new TranscribeCompiledEvent(source);
        compiledEventParentFile.setUserId(user);
        compiledEventParentFile.setIpAddress(ipAddress);
        compiledEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        compiledEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        compiledEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        compiledEventParentRoot.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(compiledEventParentRoot);
        getApplicationEventPublisher().publishEvent(compiledEventParentFile);
    }

    public void publishTranscribeProviderFailedEvent(Transcribe source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName() : TranscribeConstants.TRANSCRIBE_SYSTEM_USER;

        // No need to track history on Parent object (Case/Complaint/DocRepo) and parent File. For that reason just fire event with closest parent (version of the file)

        TranscribeProviderFailedEvent providerFailedEvent = new TranscribeProviderFailedEvent(source);
        providerFailedEvent.setUserId(user);
        providerFailedEvent.setIpAddress(ipAddress);
        providerFailedEvent.setParentObjectId(source.getMediaEcmFileVersion().getId());
        providerFailedEvent.setParentObjectType(source.getMediaEcmFileVersion().getObjectType());
        providerFailedEvent.setParentObjectName(source.getMediaEcmFileVersion().getId().toString());
        providerFailedEvent.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(providerFailedEvent);
    }

    public void publishTranscribeRollbackEvent(Transcribe source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName() : TranscribeConstants.TRANSCRIBE_SYSTEM_USER;

        // We need history on both levels, top parent (Case/Complaint/DocRepo) and parent File. So fire two (the same) events with these parent information

        TranscribeRollbackEvent rollbackEventParentRoot = new TranscribeRollbackEvent(source);
        rollbackEventParentRoot.setUserId(user);
        rollbackEventParentRoot.setIpAddress(ipAddress);
        rollbackEventParentRoot.setParentObjectId(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        rollbackEventParentRoot.setParentObjectType(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        rollbackEventParentRoot.setParentObjectName(source.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectTitle());
        rollbackEventParentRoot.setSucceeded(succeeded);

        TranscribeRollbackEvent rollbackEventParentFile = new TranscribeRollbackEvent(source);
        rollbackEventParentFile.setUserId(user);
        rollbackEventParentFile.setIpAddress(ipAddress);
        rollbackEventParentFile.setParentObjectId(source.getMediaEcmFileVersion().getFile().getId());
        rollbackEventParentFile.setParentObjectType(source.getMediaEcmFileVersion().getFile().getObjectType());
        rollbackEventParentFile.setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
        rollbackEventParentRoot.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(rollbackEventParentRoot);
        getApplicationEventPublisher().publishEvent(rollbackEventParentFile);
    }

    public void publish(Transcribe transcribe, String action)
    {
        TranscribeActionType transcribeActionType = null;
        try
        {
            transcribeActionType = TranscribeActionType.valueOf(action);
        }
        catch (IllegalArgumentException e)
        {
            String availableActions = Arrays.asList(TranscribeActionType.values()).stream().map(t -> t.toString()).collect(Collectors.joining(", "));
            LOG.error("Could not found available ACTION=[{}]. Available actions are: [{}]", action, availableActions);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication() : null;

        switch (transcribeActionType)
        {
            case CREATED:
                publishTranscribeCreatedEvent(transcribe, authentication, TranscribeConstants.TRANSCRIBE_SYSTEM_IP_ADDRESS, true);
                break;

            case UPDATED:
                publishTranscribeUpdatedEvent(transcribe, authentication, TranscribeConstants.TRANSCRIBE_SYSTEM_IP_ADDRESS, true);
                break;

            case QUEUED:
                publishTranscribeQueuedEvent(transcribe, authentication, TranscribeConstants.TRANSCRIBE_SYSTEM_IP_ADDRESS, true);
                break;

            case PROCESSING:
                publishTranscribeProcessingEvent(transcribe, authentication, TranscribeConstants.TRANSCRIBE_SYSTEM_IP_ADDRESS, true);
                break;

            case COMPLETED:
                publishTranscribeCompletedEvent(transcribe, authentication, TranscribeConstants.TRANSCRIBE_SYSTEM_IP_ADDRESS, true);
                break;

            case FAILED:
                publishTranscribeFailedEvent(transcribe, authentication, TranscribeConstants.TRANSCRIBE_SYSTEM_IP_ADDRESS, true);
                break;

            case CANCELLED:
                publishTranscribeCancelledEvent(transcribe, authentication, TranscribeConstants.TRANSCRIBE_SYSTEM_IP_ADDRESS, true);
                break;

            case COMPILED:
                publishTranscribeCompiledEvent(transcribe, authentication, TranscribeConstants.TRANSCRIBE_SYSTEM_IP_ADDRESS, true);
                break;

            case ROLLBACK:
                publishTranscribeRollbackEvent(transcribe, authentication, TranscribeConstants.TRANSCRIBE_SYSTEM_IP_ADDRESS, true);
                break;

            case PROVIDER_FAILED:
                publishTranscribeProviderFailedEvent(transcribe, authentication, TranscribeConstants.TRANSCRIBE_SYSTEM_IP_ADDRESS, true);
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
