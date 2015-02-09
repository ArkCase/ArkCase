/**
 * 
 */
package com.armedia.acm.service.objecthistory.service;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.service.objecthistory.dao.AcmObjectHistoryDao;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;

/**
 * @author riste.tutureski
 *
 */
public abstract class AcmAssigneeChangeChacker{
	
	private AcmObjectHistoryDao acmObjectHistoryDao;
	private AcmObjectHistoryEventPublisher eventPublisher;
	
	public void onApplicationEvent(AcmObjectHistoryEvent event) 
	{
		if (event != null)
		{		
			AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();
			
			if (acmObjectHistory != null)
			{
				// Create JSON unmarshaller
				AcmUnmarshaller unmarshaller = ObjectConverter.createJSONUnmarshaller();
				
				// Init properties and unarshall current object
				Object currentObject = unmarshaller.unmarshall(acmObjectHistory.getObjectString(), getTargetClass());
				
				// Get object type
				String objectType = getObjectType(currentObject);
				
				if (objectType != null && objectType.equals(acmObjectHistory.getObjectType()))
				{
					Object previousObject = null;
					AcmObjectHistory previousObjectHistory = null;
					
					AcmAssignment assignment = new AcmAssignment();
					
					// If current object is not null (check just to be sure), get needed information
					if (currentObject != null)
					{
						previousObjectHistory = getPreviousObjectHistory(getObjectId(currentObject), acmObjectHistory.getObjectType());
						
						assignment.setObjectId(getObjectId(currentObject));
						assignment.setObjectTitle(getObjectTitle(currentObject));
						assignment.setObjectName(getObjectName(currentObject));
						
						assignment.setNewAssignee(getAssignee(currentObject));
					}
					
					// Unmarshall previous object (if there is some history in database)
					if (previousObjectHistory != null)
					{
						previousObject = unmarshaller.unmarshall(previousObjectHistory.getObjectString(), getTargetClass());	
					}
					
					// if previous object is not null, get needed information
					if (previousObject != null)
					{
						assignment.setOldAssignee(getAssignee(previousObject));
					}
					
					assignment.setObjectType(objectType);
					
					// Raise Event (if assignee is changed)
					raiseEvent(assignment, event.getUserId(), event.getIpAddress());
				}
			}
		}
	}
	
	private void raiseEvent(AcmAssignment assignment, String userId, String ipAddress)
	{
		// Check if the event should be raised
		boolean raiseEvent = false;
				
		if (assignment != null)
		{
			if (assignment.getNewAssignee() != null && assignment.getOldAssignee() == null)
			{
				raiseEvent = true;
			}
			
			if (assignment.getNewAssignee() != null && assignment.getOldAssignee() != null)
			{			
				if (!assignment.getNewAssignee().equals(assignment.getOldAssignee()))
				{
					raiseEvent = true;
				}
			}
		}
		
		// Raise event if the flag is set to true
		if (raiseEvent) 
		{			
			getEventPublisher().publishAssigneeChangeEvent(assignment, userId, ipAddress);
		}
	}
	
	private AcmObjectHistory getPreviousObjectHistory(Long objectId, String objectType)
	{
		AcmObjectHistory retval = getAcmObjectHistoryDao().safeFindLastInsertedByObjectIdAndObjectType(objectId, objectType);
		
		return retval;
	}
	
	public abstract Class<?> getTargetClass();
	public abstract String getObjectType(Object in);
	public abstract Long getObjectId(Object in);
	public abstract String getObjectTitle(Object in);
	public abstract String getObjectName(Object in);
	public abstract String getAssignee(Object in);

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
