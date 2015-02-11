/**
 * 
 */
package com.armedia.acm.plugins.task.service.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.capture;
import static org.junit.Assert.assertEquals;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.armedia.acm.plugins.task.service.TaskAssigneeChangeChecker;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.dao.AcmObjectHistoryDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;

/**
 * @author riste.tutureski
 *
 */
public class TaskAssigneeChangeCheckerTest extends EasyMockSupport {

	private TaskAssigneeChangeChecker checker;
	private AcmObjectHistoryDao mockAcmObjectHistoryDao;
	private AcmObjectHistoryEventPublisher mockAcmObjectHistoryEventPublisher;
	private AcmAssignmentDao mockAcmAssignmentDao;
	
	@Before
	public void setUp() throws Exception 
	{
		checker = new TaskAssigneeChangeChecker();
		
		mockAcmObjectHistoryDao = createMock(AcmObjectHistoryDao.class);
		mockAcmObjectHistoryEventPublisher = createMock(AcmObjectHistoryEventPublisher.class);
		mockAcmAssignmentDao = createMock(AcmAssignmentDao.class);
		
		checker.setAcmObjectHistoryDao(mockAcmObjectHistoryDao);
		checker.setEventPublisher(mockAcmObjectHistoryEventPublisher);
		checker.setAcmAssignmentDao(mockAcmAssignmentDao);
	}

	@Test
	public void testAcmObjectHistoryEvent() 
	{
		Long objectId = 1234L;
		String objectName = "20150210_1234";
		String objectTitle = "Title";
		String objectType = "TASK";
		String newAssignee = "user-one";
		String oldAssignee = "user-two";
		
		AcmObjectHistory currentHistory = new AcmObjectHistory();
		currentHistory.setObjectType(objectType);
		
		String currentHistoryJson = "{\"taskId\":" + objectId.toString() + ",\"businessProcessName\":\"" + objectName + "\",\"title\":\"" + objectTitle + "\",\"assignee\":\"" + newAssignee + "\"}";
		currentHistory.setObjectString(currentHistoryJson);
		
		AcmObjectHistory previousHistory = new AcmObjectHistory();
		previousHistory.setObjectType(objectType);
		
		String previousHistoryJson = "{\"taskId\":" + objectId.toString() + ",\"businessProcessName\":\"" + objectName + "\",\"title\":\"" + objectTitle + "\",\"assignee\":\"" + oldAssignee + "\"}";
		previousHistory.setObjectString(previousHistoryJson);
		
		AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
		
		AcmAssignment assignment = new AcmAssignment();
		
		Capture<AcmAssignment> captureAssignment = new Capture<AcmAssignment>();
		
		// We don't need to check these captures .... for that I am using multiple times below
		Capture<String> captureString = new Capture<String>();
		Capture<Long> captureLong = new Capture<Long>();
		
		expect(mockAcmObjectHistoryDao.safeFindLastInsertedByObjectIdAndObjectType(capture(captureLong), capture(captureString))).andReturn(previousHistory);
		expect(mockAcmAssignmentDao.save(capture(captureAssignment))).andReturn(assignment);
		
		mockAcmObjectHistoryEventPublisher.publishAssigneeChangeEvent(capture(captureAssignment), capture(captureString), capture(captureString));
		expectLastCall().anyTimes();
		
		replayAll();
		
		checker.onApplicationEvent(event);
		
		assertEquals(objectId, captureAssignment.getValue().getObjectId());
		assertEquals(objectName, captureAssignment.getValue().getObjectName());
		assertEquals(objectTitle, captureAssignment.getValue().getObjectTitle());
		assertEquals(objectType, captureAssignment.getValue().getObjectType());
		assertEquals(newAssignee, captureAssignment.getValue().getNewAssignee());
		assertEquals(oldAssignee, captureAssignment.getValue().getOldAssignee());
	}

}
