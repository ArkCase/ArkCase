package com.armedia.acm.plugins.consultation.listener;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.utility.ConsultationEventUtility;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class ConsultationEventListener implements ApplicationListener<AcmObjectHistoryEvent>
{
    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());

    private AcmObjectHistoryService acmObjectHistoryService;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private ConsultationEventUtility consultationEventUtility;
    private AcmAssignmentDao acmAssignmentDao;
    private OutlookContainerCalendarService calendarService;
    private boolean shouldDeleteCalendarFolder;
    private List<String> consultationStatusClosed;
    private ObjectConverter objectConverter;

    private OutlookCalendarAdminServiceExtension calendarAdminService;

    private AcmOutlookFolderCreatorDao folderCreatorDao;

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event)
    {
        if (event != null)
        {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();

            boolean isConsultation = checkExecution(acmObjectHistory.getObjectType());

            if (isConsultation)
            {
                // Converter for JSON string to Object
                AcmUnmarshaller converter = getObjectConverter().getJsonUnmarshaller();

                String jsonUpdatedConsultation = acmObjectHistory.getObjectString();
                Consultation updatedConsultation = converter.unmarshall(jsonUpdatedConsultation, Consultation.class);

                AcmAssignment acmAssignment = createAcmAssignment(updatedConsultation);

                AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService().getAcmObjectHistory(updatedConsultation.getId(),
                        ConsultationConstants.OBJECT_TYPE);

                if (acmObjectHistoryExisting != null)
                {

                    String json = acmObjectHistoryExisting.getObjectString();
                    Consultation existing = converter.unmarshall(json, Consultation.class);

                    if (existing != null)
                    {
                        acmAssignment.setOldAssignee(ParticipantUtils.getAssigneeIdFromParticipants(existing.getParticipants()));

                        if (isPriorityChanged(existing, updatedConsultation))
                        {
                            getConsultationEventUtility().raiseConsultationModifiedEvent(updatedConsultation, event.getIpAddress(),
                                    "priority.changed");
                        }

                        if (isDetailsChanged(existing, updatedConsultation))
                        {
                            getConsultationEventUtility().raiseConsultationModifiedEvent(updatedConsultation, event.getIpAddress(),
                                    "details.changed");
                        }

                        checkTitle(event, updatedConsultation, existing);

                        checkOwningGroup(event, updatedConsultation, existing);

                        checkPersonAssociation(existing, updatedConsultation, event.getIpAddress());

                        checkParticipants(existing, updatedConsultation, event.getIpAddress());

                        if (isStatusChanged(existing, updatedConsultation))
                        {
                            String calId = updatedConsultation.getContainer().getCalendarFolderId();
                            if (shouldDeleteOnClose() && calId != null
                                    && consultationStatusClosed.contains(updatedConsultation.getStatus()))
                            {

                                // delete shared calendar if consultation closed
                                Optional<AcmOutlookUser> user = calendarAdminService
                                        .getEventListenerOutlookUser(ConsultationConstants.OBJECT_TYPE);
                                // if integration is not enabled the user will be null.
                                if (user.isPresent())
                                {
                                    getCalendarService().deleteFolder(user.get(), updatedConsultation.getContainer(),
                                            DeleteMode.MoveToDeletedItems);
                                    folderCreatorDao.deleteObjectReference(updatedConsultation.getId(),
                                            updatedConsultation.getObjectType());
                                }
                            }
                            getConsultationEventUtility().raiseConsultationModifiedEvent(updatedConsultation, event.getIpAddress(),
                                    "status.changed",
                                    "from " + existing.getStatus() + " to " + updatedConsultation.getStatus());

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
                    .getConfiguration(ConsultationConstants.OBJECT_TYPE).getPurgeOptions());
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

    private AcmAssignment createAcmAssignment(Consultation updatedConsultation)
    {
        AcmAssignment assignment = new AcmAssignment();
        assignment.setObjectId(updatedConsultation.getId());
        assignment.setObjectTitle(updatedConsultation.getTitle());
        assignment.setObjectName(updatedConsultation.getConsultationNumber());
        assignment.setNewAssignee(ParticipantUtils.getAssigneeIdFromParticipants(updatedConsultation.getParticipants()));
        assignment.setObjectType(ConsultationConstants.OBJECT_TYPE);

        return assignment;
    }

    private boolean isPriorityChanged(Consultation consultation, Consultation updatedConsultation)
    {
        String updatedPriority = updatedConsultation.getPriority();
        String priority = consultation.getPriority();
        return !Objects.equals(updatedPriority, priority);
    }

    private boolean isDetailsChanged(Consultation consultation, Consultation updatedConsultation)
    {
        String updatedDetails = updatedConsultation.getDetails();
        String details = consultation.getDetails();
        return !Objects.equals(details, updatedDetails);
    }

    private void checkOwningGroup(AcmObjectHistoryEvent event, Consultation updatedConsultation, Consultation existing)
    {
        String owningGroup = ParticipantUtils.getOwningGroupIdFromParticipants(existing.getParticipants());
        String updatedOwningGroup = ParticipantUtils
                .getOwningGroupIdFromParticipants(updatedConsultation.getParticipants());

        if (!Objects.equals(owningGroup, updatedOwningGroup))
        {
            AcmParticipant updatedParticipant = updatedConsultation.getParticipants().stream()
                    .filter(p -> "owning group".equals(p.getParticipantType())).findFirst().orElse(null);
            getConsultationEventUtility().raiseParticipantsModifiedInConsultation(updatedParticipant, updatedConsultation,
                    event.getIpAddress(), "changed",
                    "Owning Group Changed from " + owningGroup + " to " + updatedOwningGroup);
        }
    }

    private void checkTitle(AcmObjectHistoryEvent event, Consultation updatedConsultation, Consultation existing)
    {
        String title = existing.getTitle();
        String updatedTitle = updatedConsultation.getTitle();

        if (!Objects.equals(title, updatedTitle))
        {
            getConsultationEventUtility().raiseConsultationModifiedEvent(updatedConsultation, event.getIpAddress(),
                    "title.changed",
                    "Consultation Title changed from " + title + " to " + updatedTitle);
        }
    }

    public void checkPersonAssociation(Consultation existingConsultation, Consultation updatedConsultation, String ipAddress)
    {
        List<PersonAssociation> existingPersons = existingConsultation.getPersonAssociations();
        List<PersonAssociation> updatedPersons = updatedConsultation.getPersonAssociations();

        for (PersonAssociation person : existingPersons)
        {
            if (!updatedPersons.contains(person))
            {
                getConsultationEventUtility().raisePersonAssociationsDeletedEvent(person, updatedConsultation, ipAddress);
            }
        }

        for (PersonAssociation person : updatedPersons)
        {
            if (!existingPersons.contains(person))
            {
                getConsultationEventUtility().raisePersonAssociationsAddEvent(person, updatedConsultation, ipAddress);
            }
        }
    }

    public void checkParticipants(Consultation consultation, Consultation updatedConsultation, String ipAddress)
    {
        List<AcmParticipant> existing = consultation.getParticipants();
        List<AcmParticipant> updated = updatedConsultation.getParticipants();

        for (AcmParticipant participant : existing)
        {
            if (!updated.contains(participant))
            {
                // participant deleted
                getConsultationEventUtility().raiseParticipantsModifiedInConsultation(participant, updatedConsultation, ipAddress,
                        "deleted");
            }
        }

        for (AcmParticipant participant : updated)
        {
            if (!existing.contains(participant))
            {
                // participant added
                getConsultationEventUtility().raiseParticipantsModifiedInConsultation(participant, updatedConsultation, ipAddress, "added");
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
                            getConsultationEventUtility().raiseParticipantsModifiedInConsultation(updatedParticipant, updatedConsultation,
                                    ipAddress,
                                    "changed");
                        }
                    }
                }
            }
        }
    }

    private boolean isStatusChanged(Consultation consultation, Consultation updatedConsultation)
    {
        String updatedStatus = updatedConsultation.getStatus();
        String status = consultation.getStatus();
        return !Objects.equals(updatedStatus, status);
    }

    private boolean checkExecution(String objectType)
    {
        return objectType.equals(ConsultationConstants.OBJECT_TYPE);
    }

    public AcmObjectHistoryService getAcmObjectHistoryService()
    {

        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService)
    {

        this.acmObjectHistoryService = acmObjectHistoryService;
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

    public ConsultationEventUtility getConsultationEventUtility()
    {
        return consultationEventUtility;
    }

    public void setConsultationEventUtility(ConsultationEventUtility consultationEventUtility)
    {
        this.consultationEventUtility = consultationEventUtility;
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

    public List<String> getConsultationStatusClosed()
    {
        return consultationStatusClosed;
    }

    public void setConsultationStatusClosed(List<String> consultationStatusClosed)
    {
        this.consultationStatusClosed = consultationStatusClosed;
    }
}
