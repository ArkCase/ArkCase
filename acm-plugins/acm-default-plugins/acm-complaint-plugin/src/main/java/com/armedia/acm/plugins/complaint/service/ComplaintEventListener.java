package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import microsoft.exchange.webservices.data.enumeration.DeleteMode;
import org.springframework.context.ApplicationListener;

import java.util.List;

public class ComplaintEventListener implements ApplicationListener<AcmObjectHistoryEvent>
{

    private AcmObjectHistoryService acmObjectHistoryService;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private ComplaintEventPublisher complaintEventPublisher;
    private AcmAssignmentDao acmAssignmentDao;
    private OutlookContainerCalendarService calendarService;
    private boolean shouldDeleteCalendarFolder;

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event)
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

                AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService().
                        getAcmObjectHistory(updatedComplaint.getComplaintId(), ComplaintConstants.OBJECT_TYPE);

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
                        String calId = updatedComplaint.getContainer().getCalendarFolderId();
                        if (updatedComplaint.getStatus().equals(ComplaintConstants.COMPLAINT_STATUS_CLOSED) &&
                                shouldDeleteCalendarFolder && calId != null){

                            //delete shared calendar if complaint closed
                            getCalendarService().deleteFolder(updatedComplaint.getContainer().getContainerObjectId(),
                                    calId, DeleteMode.MoveToDeletedItems);
                        }
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "status.changed");
                    }

                    if (isLocationChanged(existing, updatedComplaint))
                    {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "location.updated");
                    }

                    checkParticipants(existing, updatedComplaint, event.getIpAddress());
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
        if (location != null)
        {
            return !location.equals(updatedLocation);
        } else if (updatedLocation != null)
        {
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

    private void checkParticipants(Complaint complaint, Complaint updatedComplaint, String ipAddress)
    {
        List<AcmParticipant> existing = complaint.getParticipants();
        List<AcmParticipant> updated = updatedComplaint.getParticipants();

        for (AcmParticipant participant : existing)
        {
            if (!updated.contains(participant))
            {
                // participant deleted
                getComplaintEventPublisher().publishParticipantsModifiedInComplaint(participant, updatedComplaint, ipAddress, "deleted");
            }
        }

        for (AcmParticipant participant : updated)
        {
            if (!existing.contains(participant))
            {
                // participant added
                getComplaintEventPublisher().publishParticipantsModifiedInComplaint(participant, updatedComplaint, ipAddress, "added");
            }
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

    public OutlookContainerCalendarService getCalendarService()
    {
        return calendarService;
    }

    public void setCalendarService(OutlookContainerCalendarService calendarService)
    {
        this.calendarService = calendarService;
    }

    public boolean isShouldDeleteCalendarFolder()
    {
        return shouldDeleteCalendarFolder;
    }

    public void setShouldDeleteCalendarFolder(boolean shouldDeleteCalendarFolder)
    {
        this.shouldDeleteCalendarFolder = shouldDeleteCalendarFolder;
    }
}
