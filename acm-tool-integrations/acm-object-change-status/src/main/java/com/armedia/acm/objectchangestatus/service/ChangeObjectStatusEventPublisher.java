/**
 * 
 */
package com.armedia.acm.objectchangestatus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.armedia.acm.objectchangestatus.model.AcmObjectStatus;
import com.armedia.acm.objectchangestatus.model.AcmObjectStatusEvent;

/**
 * @author riste.tutureski
 *
 */
public class ChangeObjectStatusEventPublisher implements ApplicationEventPublisherAware {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private ApplicationEventPublisher applicationEventPublisher;

	public void publishEvent(AcmObjectStatus source, String userId, boolean succeeded)
	{
		LOG.debug("Publishing AcmObjectStatus event.");
		
		AcmObjectStatusEvent event = new AcmObjectStatusEvent(source, userId, succeeded);
		
		getApplicationEventPublisher().publishEvent(event);
	}
			
	
	public ApplicationEventPublisher getApplicationEventPublisher() {
		return applicationEventPublisher;
	}

	@Override
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}	
}
