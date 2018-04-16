package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.objectdiff.model.AcmDiff;
import com.armedia.acm.objectdiff.service.AcmDiffService;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintApprovalWorkflowRequestedEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintClosedEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintCreatedEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintFileAddedEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintListView;
import com.armedia.acm.plugins.complaint.model.ComplaintModifiedEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintParticipantsModifiedEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintPersistenceEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintSearchResultEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintUpdatedEvent;
import com.armedia.acm.plugins.complaint.model.FindComplaintByIdEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 4/10/14.
 */
public class ComplaintEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher eventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private AcmDiffService acmDiffService;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishComplaintEvent(Complaint updatedComplaint, Complaint oldComplaint, Authentication authentication,
            boolean isNewComplaint,
            boolean succeeded)
    {
        log.debug("Publishing a complaint event.");

        ComplaintPersistenceEvent complaintPersistenceEvent = isNewComplaint ? new ComplaintCreatedEvent(updatedComplaint)
                : new ComplaintUpdatedEvent(updatedComplaint);
        complaintPersistenceEvent.setSucceeded(succeeded);
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            complaintPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }

        AcmDiff acmDiff = acmDiffService.compareObjects(oldComplaint, updatedComplaint);
        if (acmDiff != null)
        {
            try
            {
                complaintPersistenceEvent.setDiffDetailsAsJson(acmDiff.getChangesAsListJson());
            }
            catch (JsonProcessingException e)
            {
                log.warn("can't process diff details for [{}].", complaintPersistenceEvent, e);
            }
        }
        eventPublisher.publishEvent(complaintPersistenceEvent);
    }

    public void publishComplaintWorkflowEvent(Complaint source, Authentication authentication, String userIpAddress, boolean successful)
    {
        ComplaintApprovalWorkflowRequestedEvent requestEvent = new ComplaintApprovalWorkflowRequestedEvent(source);
        requestEvent.setIpAddress(userIpAddress);
        requestEvent.setUserId(authentication.getName());
        eventPublisher.publishEvent(requestEvent);
    }

    public void publishComplaintSearchResultEvent(ComplaintListView source, Authentication authentication, String userIpAddress)
    {
        ComplaintSearchResultEvent event = new ComplaintSearchResultEvent(source);

        String user = authentication.getName();
        event.setUserId(user);
        event.setIpAddress(userIpAddress);

        eventPublisher.publishEvent(event);

    }

    public void publishFindComplaintByIdEvent(Complaint source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        FindComplaintByIdEvent event = new FindComplaintByIdEvent(source);

        String user = authentication.getName();
        event.setUserId(user);
        event.setIpAddress(ipAddress);
        event.setSucceeded(succeeded);

        eventPublisher.publishEvent(event);
    }

    public void publishComplaintClosedEvent(Complaint source, String userId, boolean succeeded, Date closeDate)
    {
        ComplaintClosedEvent event = new ComplaintClosedEvent(source, succeeded, userId, closeDate);
        eventPublisher.publishEvent(event);
    }

    public void publishComplaintFileAddedEvent(Complaint source, String userId, boolean succeeded)
    {

        ComplaintFileAddedEvent event = new ComplaintFileAddedEvent(source);
        event.setSucceeded(succeeded);
        event.setUserId(userId);
        eventPublisher.publishEvent(event);
    }

    public void publishComplaintModified(Complaint in, String ipAddress, String eventStatus)
    {
        ComplaintModifiedEvent event = new ComplaintModifiedEvent(in);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setEventStatus(eventStatus);
        eventPublisher.publishEvent(event);
    }

    public void publishParticipantsModifiedInComplaint(AcmParticipant participant, Complaint in, String ipAddress, String eventStatus)
    {
        ComplaintParticipantsModifiedEvent event = new ComplaintParticipantsModifiedEvent(participant);
        event.setEventStatus(eventStatus);
        event.setSucceeded(true);
        event.setIpAddress(ipAddress);
        event.setParentObjectId(in.getComplaintId());
        event.setParentObjectType(in.getObjectType());
        event.setParentObjectName(in.getComplaintNumber());
        eventPublisher.publishEvent(event);
    }

    public void publishComplaintUpdated(Complaint in, String userId)
    {
        ComplaintUpdatedEvent event = new ComplaintUpdatedEvent(in);
        event.setUserId(userId);
        eventPublisher.publishEvent(event);
    }

    public void setAcmDiffService(AcmDiffService acmDiffService)
    {
        this.acmDiffService = acmDiffService;
    }
}
