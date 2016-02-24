package com.armedia.acm.plugins.complaint.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintPersistenceEvent;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.services.participants.model.AcmParticipant;

public class ComplaintEventListener implements ApplicationListener<ComplaintPersistenceEvent>
{

    private static final String OBJECT_TYPE = "COMPLAINT";
    private static final String EVENT_TYPE = "com.armedia.acm.complaint.updated";

    private AcmObjectHistoryService acmObjectHistoryService;
    private ComplaintEventPublisher complaintEventPublisher;

    @Override
    public void onApplicationEvent(ComplaintPersistenceEvent event)
    {
        if (event != null)
        {
            boolean execute = checkExecution(event.getEventType());

            if (execute)
            {
                Complaint updatedComplaint = (Complaint) event.getSource();

                AcmObjectHistory acmObjectHistory = getAcmObjectHistoryService().getAcmObjectHistory(updatedComplaint.getComplaintId(),
                        OBJECT_TYPE);

                String json = acmObjectHistory.getObjectString();

                // Convert JSON string to Object
                AcmUnmarshaller converter = ObjectConverter.createJSONUnmarshaller();
                Complaint exsisting = (Complaint) converter.unmarshall(json, Complaint.class);

                if (priorityChanged(exsisting, updatedComplaint))
                {
                    getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "priority.changed");
                }

                if (detailsChanged(exsisting, updatedComplaint))
                {
                    getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "details.changed");
                }

                if (statusChanged(exsisting, updatedComplaint))
                {
                    getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "status.updated");
                }

                checkParticipants(exsisting, updatedComplaint, event);
            }
        }
    }

    private boolean priorityChanged(Complaint complaint, Complaint updatedComplaint)
    {
        String updatedPriority = updatedComplaint.getPriority();
        String priority = complaint.getPriority();
        return !updatedPriority.equals(priority);
    }

    private boolean detailsChanged(Complaint complaint, Complaint updatedComplaint)
    {
        String updatedDetails = updatedComplaint.getDetails();
        String details = complaint.getDetails();
        if (updatedDetails != null && details != null)
        {
            return !details.equals(updatedDetails);
        } else if (updatedDetails != null)
        {
            return true;
        }
        return false;
    }

    private boolean statusChanged(Complaint complaint, Complaint updatedComplaint)
    {
        String updatedStatus = updatedComplaint.getStatus();
        String status = complaint.getStatus();
        return !updatedStatus.equals(status);
    }

    private void checkParticipants(Complaint complaint, Complaint updatedComplaint, ComplaintPersistenceEvent event)
    {
        List<AcmParticipant> existing = complaint.getParticipants();
        List<AcmParticipant> updated = updatedComplaint.getParticipants();

        Set<AcmParticipant> es = new HashSet<>(existing);
        Set<AcmParticipant> us = new HashSet<>(updated);

        if (es.addAll(us))
        {
            // participants added
            getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "participants.added");
        }
        
        // set is mutable
        es = new HashSet<>(existing);
       
        if (us.addAll(es))
        {
            // participants deleted
            getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "participants.deleted");
        }
    }

    private boolean checkExecution(String eventType)
    {
        return EVENT_TYPE.equals(eventType);
    }

    public AcmObjectHistoryService getAcmObjectHistoryService()
    {
        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService)
    {
        this.acmObjectHistoryService = acmObjectHistoryService;
    }

    public ComplaintEventPublisher getComplaintEventPublisher()
    {
        return complaintEventPublisher;
    }

    public void setComplaintEventPublisher(ComplaintEventPublisher complaintEventPublisher)
    {
        this.complaintEventPublisher = complaintEventPublisher;
    }
}
