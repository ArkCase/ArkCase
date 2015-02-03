/**
 * 
 */
package com.armedia.acm.service.objecthistory.service;

import com.armedia.acm.service.objecthistory.dao.AcmObjectHistoryDao;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;

/**
 * @author riste.tutureski
 *
 */
public class AcmAssigneeChangeChacker{
	
	private AcmObjectHistoryDao acmObjectHistoryDao;
	private AcmObjectHistoryEventPublisher eventPublisher;
	
	public void raiseEvent(AcmAssignment assignment, String userId, String ipAddress)
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
		
		// Raise event if the flat is set to true
		if (raiseEvent) 
		{			
			getEventPublisher().publishAssigneeChangeEvent(assignment, userId, ipAddress);
		}
	}
	
	public AcmObjectHistory getPreviousObjectHistory(Long objectId, String objectType)
	{
		AcmObjectHistory retval = getAcmObjectHistoryDao().safeFindLastInsertedByObjectIdAndObjectType(objectId, objectType);
		
		return retval;
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
