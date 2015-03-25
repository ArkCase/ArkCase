/**
 * 
 */
package com.armedia.acm.services.costsheet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.AcmCostsheetEvent;

/**
 * @author riste.tutureski
 *
 */
public class CostsheetEventPublisher {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private ApplicationEventPublisher applicationEventPublisher;

	public void publishEvent(AcmCostsheet source, String userId, String ipAddress, boolean succeeded, String type, FrevvoUploadedFiles frevvoUploadedFiles, boolean startWorkflow)
	{
		LOG.debug("Publishing AcmTimesheet event.");
		
		AcmCostsheetEvent event = new AcmCostsheetEvent(source, userId, ipAddress, succeeded, type, frevvoUploadedFiles, startWorkflow);
		
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
