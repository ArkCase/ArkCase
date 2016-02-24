package com.armedia.acm.plugins.casefile.utility;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileModifiedEvent;
import com.armedia.acm.plugins.casefile.model.FileAddedEvent;

/**
 * Created by armdev on 9/4/14.
 */
public class CaseFileEventUtility implements ApplicationEventPublisherAware
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void raiseEvent(CaseFile caseFile, String caseState, Date eventDate, String ipAddress, String userId, Authentication auth)
    {
        String eventType = "com.armedia.acm.casefile.event." + caseState;
        eventDate = eventDate == null ? new Date() : eventDate;
        CaseEvent event = new CaseEvent(caseFile, ipAddress, userId, eventType, eventDate, true, auth);

        applicationEventPublisher.publishEvent(event);
    }

    public void raiseFileAddedEvent(CaseFile source, String userId, boolean succeeded) {

        FileAddedEvent fileAddedEvent = new FileAddedEvent(source);
        fileAddedEvent.setSucceeded(succeeded);
        fileAddedEvent.setUserId(userId);

        applicationEventPublisher.publishEvent(fileAddedEvent);
    }
    
    public void raiseFileModifiedEvent(CaseFile source, String ipAddress, String eventStatus) {

        CaseFileModifiedEvent event = new CaseFileModifiedEvent(source);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setEventStatus(eventStatus);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
