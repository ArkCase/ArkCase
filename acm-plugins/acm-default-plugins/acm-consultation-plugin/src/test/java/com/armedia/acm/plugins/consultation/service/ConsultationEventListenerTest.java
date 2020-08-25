package com.armedia.acm.plugins.consultation.service;

/*-
 * #%L
 * ACM Default Plugin: Consultation
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

import com.armedia.acm.calendar.config.model.PurgeOptions;
import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.consultation.listener.ConsultationEventListener;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.utility.ConsultationEventUtility;
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

public class ConsultationEventListenerTest extends EasyMockSupport
{
    static final Long OBJECT_ID = 1234L;
    static final String OBJECT_NAME = "20150210_1234";
    static final String OBJECT_TITLE = "Title";
    static final String NEW_ASSIGNEE = "new-user";
    static final String OLD_ASSIGNEE = "old-user";
    static final String PRIORITY = "Low";
    static final String DETAILS = "not much to say";
    static final String STATUS = "DRAFT";
    static final String IP_ADDRESS = "127.0.0.1";
    static final String USER_ID = "ann-acm";
    private AcmObjectHistoryService mockAcmObjectHistoryService;
    private AcmObjectHistoryEventPublisher mockAcmObjectHistoryEventPublisher;
    private ConsultationEventUtility mockConsultationEventUtility;
    private AcmAssignmentDao mockAcmAssignmentDao;
    private OutlookContainerCalendarService mockCalendarService;
    private OutlookCalendarAdminServiceExtension mockedCalendarAdminService;
    private AcmOutlookUser mockedOutlookUser;
    private ConsultationEventListener consultationEventListener;
    private CalendarConfigurationsByObjectType mockedCalendarConfigurationType;
    private CalendarConfiguration mockedCalendarConfiguration;
    private AcmOutlookFolderCreatorDao mockedFolderCreatorDao;

    @Before
    public void setUp()
    {
        consultationEventListener = new ConsultationEventListener();
        consultationEventListener.setObjectConverter(ObjectConverter.createObjectConverterForTests());
        mockAcmObjectHistoryService = createMock(AcmObjectHistoryService.class);
        mockAcmObjectHistoryEventPublisher = createMock(AcmObjectHistoryEventPublisher.class);
        mockConsultationEventUtility = createMock(ConsultationEventUtility.class);
        mockAcmAssignmentDao = createMock(AcmAssignmentDao.class);
        mockCalendarService = createMock(OutlookContainerCalendarService.class);
        mockedCalendarAdminService = createMock(OutlookCalendarAdminService.class);
        mockedOutlookUser = createMock(AcmOutlookUser.class);
        mockedCalendarConfigurationType = createMock(CalendarConfigurationsByObjectType.class);
        mockedCalendarConfiguration = createMock(CalendarConfiguration.class);
        mockedFolderCreatorDao = createMock(AcmOutlookFolderCreatorDao.class);

        consultationEventListener.setAcmObjectHistoryService(mockAcmObjectHistoryService);
        consultationEventListener.setAcmObjectHistoryEventPublisher(mockAcmObjectHistoryEventPublisher);
        consultationEventListener.setConsultationEventUtility(mockConsultationEventUtility);
        consultationEventListener.setAcmAssignmentDao(mockAcmAssignmentDao);
        consultationEventListener.setCalendarService(mockCalendarService);
        consultationEventListener.setCalendarAdminService(mockedCalendarAdminService);
        consultationEventListener.setConsultationStatusClosed(Arrays.asList("CLOSED"));
        consultationEventListener.setFolderCreatorDao(mockedFolderCreatorDao);
    }

    public Consultation createTestConsultation()
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

        Consultation consultation = new Consultation();
        consultation.setId(OBJECT_ID);
        consultation.setConsultationNumber(OBJECT_NAME);
        consultation.setTitle(OBJECT_TITLE);
        consultation.setPriority(PRIORITY);
        consultation.setDetails(DETAILS);
        consultation.setStatus(STATUS);
        consultation.setParticipants(participants);
        consultation.setContainer(container);

        return consultation;
    }

    private AcmParticipant createTestParticipant()
    {
        AcmParticipant participant = new AcmParticipant();
        participant.setObjectType(ParticipantConstants.OBJECT_TYPE);
        participant.setParticipantType("reader");
        participant.setId(12345L);
        participant.setParticipantLdapId("nana-acm");
        return participant;
    }

    @Test
    public void testEventIsNull()
    {
        consultationEventListener.onApplicationEvent(null);
    }

    @Test
    public void testSourceIsNotConsultation()
    {
        AcmObjectHistory currentHistory = new AcmObjectHistory();
        // setting some other object type (NOT CONSULTATION)
        currentHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        consultationEventListener.onApplicationEvent(event);
    }

    @Test
    public void testAssigneeIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Consultation jsonConsultation = createTestConsultation();
        String currentJsonObject = acmMarshaller.marshal(jsonConsultation);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        // change assignee
        jsonConsultation.getParticipants().get(0).setParticipantLdapId(NEW_ASSIGNEE);
        currentJsonObject = acmMarshaller.marshal(jsonConsultation);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        AcmAssignment assignment = new AcmAssignment();
        Capture<AcmAssignment> captureAssignment = Capture.newInstance();
        Capture<String> captureUserId = Capture.newInstance();
        Capture<String> captureIpAddress = Capture.newInstance();

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ConsultationConstants.OBJECT_TYPE)).andReturn(previousHistory);
        expect(mockAcmAssignmentDao.save(capture(captureAssignment))).andReturn(assignment);

        mockAcmObjectHistoryEventPublisher.publishAssigneeChangeEvent(eq(assignment), capture(captureUserId), capture(captureIpAddress));
        expectLastCall().anyTimes();

        replayAll();
        consultationEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(OBJECT_ID, captureAssignment.getValue().getObjectId());
        assertEquals(OBJECT_NAME, captureAssignment.getValue().getObjectName());
        assertEquals(OBJECT_TITLE, captureAssignment.getValue().getObjectTitle());
        assertEquals(ConsultationConstants.OBJECT_TYPE, captureAssignment.getValue().getObjectType());
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

        Consultation jsonConsultation = createTestConsultation();
        AcmParticipant participant = createTestParticipant();

        jsonConsultation.getParticipants().add(participant);
        String currentJsonObject = acmMarshaller.marshal(jsonConsultation);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        // delete participant
        jsonConsultation.getParticipants().remove(1);
        currentJsonObject = acmMarshaller.marshal(jsonConsultation);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ConsultationConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<Consultation> consultationCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventsStatusCapture = Capture.newInstance();

        mockConsultationEventUtility.raiseParticipantsModifiedInConsultation(eq(participant), capture(consultationCapture),
                capture(ipAddressCapture),
                capture(eventsStatusCapture));
        expectLastCall().anyTimes();

        replayAll();
        consultationEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals("deleted", eventsStatusCapture.getValue());
        assertEquals(IP_ADDRESS, ipAddressCapture.getValue());
        assertNotNull(consultationCapture.getValue());
        assertEquals(jsonConsultation.getId(), consultationCapture.getValue().getId());
    }

    @Test
    public void testParticipantIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Consultation jsonConsultation = createTestConsultation();
        AcmParticipant participant = createTestParticipant();
        jsonConsultation.getParticipants().add(participant);
        String currentJsonObject = acmMarshaller.marshal(jsonConsultation);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        // change participant
        AcmParticipant participantChanged = jsonConsultation.getParticipants().get(1);
        participantChanged.setParticipantType("follower");
        currentJsonObject = acmMarshaller.marshal(jsonConsultation);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ConsultationConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<Consultation> consultationCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventsStatusCapture = Capture.newInstance();

        mockConsultationEventUtility.raiseParticipantsModifiedInConsultation(eq(participant), capture(consultationCapture),
                capture(ipAddressCapture),
                capture(eventsStatusCapture));
        expectLastCall().anyTimes();

        replayAll();
        consultationEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals("changed", eventsStatusCapture.getValue());
        assertEquals(IP_ADDRESS, ipAddressCapture.getValue());
        assertNotNull(consultationCapture.getValue());
        assertEquals(jsonConsultation.getId(), consultationCapture.getValue().getId());
    }

    @Test
    public void testParticipantIsAdded()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Consultation jsonConsultation = createTestConsultation();
        String currentJsonObject = acmMarshaller.marshal(jsonConsultation);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        // add participant
        AcmParticipant participant = createTestParticipant();
        jsonConsultation.getParticipants().add(participant);
        currentJsonObject = acmMarshaller.marshal(jsonConsultation);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ConsultationConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<Consultation> consultationCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventsStatusCapture = Capture.newInstance();

        mockConsultationEventUtility.raiseParticipantsModifiedInConsultation(eq(participant), capture(consultationCapture),
                capture(ipAddressCapture),
                capture(eventsStatusCapture));
        expectLastCall().anyTimes();

        replayAll();
        consultationEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals("added", eventsStatusCapture.getValue());
        assertEquals(IP_ADDRESS, ipAddressCapture.getValue());
        assertNotNull(consultationCapture.getValue());
        assertEquals(jsonConsultation.getId(), consultationCapture.getValue().getId());
    }

    @Test
    public void testPriorityIsChanged() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Consultation jsonConsultation = createTestConsultation();
        String currentJsonObject = acmMarshaller.marshal(jsonConsultation);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        jsonConsultation.setPriority("Medium");
        currentJsonObject = acmMarshaller.marshal(jsonConsultation);
        currentHistory.setObjectString(currentJsonObject);

        runAndCheck(currentHistory, previousHistory, "priority.changed", null, jsonConsultation, false);
    }

    @Test
    public void testStatusIsChanged() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Consultation jsonConsultation = createTestConsultation();
        String currentJsonObject = acmMarshaller.marshal(jsonConsultation);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        // set different status
        jsonConsultation.setStatus("ACTIVE");
        currentJsonObject = acmMarshaller.marshal(jsonConsultation);
        currentHistory.setObjectString(currentJsonObject);

        runAndCheck(currentHistory, previousHistory, "status.changed", "description", jsonConsultation, true);
    }

    @Test
    public void testCalendarDeleted() throws Exception
    {
        // allow calendar deleting
        consultationEventListener.setShouldDeleteCalendarFolder(true);
        consultationEventListener.setConsultationStatusClosed(Arrays.asList("CLOSED"));

        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Consultation jsonConsultation = createTestConsultation();
        String currentJsonObject = acmMarshaller.marshal(jsonConsultation);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        jsonConsultation.setStatus("CLOSED");
        currentJsonObject = acmMarshaller.marshal(jsonConsultation);
        currentHistory.setObjectString(currentJsonObject);

        runAndCheckConsultationModifiedEventStatusClosed(currentHistory, previousHistory, "status.changed", jsonConsultation);
    }

    @Test
    public void testDetailsChanged() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Consultation jsonConsultation = createTestConsultation();
        String currentJsonObject = acmMarshaller.marshal(jsonConsultation);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        // set different details
        jsonConsultation.setDetails("");
        currentJsonObject = acmMarshaller.marshal(jsonConsultation);
        currentHistory.setObjectString(currentJsonObject);

        runAndCheck(currentHistory, previousHistory, "details.changed", null, jsonConsultation, false);
    }

    @Test
    public void testDetailsChangedWhenPreviouslyNull() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        Consultation jsonConsultation = createTestConsultation();
        // set null as primary details value
        jsonConsultation.setDetails(null);
        String currentJsonObject = acmMarshaller.marshal(jsonConsultation);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ConsultationConstants.OBJECT_TYPE);
        jsonConsultation.setDetails(DETAILS);
        currentJsonObject = acmMarshaller.marshal(jsonConsultation);
        currentHistory.setObjectString(currentJsonObject);

        runAndCheck(currentHistory, previousHistory, "details.changed", null, jsonConsultation, false);
    }

    public void runAndCheck(AcmObjectHistory currentHistory, AcmObjectHistory previousHistory, String statusToCheck,
            String eventDescription,
            Consultation consultation,
            boolean mockCalendarConfiguration)
            throws Exception
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ConsultationConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<Consultation> consultationCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventStatusCapture = Capture.newInstance();

        if (eventDescription != null)
        {
            Capture<String> eventDescriptionCapture = Capture.newInstance();
            mockConsultationEventUtility.raiseConsultationModifiedEvent(capture(consultationCapture), capture(ipAddressCapture),
                    capture(eventStatusCapture),
                    capture(eventDescriptionCapture));
        }
        else
        {
            mockConsultationEventUtility.raiseConsultationModifiedEvent(capture(consultationCapture), capture(ipAddressCapture),
                    capture(eventStatusCapture));
        }

        expectLastCall().once();

        if (mockCalendarConfiguration)
        {
            expect(mockedCalendarAdminService.readConfiguration(false)).andReturn(mockedCalendarConfigurationType);
            expect(mockedCalendarConfigurationType.getConfiguration(ConsultationConstants.OBJECT_TYPE))
                    .andReturn(mockedCalendarConfiguration);
            expect(mockedCalendarConfiguration.getPurgeOptions()).andReturn(PurgeOptions.CLOSED);
        }

        replayAll();
        consultationEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(statusToCheck, eventStatusCapture.getValue());
        assertEquals(IP_ADDRESS, ipAddressCapture.getValue());
        assertNotNull(consultationCapture.getValue());
        assertEquals(consultation.getId(), consultationCapture.getValue().getId());
        assertEquals(consultation.getStatus(), consultationCapture.getValue().getStatus());
        assertEquals(consultation.getParticipants(), consultationCapture.getValue().getParticipants());
        assertEquals(consultation.getPriority(), consultationCapture.getValue().getPriority());
        assertEquals(consultation.getDetails(), consultationCapture.getValue().getDetails());
        assertEquals(consultation.getConsultationNumber(), consultationCapture.getValue().getConsultationNumber());
    }

    public void runAndCheckConsultationModifiedEventStatusClosed(AcmObjectHistory currentHistory, AcmObjectHistory previousHistory,
            String statusToCheck, Consultation consultation) throws Exception
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ConsultationConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<Consultation> consultationCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventStatusCapture = Capture.newInstance();
        Capture<String> eventDescriptionCapture = Capture.newInstance();
        Capture<AcmContainer> containerCapture = Capture.newInstance();
        Capture<AcmOutlookUser> calendarOutlookUser = Capture.newInstance();

        mockCalendarService.deleteFolder(capture(calendarOutlookUser), capture(containerCapture), eq(DeleteMode.MoveToDeletedItems));
        expectLastCall().once();

        mockConsultationEventUtility.raiseConsultationModifiedEvent(capture(consultationCapture), capture(ipAddressCapture),
                capture(eventStatusCapture),
                capture(eventDescriptionCapture));
        expectLastCall().once();

        expect(mockedCalendarAdminService.getEventListenerOutlookUser(ConsultationConstants.OBJECT_TYPE))
                .andReturn(Optional.of(mockedOutlookUser));

        mockedFolderCreatorDao.deleteObjectReference(OBJECT_ID, ConsultationConstants.OBJECT_TYPE);
        expectLastCall().once();

        expect(mockedCalendarAdminService.readConfiguration(false)).andReturn(mockedCalendarConfigurationType);
        expect(mockedCalendarConfigurationType.getConfiguration(ConsultationConstants.OBJECT_TYPE)).andReturn(mockedCalendarConfiguration);
        expect(mockedCalendarConfiguration.getPurgeOptions()).andReturn(PurgeOptions.CLOSED);

        replayAll();
        consultationEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(statusToCheck, eventStatusCapture.getValue());
        assertEquals(IP_ADDRESS, ipAddressCapture.getValue());
        assertNotNull(consultationCapture.getValue());
        assertEquals(consultation.getId(), consultationCapture.getValue().getId());
        assertEquals(consultation.getStatus(), consultationCapture.getValue().getStatus());
        assertEquals(consultation.getParticipants(), consultationCapture.getValue().getParticipants());
        assertEquals(consultation.getPriority(), consultationCapture.getValue().getPriority());
        assertEquals(consultation.getDetails(), consultationCapture.getValue().getDetails());
        assertEquals(consultation.getConsultationNumber(), consultationCapture.getValue().getConsultationNumber());
        assertEquals(consultation.getContainer(), containerCapture.getValue());
    }

}
