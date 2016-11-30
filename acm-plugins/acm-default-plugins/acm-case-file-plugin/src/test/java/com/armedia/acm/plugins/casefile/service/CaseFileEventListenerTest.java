package com.armedia.acm.plugins.casefile.service;

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
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantConstants;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    private CaseFileEventListener caseFileEventListener;

    @Before
    public void setUp()
    {
        caseFileEventListener = new CaseFileEventListener();
        mockAcmObjectHistoryService = createMock(AcmObjectHistoryService.class);
        mockAcmObjectHistoryEventPublisher = createMock(AcmObjectHistoryEventPublisher.class);
        mockCaseFileEventUtility = createMock(CaseFileEventUtility.class);
        mockAcmAssignmentDao = createMock(AcmAssignmentDao.class);
        mockCalendarService = createMock(OutlookContainerCalendarService.class);

        caseFileEventListener.setAcmObjectHistoryService(mockAcmObjectHistoryService);
        caseFileEventListener.setAcmObjectHistoryEventPublisher(mockAcmObjectHistoryEventPublisher);
        caseFileEventListener.setCaseFileEventUtility(mockCaseFileEventUtility);
        caseFileEventListener.setAcmAssignmentDao(mockAcmAssignmentDao);
        caseFileEventListener.setCalendarService(mockCalendarService);
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
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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

        mockAcmObjectHistoryEventPublisher.publishAssigneeChangeEvent(eq(assignment), capture(captureUserId),
                capture(captureIpAddress));
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
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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

        mockCaseFileEventUtility.raiseParticipantsModifiedInCaseFile(eq(participant), capture(caseCapture),
                capture(ipAddressCapture), capture(eventsStatusCapture));
        expectLastCall().anyTimes();

        replayAll();
        caseFileEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals("deleted", eventsStatusCapture.getValue());
        assertEquals(IP_ADDRESS, ipAddressCapture.getValue());
        assertNotNull(caseCapture.getValue());
        assertEquals(jsonCaseFile.getId(), caseCapture.getValue().getId());
    }

    @Test
    public void testParticipantIsAdded()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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

        mockCaseFileEventUtility.raiseParticipantsModifiedInCaseFile(eq(participant), capture(caseCapture),
                capture(ipAddressCapture), capture(eventsStatusCapture));
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
    public void testPriorityIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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

        runAndCheck(currentHistory, previousHistory, "priority.changed", jsonCaseFile);
    }

    @Test
    public void testStatusIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
        CaseFile jsonCaseFile = getCase();
        String currentJsonObject = acmMarshaller.marshal(jsonCaseFile);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(CaseFileConstants.OBJECT_TYPE);
        //set different status
        jsonCaseFile.setStatus("ACTIVE");
        currentJsonObject = acmMarshaller.marshal(jsonCaseFile);
        currentHistory.setObjectString(currentJsonObject);

        runAndCheck(currentHistory, previousHistory, "status.changed", jsonCaseFile);
    }

    @Test
    public void testCalendarDeleted()
    {
        //allow calendar deleting
        caseFileEventListener.setShouldDeleteCalendarFolder(true);
        caseFileEventListener.setCaseFileStatusClosed("CLOSED");

        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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
    public void testDetailsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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

        runAndCheck(currentHistory, previousHistory, "details.changed", jsonCaseFile);
    }

    @Test
    public void testDetailsChangedWhenPreviouslyNull()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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

        runAndCheck(currentHistory, previousHistory, "details.changed", jsonCaseFile);
    }

    public void runAndCheck(AcmObjectHistory currentHistory, AcmObjectHistory previousHistory, String statusToCheck,
                            CaseFile caseFile)
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, CaseFileConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<CaseFile> caseCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventStatusCapture = Capture.newInstance();

        mockCaseFileEventUtility.raiseCaseFileModifiedEvent(capture(caseCapture), capture(ipAddressCapture),
                capture(eventStatusCapture));
        expectLastCall().once();

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

    public void runAndCheckCaseFileModifiedEventStatusClosed(AcmObjectHistory currentHistory, AcmObjectHistory previousHistory, String statusToCheck,
                            CaseFile caseFile)
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, CaseFileConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<CaseFile> caseCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventStatusCapture = Capture.newInstance();
        Capture<Long> containerIdCapture = Capture.newInstance();
        Capture<String> calendarIdCapture = Capture.newInstance();

        mockCalendarService.deleteFolder(capture(containerIdCapture), capture(calendarIdCapture), eq(DeleteMode.MoveToDeletedItems));
        expectLastCall().once();

        mockCaseFileEventUtility.raiseCaseFileModifiedEvent(capture(caseCapture), capture(ipAddressCapture),
                capture(eventStatusCapture));
        expectLastCall().once();

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
        assertEquals(caseFile.getContainer().getContainerObjectId(), containerIdCapture.getValue());
        assertEquals(caseFile.getContainer().getCalendarFolderId(), calendarIdCapture.getValue());
    }

}
