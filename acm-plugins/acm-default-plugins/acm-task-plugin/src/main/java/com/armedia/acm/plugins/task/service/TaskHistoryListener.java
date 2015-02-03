/**
 * 
 */
package com.armedia.acm.plugins.task.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

/**
 * @author riste.tutureski
 *
 */
public class TaskHistoryListener implements ApplicationListener<AcmApplicationTaskEvent> {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private static final String OBJECT_TYPE = "TASK";
	
	private AcmObjectHistoryService acmObjectHistoryService;
	
	@Override
	public void onApplicationEvent(AcmApplicationTaskEvent event) {
		LOG.debug("Task event raised. Start adding it to the object history ...");
		
		if (event != null)
		{
			boolean execute = checkExecution(event.getEventType());
			
			if (execute)
			{
				AcmTask task = (AcmTask) event.getSource();
				
				getAcmObjectHistoryService().save(event.getUserId(), event.getEventType(), task, task.getId(), OBJECT_TYPE, event.getEventDate(), event.getIpAddress()); 	
				
				LOG.debug("Task History added to database.");
			}
		}
	}
	
	private boolean checkExecution(String eventType)
	{		
		if ("com.armedia.acm.app.task.create".equals(eventType) ||
			"com.armedia.acm.app.task.save".equals(eventType) ||
			"com.armedia.acm.app.task.delete".equals(eventType) ||
			"com.armedia.acm.app.task.complete".equals(eventType))
		{
			return true;
		}
		
		return false;
	}

	public AcmObjectHistoryService getAcmObjectHistoryService() 
	{
		return acmObjectHistoryService;
	}

	public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService) 
	{
		this.acmObjectHistoryService = acmObjectHistoryService;
	}

}
