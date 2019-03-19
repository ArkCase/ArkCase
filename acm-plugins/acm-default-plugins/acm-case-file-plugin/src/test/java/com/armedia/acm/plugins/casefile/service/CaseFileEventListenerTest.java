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
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
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
import java.util.List;
import java.util.Optional;

import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

public class CaseFileEventListenerTest extends EasyMockSupport
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
    private CaseFileEventUtility mockCaseFileEventUtility;
    private AcmAssignmentDao mockAcmAssignmentDao;
    private OutlookContainerCalendarService mockCalendarService;
    private OutlookCalendarAdminServiceExtension mockedCalendarAdminService;
    private AcmOutlookUser mockedOutlookUser;
    private CaseFileEventListener caseFileEventListener;
    private CalendarConfigurationsByObjectType mockedCalendarConfigurationType;
    private CalendarConfiguration mockedCalendarConfiguration;
    private AcmOutlookFolderCreatorDao mockedFolderCreatorDao;

    @Before
    public void setUp()
    {
        caseFileEventListener = new CaseFileEventListener();
        caseFileEventListener.setObjectConverter(ObjectConverter.createObjectConverterForTests());
        mockAcmObjectHistoryService = createMock(AcmObjectHistoryService.class);
        mockAcmObjectHistoryEventPublisher = createMock(AcmObjectHistoryEventPublisher.class);
        mockCaseFileEventUtility = createMock(CaseFileEventUtility.class);
        mockAcmAssignmentDao = createMock(AcmAssignmentDao.class);
        mockCalendarService = createMock(OutlookContainerCalendarService.class);
        mockedCalendarAdminService = createMock(OutlookCalendarAdminService.class);
        mockedOutlookUser = createMock(AcmOutlookUser.class);
        mockedCalendarConfigurationType = createMock(CalendarConfigurationsByObjectType.class);
        mockedCalendarConfiguration = createMock(CalendarConfiguration.class);
        mockedFolderCreatorDao = createMock(AcmOutlookFolderCreatorDao.class);

        caseFileEventListener.setAcmObjectHistoryService(mockAcmObjectHistoryService);
        caseFileEventListener.setAcmObjectHistoryEventPublisher(mockAcmObjectHistoryEventPublisher);
        caseFileEventListener.setCaseFileEventUtility(mockCaseFileEventUtility);
        caseFileEventListener.setAcmAssignmentDao(mockAcmAssignmentDao);
        caseFileEventListener.setCalendarService(mockCalendarService);
        caseFileEventListener.setCalendarAdminService(mockedCalendarAdminService);
        caseFileEventListener.setCaseFileStatusClosed("CLOSED");
        caseFileEventListener.setFolderCreatorDao(mockedFolderCreatorDao);
    }

    public CaseFile getCase()
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

        CaseFile caseFile = new CaseFile();
        caseFile.setId(OBJECT_ID);
        caseFile.setCaseNumber(OBJECT_NAME);
        caseFile.setTitle(OBJECT_TITLE);
        caseFile.setPriority(PRIORITY);
        caseFile.setDetails(DETAILS);
        caseFile.setStatus(STATUS);
        caseFile.setParticipants(participants);
        caseFile.setContainer(container);
        return caseFile;
    }

    @Test
    public void testEventIsNull()
    {
        caseFileEventListener.onApplicationEvent(null);
    }

    @Test
    public void testSourceIsNotCaseFile()
    {
        AcmObjectHistory currentHistory = new AcmObjectHistory();
        // setting some other object type (NOT CASE_FILE)
        currentHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        caseFileEventListener.onApplicationEvent(event);
    }

    @Test
    public void testAssigneeIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        CaseFile jsonCaseFile = getCase();
        String currentJsonObject = acmMarshaller.marshal(jsonCaseFile);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        // change assignee
        jsonCaseFile.getParticipants().get(0).setParticipantLdapId(NEW_ASSIGNEE);
        currentJsonObject = acmMarshaller.marshal(jsonCaseFile);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        AcmAssignment assignment = new AcmAssignment();
        Capture<AcmAssignment> captureAssignment = Capture.newInstance();
        Capture<String> captureUserId = Capture.newInstance();
        Capture<String> captureIpAddress = Capture.newInstance();

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, CaseFileConstants.OBJECT_TYPE)).andReturn(previousHistory);
        expect(mockAcmAssignmentDao.save(capture(captureAssignment))).andReturn(assignment);

        mockAcmObjectHistoryEventPublisher.publishAssigneeChangeEvent(eq(assignment), capture(captureUserId), capture(captureIpAddress));
        expectLastCall().anyTimes();

        replayAll();
        caseFileEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(OBJECT_ID, captureAssignment.getValue().getObjectId());
        assertEquals(OBJECT_NAME, captureAssignment.getValue().getObjectName());
        assertEquals(OBJECT_TITLE, captureAssignment.getValue().getObjectTitle());
        assertEquals(CaseFileConstants.OBJECT_TYPE, captureAssignment.getValue().getObjectType());
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
        CaseFile jsonCaseFile = getCase();
        AcmParticipant participant = new AcmParticipant();
        participant.setObjectType(ParticipantConstants.OBJECT_TYPE);
        participant.setParticipantType("reader");
        participant.setId(12345L);
        participant.setParticipantLdapId("nana-acm");
        jsonCaseFile.getParticipants().add(participant);
        String currentJsonObject = acmMarshaller.marshal(jsonCaseFile);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        // delete participant
        jsonCaseFile.getParticipants().remove(1);
        currentJsonObject = acmMarshaller.marshal(jsonCaseFile);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, CaseFileConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<CaseFile> caseCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventsStatusCapture = Capture.newInstance();

        mockCaseFileEventUtility.raiseParticipantsModifiedInCaseFile(eq(participant), capture(caseCapture), capture(ipAddressCapture),
                capture(eventsStatusCapture));
        expectLastCall().anyTimes();

        replayAll();
        caseFileEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals("deleted", eventsStatusCapture.getValue());
        assertEquals(IP_ADDRESS, ipAddressCapture.getValue());
        assertNotNull(caseCapture.getValue());
        assertEquals(jsonCaseFile.getId(), caseCapture.getValue().getId());
    }

    // test when participant is changed
    @Test
    public void testParticipantIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        CaseFile jsonCaseFile = getCase();
        AcmParticipant participant = new AcmParticipant();
        participant.setObjectType(ParticipantConstants.OBJECT_TYPE);
        participant.setParticipantType("reader");
        participant.setId(12345L);
        participant.setParticipantLdapId("nana-acm");
        jsonCaseFile.getParticipants().add(participant);
        String currentJsonObject = acmMarshaller.marshal(jsonCaseFile);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        // change participant
        AcmParticipant participantChanged = jsonCaseFile.getParticipants().get(1);
        participantChanged.setParticipantType("follower");
        currentJsonObject = acmMarshaller.marshal(jsonCaseFile);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, CaseFileConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<CaseFile> caseCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventsStatusCapture = Capture.newInstance();

        mockCaseFileEventUtility.raiseParticipantsModifiedInCaseFile(eq(participant), capture(caseCapture), capture(ipAddressCapture),
                capture(eventsStatusCapture));
        expectLastCall().anyTimes();

        replayAll();
        caseFileEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals("changed", eventsStatusCapture.getValue());
        assertEquals(IP_ADDRESS, ipAddressCapture.getValue());
        assertNotNull(caseCapture.getValue());
        assertEquals(jsonCaseFile.getId(), caseCapture.getValue().getId());
    }

    @Test
    public void testParticipantIsAdded()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        CaseFile jsonCaseFile = getCase();
        String currentJsonObject = acmMarshaller.marshal(jsonCaseFile);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        // add participant
        AcmParticipant participant = new AcmParticipant();
        participant.setObjectType(ParticipantConstants.OBJECT_TYPE);
        participant.setParticipantType("reader");
        participant.setId(12345L);
        participant.setParticipantLdapId("nana-acm");
        jsonCaseFile.getParticipants().add(participant);
        currentJsonObject = acmMarshaller.marshal(jsonCaseFile);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, CaseFileConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<CaseFile> caseCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventsStatusCapture = Capture.newInstance();

        mockCaseFileEventUtility.raiseParticipantsModifiedInCaseFile(eq(participant), capture(caseCapture), capture(ipAddressCapture),
                capture(eventsStatusCapture));
        expectLastCall().anyTimes();

        replayAll();
        caseFileEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals("added", eventsStatusCapture.getValue());
        assertEquals(IP_ADDRESS, ipAddressCapture.getValue());
        assertNotNull(caseCapture.getValue());
        assertEquals(jsonCaseFile.getId(), caseCapture.getValue().getId());
    }

    @Test
    public void testPriorityIsChanged() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        CaseFile jsonCaseFile = getCase();
        String currentJsonObject = acmMarshaller.marshal(jsonCaseFile);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        jsonCaseFile.setPriority("Medium");
        currentJsonObject = acmMarshaller.marshal(jsonCaseFile);
        currentHistory.setObjectString(currentJsonObject);

        runAndCheck(currentHistory, previousHistory, "priority.changed", null, jsonCaseFile, false);
    }

    @Test
    public void testStatusIsChanged() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        CaseFile jsonCaseFile = getCase();
        String currentJsonObject = acmMarshaller.marshal(jsonCaseFile);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        // set different status
        jsonCaseFile.setStatus("ACTIVE");
        currentJsonObject = acmMarshaller.marshal(jsonCaseFile);
        currentHistory.setObjectString(currentJsonObject);

        runAndCheck(currentHistory, previousHistory, "status.changed", "description", jsonCaseFile, true);
    }

    @Test
    public void testCalendarDeleted() throws Exception
    {
        // allow calendar deleting
        caseFileEventListener.setShouldDeleteCalendarFolder(true);
        caseFileEventListener.setCaseFileStatusClosed("CLOSED");

        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        CaseFile jsonCaseFile = getCase();
        String currentJsonObject = acmMarshaller.marshal(jsonCaseFile);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        jsonCaseFile.setStatus("CLOSED");
        currentJsonObject = acmMarshaller.marshal(jsonCaseFile);
        currentHistory.setObjectString(currentJsonObject);

        runAndCheckCaseFileModifiedEventStatusClosed(currentHistory, previousHistory, "status.changed", jsonCaseFile);
    }

    @Test
    public void testDetailsChanged() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        CaseFile jsonCaseFile = getCase();
        String currentJsonObject = acmMarshaller.marshal(jsonCaseFile);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        // set different details
        jsonCaseFile.setDetails("");
        currentJsonObject = acmMarshaller.marshal(jsonCaseFile);
        currentHistory.setObjectString(currentJsonObject);

        runAndCheck(currentHistory, previousHistory, "details.changed", null, jsonCaseFile, false);
    }

    @Test
    public void testDetailsChangedWhenPreviouslyNull() throws Exception
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        CaseFile jsonCaseFile = getCase();
        // set null as primary details value
        jsonCaseFile.setDetails(null);
        String currentJsonObject = acmMarshaller.marshal(jsonCaseFile);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        jsonCaseFile.setDetails(DETAILS);
        currentJsonObject = acmMarshaller.marshal(jsonCaseFile);
        currentHistory.setObjectString(currentJsonObject);

        runAndCheck(currentHistory, previousHistory, "details.changed", null, jsonCaseFile, false);
    }

    public void runAndCheck(AcmObjectHistory currentHistory, AcmObjectHistory previousHistory, String statusToCheck,
            String eventDescription,
            CaseFile caseFile,
            boolean mockCalendarConfiguration)
            throws Exception
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, CaseFileConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<CaseFile> caseCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventStatusCapture = Capture.newInstance();

        if (eventDescription != null)
        {
            Capture<String> eventDescriptionCapture = Capture.newInstance();
            mockCaseFileEventUtility.raiseCaseFileModifiedEvent(capture(caseCapture), capture(ipAddressCapture),
                    capture(eventStatusCapture),
                    capture(eventDescriptionCapture));
        }
        else
        {
            mockCaseFileEventUtility.raiseCaseFileModifiedEvent(capture(caseCapture), capture(ipAddressCapture),
                    capture(eventStatusCapture));
        }

        expectLastCall().once();

        if (mockCalendarConfiguration)
        {
            expect(mockedCalendarAdminService.readConfiguration(false)).andReturn(mockedCalendarConfigurationType);
            expect(mockedCalendarConfigurationType.getConfiguration(CaseFileConstants.OBJECT_TYPE)).andReturn(mockedCalendarConfiguration);
            expect(mockedCalendarConfiguration.getPurgeOptions()).andReturn(PurgeOptions.CLOSED);
        }

        replayAll();
        caseFileEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(statusToCheck, eventStatusCapture.getValue());
        assertEquals(IP_ADDRESS, ipAddressCapture.getValue());
        assertNotNull(caseCapture.getValue());
        assertEquals(caseFile.getId(), caseCapture.getValue().getId());
        assertEquals(caseFile.getStatus(), caseCapture.getValue().getStatus());
        assertEquals(caseFile.getParticipants(), caseCapture.getValue().getParticipants());
        assertEquals(caseFile.getPriority(), caseCapture.getValue().getPriority());
        assertEquals(caseFile.getDetails(), caseCapture.getValue().getDetails());
        assertEquals(caseFile.getCaseNumber(), caseCapture.getValue().getCaseNumber());
    }

    public void runAndCheckCaseFileModifiedEventStatusClosed(AcmObjectHistory currentHistory, AcmObjectHistory previousHistory,
            String statusToCheck, CaseFile caseFile) throws Exception
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, CaseFileConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<CaseFile> caseCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventStatusCapture = Capture.newInstance();
        Capture<String> eventDescriptionCapture = Capture.newInstance();
        Capture<AcmContainer> containerCapture = Capture.newInstance();
        Capture<AcmOutlookUser> calendarOutlookUser = Capture.newInstance();

        mockCalendarService.deleteFolder(capture(calendarOutlookUser), capture(containerCapture), eq(DeleteMode.MoveToDeletedItems));
        expectLastCall().once();

        mockCaseFileEventUtility.raiseCaseFileModifiedEvent(capture(caseCapture), capture(ipAddressCapture), capture(eventStatusCapture),
                capture(eventDescriptionCapture));
        expectLastCall().once();

        expect(mockedCalendarAdminService.getEventListenerOutlookUser(CaseFileConstants.OBJECT_TYPE))
                .andReturn(Optional.of(mockedOutlookUser));

        mockedFolderCreatorDao.deleteObjectReference(OBJECT_ID, CaseFileConstants.OBJECT_TYPE);
        expectLastCall().once();

        expect(mockedCalendarAdminService.readConfiguration(false)).andReturn(mockedCalendarConfigurationType);
        expect(mockedCalendarConfigurationType.getConfiguration(CaseFileConstants.OBJECT_TYPE)).andReturn(mockedCalendarConfiguration);
        expect(mockedCalendarConfiguration.getPurgeOptions()).andReturn(PurgeOptions.CLOSED);

        replayAll();
        caseFileEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(statusToCheck, eventStatusCapture.getValue());
        assertEquals(IP_ADDRESS, ipAddressCapture.getValue());
        assertNotNull(caseCapture.getValue());
        assertEquals(caseFile.getId(), caseCapture.getValue().getId());
        assertEquals(caseFile.getStatus(), caseCapture.getValue().getStatus());
        assertEquals(caseFile.getParticipants(), caseCapture.getValue().getParticipants());
        assertEquals(caseFile.getPriority(), caseCapture.getValue().getPriority());
        assertEquals(caseFile.getDetails(), caseCapture.getValue().getDetails());
        assertEquals(caseFile.getCaseNumber(), caseCapture.getValue().getCaseNumber());
        assertEquals(caseFile.getContainer(), containerCapture.getValue());
    }

}
