package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * Created by armdev on 4/10/14.
 */
public class SaveComplaintEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher eventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishComplaintEvent(
            Complaint source,
            Authentication authentication,
            boolean newComplaint,
            boolean succeeded)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Publishing a complaint created event.");
        }

        if ( newComplaint )
        {
            ComplaintCreatedEvent event = new ComplaintCreatedEvent(source);
            event.setSucceeded(succeeded);
            if ( authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
            {
                event.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
            }

            eventPublisher.publishEvent(event);
        }
    }
}
