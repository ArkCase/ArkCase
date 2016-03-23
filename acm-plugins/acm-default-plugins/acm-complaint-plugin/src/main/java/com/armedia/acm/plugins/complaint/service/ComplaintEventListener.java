package com.armedia.acm.plugins.complaint.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.services.participants.model.AcmParticipant;

public class ComplaintEventListener implements ApplicationListener<AcmObjectHistoryEvent>
{

    private AcmObjectHistoryService acmObjectHistoryService;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private ComplaintEventPublisher complaintEventPublisher;
    private AcmAssignmentDao acmAssignmentDao;

    @Override public void onApplicationEvent(AcmObjectHistoryEvent event)
    {
        if (event != null)
        {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();

            boolean isComplaint = checkExecution(acmObjectHistory.getObjectType());

            if (isComplaint)
            {
                // Converter for JSON string to Object
                AcmUnmarshaller converter = ObjectConverter.createJSONUnmarshaller();

                String jsonUpdatedComplaint = acmObjectHistory.getObjectString();
                Complaint updatedComplaint = (Complaint) converter.unmarshall(jsonUpdatedComplaint, Complaint.class);

                AcmAssignment acmAssignment = createAcmAssignment(updatedComplaint);

                AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService().getAcmObjectHistory(updatedComplaint.getComplaintId(), ComplaintConstants.OBJECT_TYPE);

                if (acmObjectHistoryExisting != null)
                {

                    String json = acmObjectHistoryExisting.getObjectString();
                    Complaint existing = (Complaint) converter.unmarshall(json, Complaint.class);

                    acmAssignment.setOldAssignee(ParticipantUtils.getAssigneeIdFromParticipants(existing.getParticipants()));

                    if (isPriorityChanged(existing, updatedComplaint))
                    {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "priority.changed");
                    }

                    if (isDetailsChanged(existing, updatedComplaint))
                    {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "details.changed");
                    }

                    if (isStatusChanged(existing, updatedComplaint))
                    {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "status.changed");
                    }

                    if (isLocationChanged(existing, updatedComplaint))
                    {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "location.updated");
                    }

                    checkParticipants(existing, updatedComplaint);
                }

                if (isAssigneeChanged(acmAssignment))
                {
                    // Save assignment change in the database
                    getAcmAssignmentDao().save(acmAssignment);

                    // Raise an event
                    getAcmObjectHistoryEventPublisher().publishAssigneeChangeEvent(acmAssignment, event.getUserId(), event.getIpAddress());
                }
            }
        }
    }

    public boolean isAssigneeChanged(AcmAssignment assignment)
    {
        if (assignment.getNewAssignee() != null && assignment.getOldAssignee() != null)
        {
            if (assignment.getNewAssignee().equals(assignment.getOldAssignee()))
            {
                return false;
            }
        }

        if (assignment.getNewAssignee() == null && assignment.getOldAssignee() == null)
        {
            return false;
        }

        return true;
    }

    private AcmAssignment createAcmAssignment(Complaint updatedComplaint)
    {
        AcmAssignment assignment = new AcmAssignment();
        assignment.setObjectId(updatedComplaint.getComplaintId());
        assignment.setObjectTitle(updatedComplaint.getComplaintTitle());
        assignment.setObjectName(updatedComplaint.getComplaintNumber());
        assignment.setNewAssignee(ParticipantUtils.getAssigneeIdFromParticipants(updatedComplaint.getParticipants()));
        assignment.setObjectType(ComplaintConstants.OBJECT_TYPE);

        return assignment;
    }

    private boolean isPriorityChanged(Complaint complaint, Complaint updatedComplaint)
    {
        String updatedPriority = updatedComplaint.getPriority();
        String priority = complaint.getPriority();
        return !updatedPriority.equals(priority);
    }

    private boolean isLocationChanged(Complaint complaint, Complaint updatedComplaint)
    {
        PostalAddress updatedLocation = updatedComplaint.getLocation();
        PostalAddress location = complaint.getLocation();
        if(location != null){
            return !location.equals(updatedLocation);
        }else if (updatedLocation != null){
            return true;
        }
        return false;
    }

    private boolean isDetailsChanged(Complaint complaint, Complaint updatedComplaint)
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

    private void checkParticipants(Complaint complaint, Complaint updatedComplaint)
    {
        List<AcmParticipant> existing = complaint.getParticipants();
        List<AcmParticipant> updated = updatedComplaint.getParticipants();

        Set<AcmParticipant> es = new HashSet<>(existing);
        Set<AcmParticipant> us = new HashSet<>(updated);

        if (us.addAll(es))
        {
            // participants deleted
            getComplaintEventPublisher().publishComplaintModified(updatedComplaint, "", "participants.deleted");
        }
    }

    private boolean isStatusChanged(Complaint complaint, Complaint updatedComplaint)
    {
        String updatedStatus = updatedComplaint.getStatus();
        String status = complaint.getStatus();
        return !updatedStatus.equals(status);
    }

    private boolean checkExecution(String objectType)
    {

        return objectType.equals(ComplaintConstants.OBJECT_TYPE);
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

    public AcmObjectHistoryEventPublisher getAcmObjectHistoryEventPublisher()
    {
        return acmObjectHistoryEventPublisher;
    }

    public void setAcmObjectHistoryEventPublisher(AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher)
    {
        this.acmObjectHistoryEventPublisher = acmObjectHistoryEventPublisher;
    }

    public AcmAssignmentDao getAcmAssignmentDao()
    {
        return acmAssignmentDao;
    }

    public void setAcmAssignmentDao(AcmAssignmentDao acmAssignmentDao)
    {
        this.acmAssignmentDao = acmAssignmentDao;
    }
}
