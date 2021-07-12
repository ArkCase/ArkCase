package com.armedia.acm.plugins.complaint.service;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.utils.ParticipantUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

public class ComplaintEventListener implements ApplicationListener<AcmObjectHistoryEvent>
{
    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());

    private AcmObjectHistoryService acmObjectHistoryService;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private ComplaintEventPublisher complaintEventPublisher;
    private AcmAssignmentDao acmAssignmentDao;
    private OutlookContainerCalendarService calendarService;
    private boolean shouldDeleteCalendarFolder;
    private List<String> complaintStatusClosed;
    private ObjectConverter objectConverter;

    private OutlookCalendarAdminServiceExtension calendarAdminService;

    private AcmOutlookFolderCreatorDao folderCreatorDao;

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event)
    {
        if (event != null)
        {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();

            boolean isComplaint = checkExecution(acmObjectHistory.getObjectType());

            if (isComplaint)
            {

                String ipAddress = event.getIpAddress();

                // Converter for JSON string to Object
                AcmUnmarshaller converter = getObjectConverter().getJsonUnmarshaller();

                String jsonUpdatedComplaint = acmObjectHistory.getObjectString();
                Complaint updatedComplaint = converter.unmarshall(jsonUpdatedComplaint, Complaint.class);

                AcmAssignment acmAssignment = createAcmAssignment(updatedComplaint);

                AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService()
                        .getAcmObjectHistory(updatedComplaint.getComplaintId(), ComplaintConstants.OBJECT_TYPE);

                if (acmObjectHistoryExisting != null)
                {
                    String json = acmObjectHistoryExisting.getObjectString();
                    Complaint existing = converter.unmarshall(json, Complaint.class);

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
                        if (shouldDeleteOnClose() && calId != null && complaintStatusClosed.contains(updatedComplaint.getStatus()))
                        {

                            // delete shared calendar if complaint closed
                            Optional<AcmOutlookUser> user = calendarAdminService
                                    .getEventListenerOutlookUser(ComplaintConstants.OBJECT_TYPE);
                            // if integration is not enabled the user will be null.
                            if (user.isPresent())
                            {
                                getCalendarService().deleteFolder(user.get(), updatedComplaint.getContainer(),
                                        DeleteMode.MoveToDeletedItems);

                                folderCreatorDao.deleteObjectReference(updatedComplaint.getId(), updatedComplaint.getObjectType());
                            }
                        }
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, ipAddress, "status.changed",
                                "from " + existing.getStatus() + " to " + updatedComplaint.getStatus());
                    }

                    if (isLocationChanged(existing, updatedComplaint))
                    {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, ipAddress, "location.updated");
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

    protected boolean shouldDeleteOnClose()
    {
        boolean purgeOption;
        try
        {
            purgeOption = PurgeOptions.CLOSED.equals(calendarAdminService.readConfiguration(false)
                    .getConfiguration(ComplaintConstants.OBJECT_TYPE).getPurgeOptions());
        }
        catch (CalendarConfigurationException e)
        {
            purgeOption = true;
        }
        return purgeOption && shouldDeleteCalendarFolder;
    }

    public boolean isAssigneeChanged(AcmAssignment assignment)
    {

        return !Objects.equals(assignment.getNewAssignee(), assignment.getOldAssignee());

    }

    protected AcmAssignment createAcmAssignment(Complaint updatedComplaint)
    {
        AcmAssignment assignment = new AcmAssignment();
        assignment.setObjectId(updatedComplaint.getComplaintId());
        assignment.setObjectTitle(updatedComplaint.getComplaintTitle());
        assignment.setObjectName(updatedComplaint.getComplaintNumber());
        assignment.setNewAssignee(ParticipantUtils.getAssigneeIdFromParticipants(updatedComplaint.getParticipants()));
        assignment.setObjectType(ComplaintConstants.OBJECT_TYPE);

        return assignment;
    }

    protected boolean isPriorityChanged(Complaint complaint, Complaint updatedComplaint)
    {
        String updatedPriority = updatedComplaint.getPriority();
        String priority = complaint.getPriority();

        return !Objects.equals(updatedPriority, priority);
    }

    protected boolean isLocationChanged(Complaint complaint, Complaint updatedComplaint)
    {
        boolean isAddressAddedOrRemoved = false;
        List<PostalAddress> existingAddresses = complaint.getAddresses();
        List<PostalAddress> updatedAddresses = updatedComplaint.getAddresses();
        Set<Long> updatedIds = updatedAddresses.stream()
                .map(PostalAddress::getId)
                .collect(Collectors.toSet());
        Set<Long> existingIds = existingAddresses.stream()
                .map(PostalAddress::getId)
                .collect(Collectors.toSet());

        if (isObjectAdded(existingIds, updatedIds))
        {
            isAddressAddedOrRemoved = true;
        }
        if (isObjectRemoved(existingIds, updatedIds))
        {
            isAddressAddedOrRemoved = true;
        }
        if (!isAddressAddedOrRemoved)
        {
            if (isPostalAddressEdited(existingAddresses, updatedAddresses))
            {
                return true;
            }
        }
        return isAddressAddedOrRemoved;
    }

    private boolean isPostalAddressEdited(List<PostalAddress> existingAddresses, List<PostalAddress> updatedAddresses)
    {
        List<PostalAddress> sortedExisting = existingAddresses.stream().sorted(Comparator.comparing(PostalAddress::getId))
                .collect(Collectors.toList());

        List<PostalAddress> sortedUpdated = updatedAddresses.stream().sorted(Comparator.comparing(PostalAddress::getId))
                .collect(Collectors.toList());

        return IntStream.range(0, sortedExisting.size())
                .anyMatch(i -> !isPostalAddressChanged(sortedExisting.get(i), sortedUpdated.get(i)));
    }

    private boolean isPostalAddressChanged(PostalAddress ex, PostalAddress up)
    {
        return Objects.equals(ex.getType(), up.getType())
                && Objects.equals(ex.getStreetAddress(), up.getStreetAddress())
                && Objects.equals(ex.getCity(), up.getCity())
                && Objects.equals(ex.getCountry(), up.getCountry())
                && Objects.equals(ex.getZip(), up.getZip())
                && Objects.equals(ex.getState(), up.getState());
    }

    private boolean isObjectAdded(Set<Long> existingIds, Set<Long> updatedIds)
    {
        return updatedIds.stream().anyMatch(id -> !existingIds.contains(id));
    }

    private boolean isObjectRemoved(Set<Long> existingIds, Set<Long> updatedIds)
    {
        return existingIds.stream().anyMatch(id -> !updatedIds.contains(id));
    }

    protected boolean isDetailsChanged(Complaint complaint, Complaint updatedComplaint)
    {
        String updatedDetails = updatedComplaint.getDetails();
        String details = complaint.getDetails();
        return !Objects.equals(details, updatedDetails);

    }

    protected void checkParticipants(Complaint complaint, Complaint updatedComplaint, String ipAddress)
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

    protected boolean isStatusChanged(Complaint complaint, Complaint updatedComplaint)
    {
        String updatedStatus = updatedComplaint.getStatus();
        String status = complaint.getStatus();
        return !Objects.equals(updatedStatus, status);
    }

    protected boolean checkExecution(String objectType)
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

    public List<String> getComplaintStatusClosed() {
        return complaintStatusClosed;
    }

    public void setComplaintStatusClosed(List<String> complaintStatusClosed) {
        this.complaintStatusClosed = complaintStatusClosed;
    }

    public void setComplaintStatusClosed(String complaintStatusClosed)
    {
        this.complaintStatusClosed = Arrays.asList(complaintStatusClosed.split(","));
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

}
