/**
 * 
 */
package com.armedia.acm.plugins.task.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.DateFormats;
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
			SimpleDateFormat formatter = new SimpleDateFormat(DateFormats.TASK_NAME_DATE_FORMAT);
			
			List<String> nameArray = new ArrayList<>();
			
			if (task.getDueDate() != null)
			{
				nameArray.add(formatter.format(task.getDueDate()));
			}
			
			if (task.getPriority() != null)
			{
				nameArray.add(task.getPriority());
			}
			
			if (task.getTitle() != null)
			{
				nameArray.add(task.getTitle());
			}
			
			return StringUtils.join(nameArray, ",");
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
