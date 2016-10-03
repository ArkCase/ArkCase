package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import org.springframework.context.ApplicationListener;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ComplaintEventListener implements ApplicationListener<AcmObjectHistoryEvent>
{

    private AcmObjectHistoryService acmObjectHistoryService;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private ComplaintEventPublisher complaintEventPublisher;
    private AcmAssignmentDao acmAssignmentDao;
    private OutlookContainerCalendarService calendarService;
    private boolean shouldDeleteCalendarFolder;
    private String complaintStatusClosed;

    private String ipAddress;

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event)
    {
        if (event != null)
        {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();

            boolean isComplaint = checkExecution(acmObjectHistory.getObjectType());

            if (isComplaint)
            {

                ipAddress = event.getIpAddress();

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
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, ipAddress, "priority.changed");
                    }

                    if (isDetailsChanged(existing, updatedComplaint))
                    {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, ipAddress, "details.changed");
                    }

                    if (isStatusChanged(existing, updatedComplaint))
                    {
                        String calId = updatedComplaint.getContainer().getCalendarFolderId();
                        if (updatedComplaint.getStatus().equals(complaintStatusClosed) &&
                                shouldDeleteCalendarFolder && calId != null)
                        {

                            //delete shared calendar if complaint closed
                            getCalendarService().deleteFolder(updatedComplaint.getContainer().getContainerObjectId(),
                                    calId, DeleteMode.MoveToDeletedItems);
                        }
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, ipAddress, "status.changed");
                    }

                    if (isLocationChanged(existing, updatedComplaint))
                    {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, ipAddress, "location.updated");
                    }

                    if (isPersonAliasUpdated(existing, updatedComplaint))
                    {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, ipAddress, "personAlias.updated");
                    }

                    checkParticipants(existing, updatedComplaint, event.getIpAddress());
                }

                if (isAssigneeChanged(acmAssignment))
                {
                    // Save assignment change in the database
                    getAcmAssignmentDao().save(acmAssignment);

                    // Raise an event
                    getAcmObjectHistoryEventPublisher().publishAssigneeChangeEvent(acmAssignment, event.getUserId(), ipAddress);
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

    private boolean isPersonAliasUpdated(Complaint complaint, Complaint updatedComplaint)
    {
        Set<PersonAssociation> existingPersonAssociation = new HashSet<>(complaint.getPersonAssociations());
        Set<PersonAssociation> updatedPersonAssociation = new HashSet<>(updatedComplaint.getPersonAssociations());
        Map<Long, Person> updatedPersons = updatedPersonAssociation.stream()
                .collect(Collectors.toMap(pa -> pa.getPerson().getId(), pa -> pa.getPerson()));

        boolean isPersonAliasAddedOrRemoved = false;
        for (PersonAssociation pa : existingPersonAssociation)
        {
            Person person = pa.getPerson();
            Person matchingPerson = updatedPersons.get(person.getId());
            List<PersonAlias> existingPersonAliases = person.getPersonAliases();
            List<PersonAlias> updatedPersonAliases = matchingPerson.getPersonAliases();

            if (isPersonAliasAdded(existingPersonAliases, updatedPersonAliases))
            {
                getComplaintEventPublisher().publishComplaintModified(updatedComplaint, ipAddress, "personAlias.added");
                isPersonAliasAddedOrRemoved = true;
            }
            if (isPersonAliasRemoved(existingPersonAliases, updatedPersonAliases))
            {
                getComplaintEventPublisher().publishComplaintModified(updatedComplaint, ipAddress, "personAlias.deleted");
                isPersonAliasAddedOrRemoved = true;
            }
            if (isPersonAliasAddedOrRemoved)
            {
                return false;
            } else if (isPersonAliasEdited(existingPersonAliases, updatedPersonAliases))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isPersonAliasEdited(List<PersonAlias> existingPersonAliases, List<PersonAlias> updatedPersonAliases)
    {
        for (PersonAlias personAlias : updatedPersonAliases)
        {
            for (PersonAlias exPersonAlias : existingPersonAliases)
            {
                if (!personAlias.equals(exPersonAlias))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPersonAliasAdded(List<PersonAlias> existing, List<PersonAlias> updated)
    {
        for (PersonAlias personAlias : updated)
        {
            if (!existing.contains(personAlias))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isPersonAliasRemoved(List<PersonAlias> existing, List<PersonAlias> updated)
    {

        for (PersonAlias personAlias : existing)
        {
            if (!updated.contains(personAlias))
            {
                return true;
            }
        }

        return false;
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

    public String getComplaintStatusClosed()
    {
        return complaintStatusClosed;
    }

    public void setComplaintStatusClosed(String complaintStatusClosed)
    {
        this.complaintStatusClosed = complaintStatusClosed;
    }
}
