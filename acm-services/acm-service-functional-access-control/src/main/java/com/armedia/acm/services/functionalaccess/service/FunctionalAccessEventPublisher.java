package com.armedia.acm.services.functionalaccess.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

import com.armedia.acm.services.functionalaccess.model.FunctionalAccessUpdatedEvent;

/**
 * @author riste.tutureski
 *
 */
public class FunctionalAccessEventPublisher implements ApplicationEventPublisherAware {

	private ApplicationEventPublisher eventPublisher;
	
	public void publishFunctionalAccessUpdateEvent(Object source, Authentication auth)
	{
		FunctionalAccessUpdatedEvent event = new FunctionalAccessUpdatedEvent(source, auth);
		getEventPublisher().publishEvent(event);
	}
	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		eventPublisher = applicationEventPublisher;
	}

	public ApplicationEventPublisher getEventPublisher() {
		return eventPublisher;
	}
	
	

}
