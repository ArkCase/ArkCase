/**
 * 
 */
package com.armedia.acm.service.objecthistory.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.service.objecthistory.dao.AcmObjectHistoryDao;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.model.AssigneeChangeInfo;
import com.armedia.acm.services.participants.model.AcmParticipant;

/**
 * @author riste.tutureski
 *
 */
public class AcmAssigneeChangeListener implements ApplicationListener<AcmObjectHistoryEvent> {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private static final String COMPLAINT_OBJECT_TYPE = "COMPLAINT";
	private static final String CASE_FILE_OBJECT_TYPE = "CASE_FILE";
	private static final String TASK_OBJECT_TYPE = "TASK";
	private static final String ASSIGNEE = "assignee";
	
	private AcmObjectHistoryDao acmObjectHistoryDao;
	private AcmObjectHistoryEventPublisher eventPublisher;
	 
	/**
	 * When Object History event is raised, find previous Object History (most resent "modified" date) for the same 
	 * object type (if any). Check the changes of assignee value and raise Assignee Change event if the values are different.
	 * 
	 *  @param event
	 */
	@Override
	public void onApplicationEvent(AcmObjectHistoryEvent event) 
	{
		LOG.debug("Object History Event raised.");

		if (event != null)
		{
			AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();
			
			LOG.debug("Checking Object type ...");
			
			if (COMPLAINT_OBJECT_TYPE.equals(acmObjectHistory.getObjectType()))
			{
				LOG.debug("Complaint Object type.");

				checkAndRaiseAssigneeChangeEventForComplaint(acmObjectHistory);
			}
			else if (CASE_FILE_OBJECT_TYPE.equals(acmObjectHistory.getObjectType()))
			{
				LOG.debug("CaseFile Object type.");

				checkAndRaiseAssigneeChangeEventForCaseFile(acmObjectHistory);
			}
			else if (TASK_OBJECT_TYPE.equals(acmObjectHistory.getObjectType()))
			{
				LOG.debug("Task Object type.");

				checkAndRaiseAssigneeChangeEventForTask(acmObjectHistory);
			}
		}
	}
	
	/**
	 * Check assignee value for the current Complaint and previous Complaint (if any). If different, raise AcmAssigneeChangeEvent
	 * 
	 * @param acmObjectHistory
	 */
	private void checkAndRaiseAssigneeChangeEventForComplaint(AcmObjectHistory acmObjectHistory)
	{
		// Create JSON unmarshaller
		AcmUnmarshaller unmarshaller = ObjectConverter.createJSONUnmarshaller();
		
		// Get previous Object History
		AcmObjectHistory previousObjectHistory = getPreviousObjectHistory(acmObjectHistory.getObjectType());
		
		// Init properties and unarshall current complaint
		Complaint currentComplaint = (Complaint) unmarshaller.unmarshall(acmObjectHistory.getObjectString(), Complaint.class);
		Complaint previousComplaint = null;
		
		Long id = null;
		String number = null;
		String name = null;
		String newAssignee = null;
		String oldAssignee = null;
		
		// Unmarshall previous complaint (if there is some history in database)
		if (previousObjectHistory != null)
		{
			previousComplaint = (Complaint) unmarshaller.unmarshall(previousObjectHistory.getObjectString(), Complaint.class);	
		}
		
		// If current complaint is not null (check just to be sure), get needed information
		if (currentComplaint != null)
		{
			id = currentComplaint.getComplaintId();
			number = currentComplaint.getComplaintNumber();
			name = currentComplaint.getComplaintTitle();
			
			newAssignee = getAssigneeIdFromParticipants(currentComplaint.getParticipants());
		}
		
		// if previous complaint is not null, get needed information
		if (previousComplaint != null)
		{
			oldAssignee = getAssigneeIdFromParticipants(previousComplaint.getParticipants());
		}
		
		// Raise Event (if assignee is changed)
		raiseEvent(id, COMPLAINT_OBJECT_TYPE, number, name, newAssignee, oldAssignee);
	}
	
	/**
	 * Check assignee value for the current CaseFile and previous CaseFile (if any). If different, raise AcmAssigneeChangeEvent
	 * 
	 * @param acmObjectHistory
	 */
	private void checkAndRaiseAssigneeChangeEventForCaseFile(AcmObjectHistory acmObjectHistory)
	{
		// Create JSON unmarshaller
		AcmUnmarshaller unmarshaller = ObjectConverter.createJSONUnmarshaller();
		
		// Get previous Object History
		AcmObjectHistory previousObjectHistory = getPreviousObjectHistory(acmObjectHistory.getObjectType());
		
		// Init properties and unarshall current case file
		CaseFile currentCaseFile = (CaseFile) unmarshaller.unmarshall(acmObjectHistory.getObjectString(), CaseFile.class);
		CaseFile previousCaseFile = null;
		
		Long id = null;
		String number = null;
		String name = null;
		String newAssignee = null;
		String oldAssignee = null;
		
		// Unmarshall previous case file (if there is some history in database)
		if (previousObjectHistory != null)
		{
			previousCaseFile = (CaseFile) unmarshaller.unmarshall(previousObjectHistory.getObjectString(), CaseFile.class);	
		}
		
		// If current case file is not null (check just to be sure), get needed information
		if (currentCaseFile != null)
		{
			id = currentCaseFile.getId();
			number = currentCaseFile.getCaseNumber();
			name = currentCaseFile.getTitle();
			
			newAssignee = getAssigneeIdFromParticipants(currentCaseFile.getParticipants());
		}
		
		// if previous case file is not null, get needed information
		if (previousCaseFile != null)
		{
			oldAssignee = getAssigneeIdFromParticipants(previousCaseFile.getParticipants());
		}
		
		// Raise Event (if assignee is changed)
		raiseEvent(id, CASE_FILE_OBJECT_TYPE, number, name, newAssignee, oldAssignee);
	}
	
	/**
	 * Check assignee value for the current Task and previous Task (if any). If different, raise AcmAssigneeChangeEvent
	 * 
	 * @param acmObjectHistory
	 */
	private void checkAndRaiseAssigneeChangeEventForTask(AcmObjectHistory acmObjectHistory)
	{
		// Create JSON unmarshaller
		AcmUnmarshaller unmarshaller = ObjectConverter.createJSONUnmarshaller();
		
		// Get previous Object History
		AcmObjectHistory previousObjectHistory = getPreviousObjectHistory(acmObjectHistory.getObjectType());
		
		// Init properties and unarshall current task
		AcmTask currentTask = (AcmTask) unmarshaller.unmarshall(acmObjectHistory.getObjectString(), AcmTask.class);
		AcmTask previousTask = null;
		
		Long id = null;
		String number = null;
		String name = null;
		String newAssignee = null;
		String oldAssignee = null;
		
		// Unmarshall previous task (if there is some history in database)
		if (previousObjectHistory != null)
		{
			previousTask = (AcmTask) unmarshaller.unmarshall(previousObjectHistory.getObjectString(), AcmTask.class);	
		}
		
		// If current task is not null (check just to be sure), get needed information
		if (currentTask != null)
		{
			id = currentTask.getTaskId();
			number = currentTask.getBusinessProcessName();
			name = currentTask.getTitle();
			
			newAssignee = currentTask.getAssignee();
		}
		
		// if previous task is not null, get needed information
		if (previousTask != null)
		{
			oldAssignee = previousTask.getAssignee();
		}
		
		// Raise Event (if assignee is changed)
		raiseEvent(id, TASK_OBJECT_TYPE, number, name, newAssignee, oldAssignee);
	}
	
	private void raiseEvent(Long id, String type, String number, String name, String newAssignee, String oldAssignee)
	{
		// Check if the event should be raised
		boolean raiseEvent = false;
				
		if (newAssignee != null && oldAssignee == null)
		{
			raiseEvent = true;
		}
		
		if (newAssignee != null && oldAssignee != null)
		{			
			if (!newAssignee.equals(oldAssignee))
			{
				raiseEvent = true;
			}
		}
		
		// Raise event if the flat is set to true
		if (raiseEvent) 
		{
			AssigneeChangeInfo assigneeChangeInfo = new AssigneeChangeInfo(id, type, number, name, newAssignee, oldAssignee);
			
			getEventPublisher().publishAssigneeChangeEvent(assigneeChangeInfo);
		}
	}
	
	private AcmObjectHistory getPreviousObjectHistory(String objectType)
	{
		AcmObjectHistory retval = null;
		
		try
		{
			retval = getAcmObjectHistoryDao().findLastInsertedByObjectType(objectType);
		}
		catch(Exception e)
		{
			LOG.warn("There is no any result (new record). Proceed with execution and raise the event.");
		}
		
		return retval;
	}
	
	private String getAssigneeIdFromParticipants(List<AcmParticipant> participants)
	{
		if (participants != null)
		{
			for (AcmParticipant participant : participants)
			{
				if (ASSIGNEE.equals(participant.getParticipantType()))
				{
					return participant.getParticipantLdapId();
				}
			}
		}
		
		return null;
	}

	public AcmObjectHistoryDao getAcmObjectHistoryDao() {
		return acmObjectHistoryDao;
	}

	public void setAcmObjectHistoryDao(AcmObjectHistoryDao acmObjectHistoryDao) {
		this.acmObjectHistoryDao = acmObjectHistoryDao;
	}

	public AcmObjectHistoryEventPublisher getEventPublisher() {
		return eventPublisher;
	}

	public void setEventPublisher(AcmObjectHistoryEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

}
