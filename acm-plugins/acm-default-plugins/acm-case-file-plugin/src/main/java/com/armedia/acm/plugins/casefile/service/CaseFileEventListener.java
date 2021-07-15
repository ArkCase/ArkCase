package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
import com.armedia.acm.services.holiday.service.DateTimeService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.utils.ParticipantUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

public class CaseFileEventListener implements ApplicationListener<AcmObjectHistoryEvent>
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
    private DateTimeService dateTimeService;

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
                        if (updatedCaseFile.getDueDate() != null && existing.getDueDate() != null)
                        {
                            if (isDueDateChanged(updatedCaseFile, existing))
                            {
                                String newDate = getDateString(getDateTimeService().fromDateToClientLocalDateTime(updatedCaseFile.getDueDate()));
                                String oldDate = getDateString(getDateTimeService().fromDateToClientLocalDateTime(existing.getDueDate()));
                                String timeZone = getDateTimeService().getDefaultClientZoneId().getId();

                                getCaseFileEventUtility().raiseDueDateUpdatedEvent(updatedCaseFile, oldDate, newDate, timeZone, event.getIpAddress());
                            }
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

    protected boolean shouldDeleteOnClose()
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

    protected AcmAssignment createAcmAssignment(CaseFile updatedCaseFile)
    {
        AcmAssignment assignment = new AcmAssignment();
        assignment.setObjectId(updatedCaseFile.getId());
        assignment.setObjectTitle(updatedCaseFile.getTitle());
        assignment.setObjectName(updatedCaseFile.getCaseNumber());
        assignment.setNewAssignee(ParticipantUtils.getAssigneeIdFromParticipants(updatedCaseFile.getParticipants()));
        assignment.setObjectType(CaseFileConstants.OBJECT_TYPE);

        return assignment;
    }

    private boolean isDueDateChanged(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        return !caseFile.getDueDate().equals(updatedCaseFile.getDueDate());
    }

    protected boolean isPriorityChanged(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        String updatedPriority = updatedCaseFile.getPriority();
        String priority = caseFile.getPriority();
        return !Objects.equals(updatedPriority, priority);
    }

    protected boolean isDetailsChanged(CaseFile caseFile, CaseFile updatedCaseFile)
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

    protected boolean isStatusChanged(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        String updatedStatus = updatedCaseFile.getStatus();
        String status = caseFile.getStatus();
        return !Objects.equals(updatedStatus, status);
    }

    protected boolean checkExecution(String objectType)
    {
        return objectType.equals(CaseFileConstants.OBJECT_TYPE);
    }

    private String getDateString(LocalDateTime date)
    {
        if (date != null)
        {
            DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
            return date.format(datePattern);
        }

        return "None";
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

    public List<String> getCaseFileStatusClosed() {
        return caseFileStatusClosed;
    }

    public void setCaseFileStatusClosed(List<String> caseFileStatusClosed) {
        this.caseFileStatusClosed = caseFileStatusClosed;
    }

    public void setCaseFileStatusClosed(String caseFileStatusClosed) {
        this.caseFileStatusClosed = Arrays.asList(caseFileStatusClosed.split(","));
    }

    public OutlookCalendarAdminServiceExtension getCalendarAdminService() {
        return calendarAdminService;
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

    public AcmOutlookFolderCreatorDao getFolderCreatorDao() {
        return folderCreatorDao;
    }

    /**
     * @param folderCreatorDao
     *            the folderCreatorDao to set
     */
    public void setFolderCreatorDao(AcmOutlookFolderCreatorDao folderCreatorDao)
    {
        this.folderCreatorDao = folderCreatorDao;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
