/**
 * 
 */
package com.armedia.acm.service.objecthistory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintPersistenceEvent;

/**
 * @author riste.tutureski
 *
 */
public class AcmComplaintHistoryListener implements ApplicationListener<ComplaintPersistenceEvent>{

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private static final String OBJECT_TYPE = "COMPLAINT";
	
	private AcmObjectHistoryService acmObjectHistoryService;
	
	@Override
	public void onApplicationEvent(ComplaintPersistenceEvent event) 
	{
		LOG.debug("Complaint event raised. Start adding it to the object history ...");
		
		boolean execute = checkExecution(event.getEventType());
		
		if (event != null && execute)
		{
			Complaint complaint = (Complaint) event.getSource();
			
			getAcmObjectHistoryService().save(event.getUserId(), event.getEventType(), complaint, complaint.getComplaintId(), OBJECT_TYPE, event.getEventDate(), event.getIpAddress()); 	
			
			LOG.debug("Complain History added to database.");
		}
	}
	
	private boolean checkExecution(String eventType)
	{		
		if ("com.armedia.acm.complaint.created".equals(eventType) ||
			"com.armedia.acm.complaint.updated".equals(eventType))
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
