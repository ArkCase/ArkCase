/**
 *
 */
package com.armedia.acm.service.objecthistory.service;

import com.armedia.acm.service.objecthistory.model.AcmAssigneeChangeEvent;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEventType;
import com.armedia.acm.service.objecthistory.model.AcmOwningGroup;
import com.armedia.acm.service.objecthistory.model.AcmOwningGroupChangeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @author riste.tutureski
 */
public class AcmObjectHistoryEventPublisher implements ApplicationEventPublisherAware
{

    private ApplicationEventPublisher eventPublisher;

    public void publishCreatedEvent(AcmObjectHistory source, String ipAddress)
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(source);

        event.setEventType(AcmObjectHistoryEventType.CREATED);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);

        getEventPublisher().publishEvent(event);
    }

    public void publishAssigneeChangeEvent(AcmAssignment source, String userId, String ipAddress)
    {
        AcmAssigneeChangeEvent event = new AcmAssigneeChangeEvent(source, userId);

        event.setIpAddress(ipAddress);
        event.setSucceeded(true);

        getEventPublisher().publishEvent(event);
    }

    public void publishOwningGroupChangeEvent(AcmOwningGroup source, String userId, String ipAddress)
    {
        AcmOwningGroupChangeEvent event = new AcmOwningGroupChangeEvent(source, userId);

        event.setIpAddress(ipAddress);
        event.setSucceeded(true);

        getEventPublisher().publishEvent(event);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

}
