/**
 * 
 */
package com.armedia.acm.plugins.task.service;

import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmAssigneeChangeChacker;

/**
 * @author riste.tutureski
 *
 */
public class TaskAssigneeChangeChecker extends AcmAssigneeChangeChacker implements ApplicationListener<AcmObjectHistoryEvent> {

	private static final String TASK_OBJECT_TYPE = "TASK";
	
	@Override
	public void onApplicationEvent(AcmObjectHistoryEvent event) 
	{
		if (event != null)
		{		
			AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();
			
			if (acmObjectHistory != null &&  TASK_OBJECT_TYPE.equals(acmObjectHistory.getObjectType()))
			{			
				// Create JSON unmarshaller
				AcmUnmarshaller unmarshaller = ObjectConverter.createJSONUnmarshaller();
				
				// Init properties and unarshall current task
				AcmTask currentTask = (AcmTask) unmarshaller.unmarshall(acmObjectHistory.getObjectString(), AcmTask.class);
				AcmTask previousTask = null;
				AcmObjectHistory previousObjectHistory = null;
				
				AcmAssignment assignment = new AcmAssignment();
				
				// If current task is not null (check just to be sure), get needed information
				if (currentTask != null)
				{
					previousObjectHistory = getPreviousObjectHistory(currentTask.getTaskId(), acmObjectHistory.getObjectType());
					
					assignment.setObjectId(currentTask.getTaskId());
					assignment.setObjectNumber(currentTask.getBusinessProcessName());
					assignment.setObjectName(currentTask.getTitle());
					
					assignment.setNewAssignee(currentTask.getAssignee());
				}
				
				// Unmarshall previous task (if there is some history in database)
				if (previousObjectHistory != null)
				{
					previousTask = (AcmTask) unmarshaller.unmarshall(previousObjectHistory.getObjectString(), AcmTask.class);	
				}
				
				// if previous task is not null, get needed information
				if (previousTask != null)
				{
					assignment.setOldAssignee(previousTask.getAssignee());
				}
				
				assignment.setObjectType(TASK_OBJECT_TYPE);
				
				// Raise Event (if assignee is changed)
				raiseEvent(assignment, event.getUserId(), event.getIpAddress());
			}
		}
	}
	
}
