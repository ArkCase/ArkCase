/**
 * 
 */
package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.dao.AcmObjectHistoryDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileAssigneeChangeCheckerTest extends EasyMockSupport {

	private CaseFileAssigneeChangeChecker checker;
	private AcmObjectHistoryDao mockAcmObjectHistoryDao;
	private AcmObjectHistoryEventPublisher mockAcmObjectHistoryEventPublisher;
	private AcmAssignmentDao mockAcmAssignmentDao;
	
	@Before
	public void setUp() throws Exception 
	{
		checker = new CaseFileAssigneeChangeChecker();
		
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
		String objectType = "CASE_FILE";
		String newAssignee = "user-one";
		String oldAssignee = "user-two";
		
		AcmObjectHistory currentHistory = new AcmObjectHistory();
		currentHistory.setObjectType(objectType);
		
		String currentHistoryJson = "{\"id\":" + objectId.toString() + ",\"className\": \"com.armedia.acm.plugins.casefile.model.CaseFile\",\"caseNumber\":\"" + objectName + "\",\"title\":\"" + objectTitle + "\",\"participants\":[{\"className\":\"com.armedia.acm.services.participants.model.AcmParticipant\",\"participantType\":\"assignee\",\"participantLdapId\":\"" + newAssignee + "\"}]}";
		currentHistory.setObjectString(currentHistoryJson);
		
		AcmObjectHistory previousHistory = new AcmObjectHistory();
		previousHistory.setObjectType(objectType);
		
		String previousHistoryJson = "{\"id\":" + objectId.toString() + ",\"className\": \"com.armedia.acm.plugins.casefile.model.CaseFile\", \"caseNumber\":\"" + objectName + "\",\"title\":\"" + objectTitle + "\",\"participants\":[{\"className\":\"com.armedia.acm.services.participants.model.AcmParticipant\",\"participantType\":\"assignee\",\"participantLdapId\":\"" + oldAssignee + "\"}]}";
		previousHistory.setObjectString(previousHistoryJson);
		
		AcmObjectHistoryEvent event = new AcmObjectHistoryEvent(currentHistory);
		
		AcmAssignment assignment = new AcmAssignment();
		
		Capture<AcmAssignment> captureAssignment = new Capture<AcmAssignment>();
		
		// We don't need to check this capture .... for that I am using multiple times below
		Capture<String> captureString = new Capture<String>();
		
		expect(mockAcmObjectHistoryDao.safeFindLastInsertedByObjectIdAndObjectType(1234L, objectType)).andReturn(previousHistory);
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
