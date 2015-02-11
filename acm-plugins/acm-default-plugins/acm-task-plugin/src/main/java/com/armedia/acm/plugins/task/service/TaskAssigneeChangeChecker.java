/**
 * 
 */
package com.armedia.acm.plugins.task.service;

import org.springframework.context.ApplicationListener;

import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmAssigneeChangeChacker;

/**
 * @author riste.tutureski
 *
 */
public class TaskAssigneeChangeChecker extends AcmAssigneeChangeChacker implements ApplicationListener<AcmObjectHistoryEvent> {
	
	@Override
	public void onApplicationEvent(AcmObjectHistoryEvent event) 
	{
		super.onApplicationEvent(event);
	}

	@Override
	public Class<?> getTargetClass() 
	{
		return AcmTask.class;
	}

	@Override
	public String getObjectType(Object in) 
	{
		AcmTask task = (AcmTask) in;
		
		if (task != null)
		{
			return task.getObjectType();
		}
		
		return null;
	}

	@Override
	public Long getObjectId(Object in) 
	{
		AcmTask task = (AcmTask) in;
		
		if (task != null)
		{
			return task.getTaskId();
		}
		
		return null;
	}

	@Override
	public String getObjectTitle(Object in) 
	{
		AcmTask task = (AcmTask) in;
		
		if (task != null)
		{
			return task.getTitle();
		}
		
		return null;
	}

	@Override
	public String getObjectName(Object in) 
	{
		AcmTask task = (AcmTask) in;
		
		if (task != null)
		{
			return task.getBusinessProcessName();
		}
		
		return null;
	}

	@Override
	public String getAssignee(Object in) 
	{
		AcmTask task = (AcmTask) in;
		
		if (task != null)
		{
			return task.getAssignee();
		}
		
		return null;
	}
	
}
