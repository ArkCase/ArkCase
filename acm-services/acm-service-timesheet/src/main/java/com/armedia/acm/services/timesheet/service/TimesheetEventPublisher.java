/**
 * 
 */
package com.armedia.acm.services.timesheet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.AcmTimesheetEvent;

/**
 * @author riste.tutureski
 *
 */
public class TimesheetEventPublisher {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private ApplicationEventPublisher applicationEventPublisher;

	public void publishEvent(AcmTimesheet source, String userId, String ipAddress, boolean succeeded, String type, FrevvoUploadedFiles frevvoUploadedFiles, boolean startWorkflow)
	{
		LOG.debug("Publishing AcmTimesheet event.");
		
		AcmTimesheetEvent event = new AcmTimesheetEvent(source, userId, ipAddress, succeeded, type, frevvoUploadedFiles, startWorkflow);
		
		getApplicationEventPublisher().publishEvent(event);
	}
	
	public ApplicationEventPublisher getApplicationEventPublisher() {
		return applicationEventPublisher;
	}

	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
}
