package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
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
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    private ComplaintEventListener complaintEventListener;

    @Before
    public void setUp()
    {
        mockAcmObjectHistoryService = createMock(AcmObjectHistoryService.class);
        mockAcmObjectHistoryEventPublisher = createMock(AcmObjectHistoryEventPublisher.class);
        mockComplaintEventPublisher = createMock(ComplaintEventPublisher.class);
        mockAcmAssignmentDao = createMock(AcmAssignmentDao.class);
        mockCalendarService = createMock(OutlookContainerCalendarService.class);

        complaintEventListener = new ComplaintEventListener();
        complaintEventListener.setAcmObjectHistoryService(mockAcmObjectHistoryService);
        complaintEventListener.setAcmObjectHistoryEventPublisher(mockAcmObjectHistoryEventPublisher);
        complaintEventListener.setComplaintEventPublisher(mockComplaintEventPublisher);
        complaintEventListener.setAcmAssignmentDao(mockAcmAssignmentDao);
        complaintEventListener.setCalendarService(mockCalendarService);
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
        complaint.setLocation(getAddress());
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
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
        Complaint jsonComplaint = getComplaint();
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        //change assignee
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
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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
    public void testPriorityIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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

        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "priority.changed", jsonComplaint);
    }

    @Test
    public void testLocationIsUpdated()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
        Complaint jsonComplaint = getComplaint();
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        // set different location's info
        jsonComplaint.getLocation().setState("USA");
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectString(currentJsonObject);


        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "location.updated", jsonComplaint);
    }

    @Test
    public void testLocationIsUpdatedWhenPreviouslyNull()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
        Complaint jsonComplaint = getComplaint();
        // set null as primary location value
        jsonComplaint.setLocation(null);
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        jsonComplaint.setLocation(getAddress());
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "location.updated", jsonComplaint);
    }

    @Test
    public void testStatusIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
        Complaint jsonComplaint = getComplaint();
        String currentJsonObject = acmMarshaller.marshal(jsonComplaint);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(ComplaintConstants.OBJECT_TYPE);
        //set different status
        jsonComplaint.setStatus("ACTIVE");
        currentJsonObject = acmMarshaller.marshal(jsonComplaint);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "status.changed", jsonComplaint);
    }

    @Test
    public void testCalendarFolderDeleted()
    {
        // allow calendar deleting
        complaintEventListener.setShouldDeleteCalendarFolder(true);
        complaintEventListener.setComplaintStatusClosed("CLOSED");

        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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
    public void testDetailsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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

        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "details.changed", jsonComplaint);
    }

    @Test
    public void testDetailsChangedWhenPreviouslyNull()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
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

        runAndTestComplaintModifiedEvent(currentHistory, previousHistory, "details.changed", jsonComplaint);
    }

    public void verifyEvent(String ipAddress, Complaint complaintCapture, Complaint jsonComplaint)
    {
        assertEquals(IP_ADDRESS, ipAddress);
        assertNotNull(complaintCapture);
        assertEquals(jsonComplaint.getId(), complaintCapture.getId());
        assertEquals(jsonComplaint.getLocation(), complaintCapture.getLocation());
        assertEquals(jsonComplaint.getDetails(), complaintCapture.getDetails());
        assertEquals(jsonComplaint.getStatus(), complaintCapture.getStatus());
        assertEquals(jsonComplaint.getPriority(), complaintCapture.getPriority());
        assertEquals(jsonComplaint.getParticipants(), complaintCapture.getParticipants());
        assertEquals(jsonComplaint.getComplaintTitle(), complaintCapture.getComplaintTitle());
        assertEquals(jsonComplaint.getComplaintNumber(), complaintCapture.getComplaintNumber());
    }

    public void runAndTestComplaintModifiedEvent(AcmObjectHistory currentHistory, AcmObjectHistory previousHistory,
                                                 String statusToCheck, Complaint complaint)
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ComplaintConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<Complaint> complaintCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventStatusCapture = Capture.newInstance();

        mockComplaintEventPublisher.publishComplaintModified(capture(complaintCapture), capture(ipAddressCapture),
                capture(eventStatusCapture));
        expectLastCall().once();

        replayAll();
        complaintEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(statusToCheck, eventStatusCapture.getValue());
        verifyEvent(ipAddressCapture.getValue(), complaintCapture.getValue(), complaint);
    }

    public void runAndTestComplaintModifiedEventStatusClosed(AcmObjectHistory currentHistory, AcmObjectHistory previousHistory,
                                                             String statusToCheck, Complaint complaint)
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, ComplaintConstants.OBJECT_TYPE)).andReturn(previousHistory);


        Capture<Complaint> complaintCapture = Capture.newInstance();
        Capture<String> ipAddressCapture = Capture.newInstance();
        Capture<String> eventStatusCapture = Capture.newInstance();
        Capture<Long> containerIdCapture = Capture.newInstance();
        Capture<String> calendarIdCapture = Capture.newInstance();

        mockCalendarService.deleteFolder(capture(containerIdCapture), capture(calendarIdCapture), eq(DeleteMode.MoveToDeletedItems));
        expectLastCall().once();

        mockComplaintEventPublisher.publishComplaintModified(capture(complaintCapture), capture(ipAddressCapture),
                capture(eventStatusCapture));
        expectLastCall().once();

        replayAll();
        complaintEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(statusToCheck, eventStatusCapture.getValue());
        verifyEvent(ipAddressCapture.getValue(), complaintCapture.getValue(), complaint);
        assertEquals(complaint.getContainer().getContainerObjectId(), containerIdCapture.getValue());
        assertEquals(complaint.getContainer().getCalendarFolderId(), calendarIdCapture.getValue());
    }
}
