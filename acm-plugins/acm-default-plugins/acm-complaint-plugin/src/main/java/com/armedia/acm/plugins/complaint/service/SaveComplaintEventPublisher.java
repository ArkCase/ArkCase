package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintApprovalWorkflowRequestedEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintCreatedEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintListView;
import com.armedia.acm.plugins.complaint.model.ComplaintPersistenceEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintSearchResultEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintUpdatedEvent;
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
            log.debug("Publishing a complaint event.");
        }

        ComplaintPersistenceEvent complaintPersistenceEvent =
                newComplaint ? new ComplaintCreatedEvent(source) : new ComplaintUpdatedEvent(source);
        complaintPersistenceEvent.setSucceeded(succeeded);
        if ( authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            complaintPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }

        eventPublisher.publishEvent(complaintPersistenceEvent);
    }

    public void publishComplaintWorkflowEvent(
            Complaint source,
            Authentication authentication,
            String userIpAddress)
    {
        ComplaintApprovalWorkflowRequestedEvent requestEvent = new ComplaintApprovalWorkflowRequestedEvent(source);
        requestEvent.setIpAddress(userIpAddress);
        requestEvent.setUserId(authentication.getName());
        eventPublisher.publishEvent(requestEvent);
    }


    public void publishComplaintSearchResultEvent(
            ComplaintListView source,
            Authentication authentication,
            String userIpAddress)
    {
        ComplaintSearchResultEvent event = new ComplaintSearchResultEvent(source);

        String user = authentication.getName();
        event.setUserId(user);
        event.setIpAddress(userIpAddress);

        eventPublisher.publishEvent(event);

    }
}
