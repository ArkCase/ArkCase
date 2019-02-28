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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;
import com.armedia.acm.service.outlook.service.impl.OutlookCalendarAdminService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantConstants;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

public class ComplaintEventListenerTest extends EasyMockSupport
{
    static final Long OBJECT_ID = 1234L;
    static final String OBJECT_NAME = "20150210_1234";
    static final String OBJECT_TITLE = "Title";
    static final String NEW_ASSIGNEE = "new-user";
    static final String OLD_ASSIGNEE = "old-user";
    static final String PRIORITY = "Low";
    static final String DETAILS = "details";
    static final String STATUS = "DRAFT";
    static final String IP_ADDRESS = "127.0.0.1";
    static final String USER_ID = "ann-acm";
    private AcmObjectHistoryService mockAcmObjectHistoryService;
    private AcmObjectHistoryEventPublisher mockAcmObjectHistoryEventPublisher;
    private ComplaintEventPublisher mockComplaintEventPublisher;
    private AcmAssignmentDao mockAcmAssignmentDao;
    private OutlookContainerCalendarService mockCalendarService;
    private OutlookCalendarAdminServiceExtension mockedCalendarAdminService;
    private AcmOutlookUser mockedOutlookUser;
    private ComplaintEventListener complaintEventListener;
    private CalendarConfigurationsByObjectType mockedCalendarConfigurationType;
    private CalendarConfiguration mockedCalendarConfiguration;
    private AcmOutlookFolderCreatorDao mockedFolderCreatorDao;

    @Before
    public void setUp()
    {
        mockAcmObjectHistoryService = createMock(AcmObjectHistoryService.class);
        mockAcmObjectHistoryEventPublisher = createMock(AcmObjectHistoryEventPublisher.class);
        mockComplaintEventPublisher = createMock(ComplaintEventPublisher.class);
        mockAcmAssignmentDao = createMock(AcmAssignmentDao.class);
        mockCalendarService = createMock(OutlookContainerCalendarService.class);
        mockedCalendarAdminService = createMock(OutlookCalendarAdminService.class);
        mockedOutlookUser = createMock(AcmOutlookUser.class);
        mockedCalendarConfigurationType = createMock(CalendarConfigurationsByObjectType.class);
        mockedCalendarConfiguration = createMock(CalendarConfiguration.class);
        mockedFolderCreatorDao = createMock(AcmOutlookFolderCreatorDao.class);

        complaintEventListener = new ComplaintEventListener();
        complaintEventListener.setAcmObjectHistoryService(mockAcmObjectHistoryService);
        complaintEventListener.setAcmObjectHistoryEventPublisher(mockAcmObjectHistoryEventPublisher);
        complaintEventListener.setComplaintEventPublisher(mockComplaintEventPublisher);
        complaintEventListener.setAcmAssignmentDao(mockAcmAssignmentDao);
        complaintEventListener.setCalendarService(mockCalendarService);
        complaintEventListener.setCalendarAdminService(mockedCalendarAdminService);
        complaintEventListener.setComplaintStatusClosed("CLOSED");
        complaintEventListener.setObjectConverter(ObjectConverter.createObjectConverterForTests());
        complaintEventListener.setFolderCreatorDao(mockedFolderCreatorDao);
    }

    public Complaint getComplaint()
    {
        AcmParticipant participant = new AcmParticipant();
        participant.setId(12L);
        participant.setObjectType(ParticipantConstants.OBJECT_TYPE);
        participant.setParticipantType("assignee");
        participant.setParticipantLdapId(OLD_ASSIGNEE);
        List<AcmParticipant> participants = new ArrayList<>();
        participants.add(participant);

        AcmContainer container = new AcmContainer();
        container.setContainerObjectId(1L);
        container.setCalendarFolderId("cf1");

        Complaint complaint = new Complaint();
        complaint.setComplaintId(OBJECT_ID);
        complaint.setComplaintNumber(OBJECT_NAME);
        complaint.setComplaintTitle(OBJECT_TITLE);
        complaint.setPriority(PRIORITY);
        complaint.setDetails(DETAILS);
        complaint.setStatus(STATUS);
        complaint.setAddresses(Arrays.asList(getAddress()));
        complaint.setParticipants(participants);
        complaint.setContainer(container);
        return complaint;
    }

    public PostalAddress getAddress()
    {
        PostalAddress address = new PostalAddress();
        address.setId(123L);
        address.setStatus("ACTIVE");
        address.setCity("Skopje");
        address.setState("RM");
        return address;
    }

    @Test
    public void testEventIsNull()
    {
        complaintEventListener.onApplicationEvent(null);
    }

    @Test
    public void testSourceIsNotComplaint()
    {
        AcmObjectHistory currentHistory = new AcmObjectHistory();
        // setting some other object type (NOT COMPLAINT)
        currentHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        complaintEventListener.onApplicationEvent(event);
    }

    @Test
    public void testAssigneeIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Complaint jsonComplaint = getComplaint();
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        // change assignee
        jsonComplaint.getParticipants().get(0).setParticipantLdapId(NEW_ASSIGNEE);
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        AcmAssignment assignment = new AcmAssignment();
        Capture<AcmAssignment> captureAssignment = Capture.newInstance();
        Capture<String> captureUserId = Capture.newInstance();
        Capture<String> captureIpAddress = Capture.newInstance();

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ComplaintConstants.OBJECT_TYPE)).andReturn(previousHistory);
        expect(mockAcmAssignmentDao.save(capture(captureAssignment))).andReturn(assignment);

        mockAcmObjectHistoryEventPublisher.publishAssigneeChangeEvent(capture(captureAssignment), capture(captureUserId),
                capture(captureIpAddress));
        expectLastCall().anyTimes();

        replayAll();
        complaintEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(OBJECT_ID, captureAssignment.getValue().getObjectId());
        assertEquals(OBJECT_NAME, captureAssignment.getValue().getObjectName());
        assertEquals(OBJECT_TITLE, captureAssignment.getValue().getObjectTitle());
        assertEquals(ComplaintConstants.OBJECT_TYPE, captureAssignment.getValue().getObjectType());
        assertEquals(NEW_ASSIGNEE, captureAssignment.getValue().getNewAssignee());
        assertEquals(OLD_ASSIGNEE, captureAssignment.getValue().getOldAssignee());
        assertEquals(IP_ADDRESS, captureIpAddress.getValue());
        assertEquals(USER_ID, captureUserId.getValue());
    }

    // test when participant is deleted but it's not an assignee
    @Test
    public void testParticipantIsDeleted()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Complaint jsonComplaint = getComplaint();
        AcmParticipant participant = new AcmParticipant();
        participant.setObjectType(ParticipantConstants.OBJECT_TYPE);
        participant.setParticipantType("reader");
        participant.setId(12345L);
        participant.setParticipantLdapId("nana-acm");
        jsonComplaint.getParticipants().add(participant);
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        // delete participant
        jsonComplaint.getParticipants().remove(1);
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ComplaintConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<Complaint> complaintCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventsStatusCapture = Capture.newInstance();

        mockComplaintEventPublisher.publishParticipantsModifiedInComplaint(eq(participant), capture(complaintCapture),
                capture(ipAddressCapture), capture(eventsStatusCapture));
        expectLastCall().anyTimes();

        replayAll();
        complaintEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals("deleted", eventsStatusCapture.getValue());
        verifyEvent(ipAddressCapture.getValue(), complaintCapture.getValue(), jsonComplaint);
    }

    @Test
    public void testParticipantIsAdded()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Complaint jsonComplaint = getComplaint();
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        // add participant
        AcmParticipant participant = new AcmParticipant();
        participant.setObjectType(ParticipantConstants.OBJECT_TYPE);
        participant.setParticipantType("reader");
        participant.setId(12345L);
        participant.setParticipantLdapId("nana-acm");
        jsonComplaint.getParticipants().add(participant);
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ComplaintConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<Complaint> complaintCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventsStatusCapture = Capture.newInstance();

        mockComplaintEventPublisher.publishParticipantsModifiedInComplaint(eq(participant), capture(complaintCapture),
                capture(ipAddressCapture), capture(eventsStatusCapture));
        expectLastCall().anyTimes();

        replayAll();
        complaintEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals("added", eventsStatusCapture.getValue());
        verifyEvent(ipAddressCapture.getValue(), complaintCapture.getValue(), jsonComplaint);
    }

    @Test
    public void testPriorityIsChanged() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Complaint jsonComplaint = getComplaint();
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        jsonComplaint.setPriority("Medium");
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "priority.changed", null, jsonComplaint, false);
    }

    @Test
    public void testLocationIsUpdated() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Complaint jsonComplaint = getComplaint();
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        // set different location's info
        jsonComplaint.getAddresses().get(0).setState("USA");
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "location.updated", null, jsonComplaint, false);
    }

    @Test
    public void testLocationIsUpdatedWhenPreviouslyIsEmpty() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Complaint jsonComplaint = getComplaint();
        // set null as primary location value
        jsonComplaint.setAddresses(new ArrayList<>());
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        jsonComplaint.setAddresses(Arrays.asList(getAddress()));
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "location.updated", null, jsonComplaint, false);
    }

    @Test
    public void testStatusIsChanged() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Complaint jsonComplaint = getComplaint();
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        // set different status
        jsonComplaint.setStatus("ACTIVE");
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "status.changed", "description", jsonComplaint, true);
    }

    @Test
    public void testCalendarFolderDeleted() throws Exception
    {
        // allow calendar deleting
        complaintEventListener.setShouldDeleteCalendarFolder(true);
        complaintEventListener.setComplaintStatusClosed("CLOSED");

        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Complaint jsonComplaint = getComplaint();
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        jsonComplaint.setStatus("CLOSED");
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestComplaintModifiedEventStatusClosed(currentHistory, previousHistory, "status.changed", jsonComplaint);
    }

    @Test
    public void testDetailsChanged() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Complaint jsonComplaint = getComplaint();
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        // set different details
        jsonComplaint.setDetails("");
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "details.changed", null, jsonComplaint, false);
    }

    @Test
    public void testDetailsChangedWhenPreviouslyNull() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Complaint jsonComplaint = getComplaint();
        // set null as primary details value
        jsonComplaint.setDetails(null);
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        jsonComplaint.setDetails(DETAILS);
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "details.changed", null, jsonComplaint, false);
    }

    public void verifyEvent(String ipAddress, Complaint complaintCapture, Complaint jsonComplaint)
    {
        assertEquals(IP_ADDRESS, ipAddress);
        assertNotNull(complaintCapture);
        assertEquals(jsonComplaint.getId(), complaintCapture.getId());
        assertEquals(jsonComplaint.getAddresses(), complaintCapture.getAddresses());
        assertEquals(jsonComplaint.getDetails(), complaintCapture.getDetails());
        assertEquals(jsonComplaint.getStatus(), complaintCapture.getStatus());
        assertEquals(jsonComplaint.getPriority(), complaintCapture.getPriority());
        assertEquals(jsonComplaint.getParticipants(), complaintCapture.getParticipants());
        assertEquals(jsonComplaint.getComplaintTitle(), complaintCapture.getComplaintTitle());
        assertEquals(jsonComplaint.getComplaintNumber(), complaintCapture.getComplaintNumber());
    }

    public void runAndTestComplaintModifiedEvent(AcmObjectHistory currentHistory, AcmObjectHistory previousHistory, String statusToCheck,
            String eventDescription, Complaint complaint, boolean mockCalendarConfiguration) throws Exception
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ComplaintConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<Complaint> complaintCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventStatusCapture = Capture.newInstance();

        if (eventDescription != null)
        {
            Capture<String> eventDescriptionCapture = Capture.newInstance();
            mockComplaintEventPublisher.publishComplaintModified(capture(complaintCapture), capture(ipAddressCapture),
                    capture(eventStatusCapture), capture(eventDescriptionCapture));
        }
        else
        {
            mockComplaintEventPublisher.publishComplaintModified(capture(complaintCapture), capture(ipAddressCapture),
                    capture(eventStatusCapture));
        }
        expectLastCall().once();

        if (mockCalendarConfiguration)
        {
            expect(mockedCalendarAdminService.readConfiguration(false)).andReturn(mockedCalendarConfigurationType);
            expect(mockedCalendarConfigurationType.getConfiguration(ComplaintConstants.OBJECT_TYPE)).andReturn(mockedCalendarConfiguration);
            expect(mockedCalendarConfiguration.getPurgeOptions()).andReturn(PurgeOptions.CLOSED);
        }

        replayAll();
        complaintEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(statusToCheck, eventStatusCapture.getValue());
        verifyEvent(ipAddressCapture.getValue(), complaintCapture.getValue(), complaint);
    }

    public void runAndTestComplaintModifiedEventStatusClosed(AcmObjectHistory currentHistory, AcmObjectHistory previousHistory,
            String statusToCheck, Complaint complaint) throws Exception
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ComplaintConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<Complaint> complaintCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventStatusCapture = Capture.newInstance();
        Capture<String> eventDescriptionCapture = Capture.newInstance();
        Capture<AcmContainer> containerCapture = Capture.newInstance();
        Capture<AcmOutlookUser> calendarOutlookUser = Capture.newInstance();

        mockCalendarService.deleteFolder(capture(calendarOutlookUser), capture(containerCapture), eq(DeleteMode.MoveToDeletedItems));
        expectLastCall().once();

        mockComplaintEventPublisher.publishComplaintModified(capture(complaintCapture), capture(ipAddressCapture),
                capture(eventStatusCapture), capture(eventDescriptionCapture));
        expectLastCall().once();

        expect(mockedCalendarAdminService.getEventListenerOutlookUser(ComplaintConstants.OBJECT_TYPE))
                .andReturn(Optional.of(mockedOutlookUser));

        mockedFolderCreatorDao.deleteObjectReference(OBJECT_ID, ComplaintConstants.OBJECT_TYPE);
        expectLastCall().once();

        expect(mockedCalendarAdminService.readConfiguration(false)).andReturn(mockedCalendarConfigurationType);
        expect(mockedCalendarConfigurationType.getConfiguration(ComplaintConstants.OBJECT_TYPE)).andReturn(mockedCalendarConfiguration);
        expect(mockedCalendarConfiguration.getPurgeOptions()).andReturn(PurgeOptions.CLOSED);

        replayAll();
        complaintEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(statusToCheck, eventStatusCapture.getValue());
        verifyEvent(ipAddressCapture.getValue(), complaintCapture.getValue(), complaint);
        assertEquals(complaint.getContainer(), containerCapture.getValue());
    }
}
