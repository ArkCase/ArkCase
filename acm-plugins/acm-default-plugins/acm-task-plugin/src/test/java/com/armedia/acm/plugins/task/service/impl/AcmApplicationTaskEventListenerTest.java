package com.armedia.acm.plugins.task.service.impl;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantConstants;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AcmApplicationTaskEventListenerTest extends EasyMockSupport
{
    static final Long OBJECT_ID = 1234L;
    static final String OBJECT_NAME = String.format("%s_%d", getDate(), OBJECT_ID);
    static final String NEW_ASSIGNEE = "new-user";
    static final String OLD_ASSIGNEE = "old-user";
    static final String PRIORITY = "Low";
    static final String DETAILS = "details";
    static final String REWORK_DETAILS = "rework-instructions";
    static final String EVENT_TYPE = "com.armedia.acm.app.task";
    static final String IP_ADDRESS = "127.0.0.1";
    static final String USER_ID = "ann-acm";
    private AcmObjectHistoryService mockAcmObjectHistoryService;
    private AcmObjectHistoryEventPublisher mockAcmObjectHistoryEventPublisher;
    private TaskEventPublisher mockTaskEventPublisher;
    private AcmAssignmentDao mockAcmAssignmentDao;
    private AcmApplicationTaskEventListener taskEventListener;

    private static String getDate()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat formatter = new SimpleDateFormat(DateFormats.TASK_NAME_DATE_FORMAT);
        return formatter.format(new Date());
    }

    @Before
    public void setUp()
    {
        taskEventListener = new AcmApplicationTaskEventListener();
        taskEventListener.setObjectConverter(ObjectConverter.createObjectConverterForTests());
        mockAcmObjectHistoryService = createMock(AcmObjectHistoryService.class);
        mockAcmObjectHistoryEventPublisher = createMock(AcmObjectHistoryEventPublisher.class);
        mockTaskEventPublisher = createMock(TaskEventPublisher.class);
        mockAcmAssignmentDao = createMock(AcmAssignmentDao.class);

        taskEventListener.setAcmObjectHistoryService(mockAcmObjectHistoryService);
        taskEventListener.setAcmObjectHistoryEventPublisher(mockAcmObjectHistoryEventPublisher);
        taskEventListener.setTaskEventPublisher(mockTaskEventPublisher);
        taskEventListener.setAcmAssignmentDao(mockAcmAssignmentDao);
    }

    private AcmTask getTask()
    {
        AcmParticipant participant = new AcmParticipant();
        participant.setId(12L);
        participant.setObjectType(ParticipantConstants.OBJECT_TYPE);
        participant.setParticipantType("assignee");
        participant.setParticipantLdapId(OLD_ASSIGNEE);
        List<AcmParticipant> participants = new ArrayList<>();
        participants.add(participant);

        AcmTask task = new AcmTask();
        task.setTaskId(OBJECT_ID);
        task.setAssignee(OLD_ASSIGNEE);
        task.setPriority(PRIORITY);
        task.setDetails(DETAILS);
        task.setReworkInstructions(REWORK_DETAILS);
        task.setStatus(TaskConstants.STATE_ACTIVE);
        task.setParticipants(participants);
        task.setDueDate(new Date());
        return task;
    }

    @Test
    public void testEventIsNull()
    {
        taskEventListener.onApplicationEvent(null);
    }

    @Test
    public void testSourceIsNotComplaint()
    {
        AcmObjectHistory currentHistory = new AcmObjectHistory();
        // setting some other object type (NOT TASK)
        currentHistory.setObjectType(ParticipantConstants.OBJECT_TYPE);
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        taskEventListener.onApplicationEvent(event);
    }

    @Test
    public void testAssigneeIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        AcmTask jsonTask = getTask();
        String currentJsonObject = acmMarshaller.marshal(jsonTask);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        // change assignee
        jsonTask.getParticipants().get(0).setParticipantLdapId(NEW_ASSIGNEE);
        jsonTask.setAssignee(NEW_ASSIGNEE);
        currentJsonObject = acmMarshaller.marshal(jsonTask);
        currentHistory.setObjectString(currentJsonObject);

        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        AcmAssignment assignment = new AcmAssignment();
        Capture<AcmAssignment> captureAssignment = Capture.newInstance();
        Capture<String> captureUserId = Capture.newInstance();
        Capture<String> captureIpAddress = Capture.newInstance();

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, TaskConstants.OBJECT_TYPE)).andReturn(previousHistory);
        expect(mockAcmAssignmentDao.save(capture(captureAssignment))).andReturn(assignment);

        mockAcmObjectHistoryEventPublisher.publishAssigneeChangeEvent(capture(captureAssignment), capture(captureUserId),
                capture(captureIpAddress));
        expectLastCall().anyTimes();

        replayAll();
        taskEventListener.onApplicationEvent(event);

        verifyAll();
        assertEquals(OBJECT_ID, captureAssignment.getValue().getObjectId());
        assertEquals(OBJECT_NAME, captureAssignment.getValue().getObjectName());
        assertEquals(TaskConstants.OBJECT_TYPE, captureAssignment.getValue().getObjectType());
        assertEquals(NEW_ASSIGNEE, captureAssignment.getValue().getNewAssignee());
        assertEquals(OLD_ASSIGNEE, captureAssignment.getValue().getOldAssignee());
        assertEquals(IP_ADDRESS, captureIpAddress.getValue());
        assertEquals(USER_ID, captureUserId.getValue());
    }

    @Test
    public void testPriorityIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        AcmTask jsonTask = getTask();
        String currentJsonObject = acmMarshaller.marshal(jsonTask);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        jsonTask.setPriority("Medium");
        currentJsonObject = acmMarshaller.marshal(jsonTask);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestTaskEvent(currentHistory, previousHistory, "priority.changed", jsonTask);
    }

    @Test
    public void testStatusIsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        AcmTask jsonTask = getTask();
        String currentJsonObject = acmMarshaller.marshal(jsonTask);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        // set different status
        jsonTask.setStatus(TaskConstants.STATE_CLOSED);
        currentJsonObject = acmMarshaller.marshal(jsonTask);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestTaskEvent(currentHistory, previousHistory, "status.changed", jsonTask);
    }

    @Test
    public void testDetailsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        AcmTask jsonTask = getTask();
        String currentJsonObject = acmMarshaller.marshal(jsonTask);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        // set different details
        jsonTask.setDetails("");
        currentJsonObject = acmMarshaller.marshal(jsonTask);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestTaskEvent(currentHistory, previousHistory, "details.changed", jsonTask);
    }

    @Test
    public void testDetailsChangedWhenPreviouslyNull()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        AcmTask jsonTask = getTask();
        // set null as primary details value
        jsonTask.setDetails(null);
        String currentJsonObject = acmMarshaller.marshal(jsonTask);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        jsonTask.setDetails(DETAILS);
        currentJsonObject = acmMarshaller.marshal(jsonTask);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestTaskEvent(currentHistory, previousHistory, "details.changed", jsonTask);
    }

    @Test
    public void testReworkDetailsChanged()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        AcmTask jsonTask = getTask();
        String currentJsonObject = acmMarshaller.marshal(jsonTask);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        // set different details
        jsonTask.setReworkInstructions("");
        currentJsonObject = acmMarshaller.marshal(jsonTask);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestTaskEvent(currentHistory, previousHistory, "reworkdetails.changed", jsonTask);
    }

    @Test
    public void testReworkDetailsChangedWhenPreviouslyNull()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        AcmTask jsonTask = getTask();
        // set null as primary details value
        jsonTask.setReworkInstructions(null);
        String currentJsonObject = acmMarshaller.marshal(jsonTask);

        AcmObjectHistory previousHistory = new AcmObjectHistory();
        previousHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        previousHistory.setObjectString(currentJsonObject);

        AcmObjectHistory currentHistory = new AcmObjectHistory();
        currentHistory.setObjectType(TaskConstants.OBJECT_TYPE);
        jsonTask.setReworkInstructions(REWORK_DETAILS);
        currentJsonObject = acmMarshaller.marshal(jsonTask);
        currentHistory.setObjectString(currentJsonObject);

        runAndTestTaskEvent(currentHistory, previousHistory, "reworkdetails.changed", jsonTask);
    }

    public void runAndTestTaskEvent(AcmObjectHistory currentHistory, AcmObjectHistory previousHistory, String statusToCheck, AcmTask task)
    {
        AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
        event.setIpAddress(IP_ADDRESS);
        event.setUserId(USER_ID);

        expect(mockAcmObjectHistoryService.getAcmObjectHistory(OBJECT_ID, TaskConstants.OBJECT_TYPE)).andReturn(previousHistory);

        Capture<AcmApplicationTaskEvent> taskEventCapture = Capture.newInstance();

        mockTaskEventPublisher.publishTaskEvent(capture(taskEventCapture));
        expectLastCall().once();

        replayAll();
        taskEventListener.onApplicationEvent(event);

        verifyAll();
        AcmTask taskCapture = taskEventCapture.getValue().getAcmTask();
        assertEquals(String.format("%s.%s", EVENT_TYPE, statusToCheck), taskEventCapture.getValue().getEventType());
        assertNotNull(taskCapture);
        assertEquals(task.getId(), taskCapture.getId());
        assertEquals(task.getNextAssignee(), taskCapture.getNextAssignee());
        assertEquals(task.getDetails(), taskCapture.getDetails());
        assertEquals(task.getStatus(), taskCapture.getStatus());
        assertEquals(task.getTitle(), taskCapture.getTitle());
        assertEquals(task.getReworkInstructions(), taskCapture.getReworkInstructions());
    }
}
