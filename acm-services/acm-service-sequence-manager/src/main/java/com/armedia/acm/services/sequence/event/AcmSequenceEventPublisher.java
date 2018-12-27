package com.armedia.acm.services.sequence.event;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishSequenceConfigurationUpdatedEvent(List<AcmSequenceConfiguration> source)
    {
        AcmSequenceConfigurationUpdatedEvent sequenceConfigurationUpdatedEvent = new AcmSequenceConfigurationUpdatedEvent(source);
        sequenceConfigurationUpdatedEvent.setUserId(AuthenticationUtils.getUsername());
        sequenceConfigurationUpdatedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        sequenceConfigurationUpdatedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(sequenceConfigurationUpdatedEvent);
    }

    public void publishSequenceResetCreatedEvent(AcmSequenceReset source)
    {
        AcmSequenceResetCreatedEvent sequenceResetCreatedEvent = new AcmSequenceResetCreatedEvent(source);
        sequenceResetCreatedEvent.setUserId(AuthenticationUtils.getUsername());
        sequenceResetCreatedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        sequenceResetCreatedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(sequenceResetCreatedEvent);
    }

    public void publishSequenceResetUpdatedEvent(AcmSequenceReset source)
    {
        AcmSequenceResetUpdatedEvent sequenceResetUpdatedEvent = new AcmSequenceResetUpdatedEvent(source);
        sequenceResetUpdatedEvent.setUserId(AuthenticationUtils.getUsername());
        sequenceResetUpdatedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        sequenceResetUpdatedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(sequenceResetUpdatedEvent);
    }

    public void publishSequenceresetRemovedEvent(AcmSequenceReset source)
    {
        AcmSequenceResetRemovedEvent sequenceResetRemovedEvent = new AcmSequenceResetRemovedEvent(source);
        sequenceResetRemovedEvent.setUserId(AuthenticationUtils.getUsername());
        sequenceResetRemovedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        sequenceResetRemovedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(sequenceResetRemovedEvent);
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
