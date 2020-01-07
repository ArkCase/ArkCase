package com.armedia.acm.services.sequence.event;

/*-
 * #%L
 * ACM Service: Sequence Manager
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
import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());
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
