package com.armedia.acm.plugins.consultation.service;

import com.armedia.acm.calendar.config.model.PurgeOptions;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.utils.ParticipantUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

public class ConsultationEventListener implements ApplicationListener<AcmObjectHistoryEvent>
{
    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());

    private AcmObjectHistoryService acmObjectHistoryService;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private CaseFileEventUtility caseFileEventUtility;
    private AcmAssignmentDao acmAssignmentDao;
    private OutlookContainerCalendarService calendarService;
    private boolean shouldDeleteCalendarFolder;
    private List<String> caseFileStatusClosed;
    private ObjectConverter objectConverter;

    private OutlookCalendarAdminServiceExtension calendarAdminService;

    private AcmOutlookFolderCreatorDao folderCreatorDao;

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event)
    {
        if (event != null)
        {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();

            boolean isCaseFile = checkExecution(acmObjectHistory.getObjectType());

            if (isCaseFile)
            {
                // Converter for JSON string to Object
                AcmUnmarshaller converter = getObjectConverter().getJsonUnmarshaller();

                String jsonUpdatedCaseFile = acmObjectHistory.getObjectString();
                CaseFile updatedCaseFile = converter.unmarshall(jsonUpdatedCaseFile, CaseFile.class);

                AcmAssignment acmAssignment = createAcmAssignment(updatedCaseFile);

                AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService().getAcmObjectHistory(updatedCaseFile.getId(),
                        CaseFileConstants.OBJECT_TYPE);

                if (acmObjectHistoryExisting != null)
                {

                    String json = acmObjectHistoryExisting.getObjectString();
                    CaseFile existing = converter.unmarshall(json, CaseFile.class);

                    if (existing != null)
                    {
                        acmAssignment.setOldAssignee(ParticipantUtils.getAssigneeIdFromParticipants(existing.getParticipants()));

                        if (isPriorityChanged(existing, updatedCaseFile))
                        {
                            getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "priority.changed");
                        }

                        if (isDetailsChanged(existing, updatedCaseFile))
                        {
                            getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "details.changed");
                        }

                        String title = existing.getTitle();
                        String updatedTitle = updatedCaseFile.getTitle();
                        if (!Objects.equals(title, updatedTitle))
                        {
                            getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "title.changed",
                                    "Case File Title changed from " + title + " to " + updatedTitle);
                        }

                        String owningGroup = ParticipantUtils.getOwningGroupIdFromParticipants(existing.getParticipants());
                        String updatedOwningGroup = ParticipantUtils.getOwningGroupIdFromParticipants(updatedCaseFile.getParticipants());
                        if (!Objects.equals(owningGroup, updatedOwningGroup))
                        {
                            AcmParticipant updatedParticipant = updatedCaseFile.getParticipants().stream()
                                    .filter(p -> "owning group".equals(p.getParticipantType())).findFirst().orElse(null);
                            getCaseFileEventUtility().raiseParticipantsModifiedInCaseFile(updatedParticipant, updatedCaseFile,
                                    event.getIpAddress(), "changed",
                                    "Owning Group Changed from " + owningGroup + " to " + updatedOwningGroup);
                        }

                        checkPersonAssociation(existing, updatedCaseFile, event.getIpAddress());

                        checkParticipants(existing, updatedCaseFile, event.getIpAddress());

                        if (isStatusChanged(existing, updatedCaseFile))
                        {
                            String calId = updatedCaseFile.getContainer().getCalendarFolderId();
                            if (shouldDeleteOnClose() && calId != null && caseFileStatusClosed.contains(updatedCaseFile.getStatus()))
                            {

                                // delete shared calendar if case closed
                                Optional<AcmOutlookUser> user = calendarAdminService
                                        .getEventListenerOutlookUser(CaseFileConstants.OBJECT_TYPE);
                                // if integration is not enabled the user will be null.
                                if (user.isPresent())
                                {
                                    getCalendarService().deleteFolder(user.get(), updatedCaseFile.getContainer(),
                                            DeleteMode.MoveToDeletedItems);
                                    folderCreatorDao.deleteObjectReference(updatedCaseFile.getId(), updatedCaseFile.getObjectType());
                                }
                            }
                            getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "status.changed",
                                        "from " + existing.getStatus() + " to " + updatedCaseFile.getStatus());

                        }
                    }
                }

                if (isAssigneeChanged(acmAssignment))
                {
                    // Save assignment change in the database
                    AcmAssignment assignmentSaved = getAcmAssignmentDao().save(acmAssignment);

                    // Raise an event
                    getAcmObjectHistoryEventPublisher().publishAssigneeChangeEvent(assignmentSaved, event.getUserId(),
                            event.getIpAddress());
                }
            }
        }
    }

    private boolean shouldDeleteOnClose()
    {
        boolean purgeOption;
        try
        {
            purgeOption = PurgeOptions.CLOSED.equals(calendarAdminService.readConfiguration(false)
                    .getConfiguration(CaseFileConstants.OBJECT_TYPE).getPurgeOptions());
        }
        catch (CalendarConfigurationException e)
        {
            purgeOption = true;
        }
        return purgeOption && shouldDeleteCalendarFolder;
    }

    public boolean isAssigneeChanged(AcmAssignment assignment)
    {
        String newAssignee = assignment.getNewAssignee();
        String oldAssignee = assignment.getOldAssignee();
        return !Objects.equals(newAssignee, oldAssignee);
    }

    private AcmAssignment createAcmAssignment(CaseFile updatedCaseFile)
    {
        AcmAssignment assignment = new AcmAssignment();
        assignment.setObjectId(updatedCaseFile.getId());
        assignment.setObjectTitle(updatedCaseFile.getTitle());
        assignment.setObjectName(updatedCaseFile.getCaseNumber());
        assignment.setNewAssignee(ParticipantUtils.getAssigneeIdFromParticipants(updatedCaseFile.getParticipants()));
        assignment.setObjectType(CaseFileConstants.OBJECT_TYPE);

        return assignment;
    }

    private boolean isPriorityChanged(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        String updatedPriority = updatedCaseFile.getPriority();
        String priority = caseFile.getPriority();
        return !Objects.equals(updatedPriority, priority);
    }

    private boolean isDetailsChanged(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        String updatedDetails = updatedCaseFile.getDetails();
        String details = caseFile.getDetails();
        return !Objects.equals(details, updatedDetails);
    }

    public void checkPersonAssociation(CaseFile existingCaseFile, CaseFile updatedCaseFile, String ipAddress)
    {
        List<PersonAssociation> existingPersons = existingCaseFile.getPersonAssociations();
        List<PersonAssociation> updatedPersons = updatedCaseFile.getPersonAssociations();

        for (PersonAssociation person : existingPersons)
        {
            if (!updatedPersons.contains(person))
            {
                getCaseFileEventUtility().raisePersonAssociationsDeletedEvent(person,updatedCaseFile, ipAddress);
            }
        }

        for (PersonAssociation person : updatedPersons)
        {
            if (!existingPersons.contains(person))
            {
                getCaseFileEventUtility().raisePersonAssociationsAddEvent(person, updatedCaseFile, ipAddress);
            }
        }
    }

    public void checkParticipants(CaseFile caseFile, CaseFile updatedCaseFile, String ipAddress)
    {
        List<AcmParticipant> existing = caseFile.getParticipants();
        List<AcmParticipant> updated = updatedCaseFile.getParticipants();

        for (AcmParticipant participant : existing)
        {
            if (!updated.contains(participant))
            {
                // participant deleted
                getCaseFileEventUtility().raiseParticipantsModifiedInCaseFile(participant, updatedCaseFile, ipAddress, "deleted");
            }
        }

        for (AcmParticipant participant : updated)
        {
            if (!existing.contains(participant))
            {
                // participant added
                getCaseFileEventUtility().raiseParticipantsModifiedInCaseFile(participant, updatedCaseFile, ipAddress, "added");
            }
        }

        for (AcmParticipant existingParticipant : existing)
        {
            for (AcmParticipant updatedParticipant : updated)
            {
                if (existingParticipant.getId() != null && updatedParticipant.getId() != null)
                {
                    if (existingParticipant.getId().equals(updatedParticipant.getId()))
                    {
                        if (!existingParticipant.getParticipantType().equals(updatedParticipant.getParticipantType()))
                        {
                            // participant changed
                            getCaseFileEventUtility().raiseParticipantsModifiedInCaseFile(updatedParticipant, updatedCaseFile, ipAddress,
                                    "changed");
                        }
                    }
                }
            }
        }
    }

    private boolean isStatusChanged(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        String updatedStatus = updatedCaseFile.getStatus();
        String status = caseFile.getStatus();
        return !Objects.equals(updatedStatus, status);
    }

    private boolean checkExecution(String objectType)
    {

        return objectType.equals(CaseFileConstants.OBJECT_TYPE);
    }

    public AcmObjectHistoryService getAcmObjectHistoryService()
    {

        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService)
    {

        this.acmObjectHistoryService = acmObjectHistoryService;
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {

        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {

        this.caseFileEventUtility = caseFileEventUtility;
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

    public void setCaseFileStatusClosed(String caseFileStatusClosed)
    {
        this.caseFileStatusClosed = Arrays.asList(caseFileStatusClosed.split(","));
    }

    /**
     * @param calendarAdminService
     *            the calendarAdminService to set
     */
    public void setCalendarAdminService(OutlookCalendarAdminServiceExtension calendarAdminService)
    {
        this.calendarAdminService = calendarAdminService;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    /**
     * @param folderCreatorDao
     *            the folderCreatorDao to set
     */
    public void setFolderCreatorDao(AcmOutlookFolderCreatorDao folderCreatorDao)
    {
        this.folderCreatorDao = folderCreatorDao;
    }
}
