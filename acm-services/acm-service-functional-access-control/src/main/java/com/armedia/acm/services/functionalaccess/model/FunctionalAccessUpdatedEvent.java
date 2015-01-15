package com.armedia.acm.services.functionalaccess.model;

import java.util.Date;

import org.springframework.security.core.Authentication;

import com.armedia.acm.event.AcmEvent;

/**
 * @author riste.tutureski
 *
 */
public class FunctionalAccessUpdatedEvent extends AcmEvent {

	private static final long serialVersionUID = 2731024788487648140L;
	
	private static final String EVENT_TYPE = "com.armedia.acm.functionalaccess.updated";

	public FunctionalAccessUpdatedEvent(Object source, Authentication auth) {
		super(source);
		setEventDate(new Date());
		setUserId(auth.getName());
	}

	@Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
