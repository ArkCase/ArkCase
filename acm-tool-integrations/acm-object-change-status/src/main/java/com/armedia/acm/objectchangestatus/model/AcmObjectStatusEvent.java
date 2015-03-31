/**
 * 
 */
package com.armedia.acm.objectchangestatus.model;

import java.util.Date;

import com.armedia.acm.event.AcmEvent;

/**
 * @author riste.tutureski
 *
 */
public class AcmObjectStatusEvent extends AcmEvent{

	private static final long serialVersionUID = 1631198771405305265L;
	
	public AcmObjectStatusEvent(AcmObjectStatus source, String userId, boolean succeeded) 
	{
		super(source);
		
		setObjectId(source.getObjectId());
		setObjectType(source.getObjectType());
		setUserId(userId);
		setSucceeded(succeeded);
		setEventDate(new Date());
		setEventType(AcmObjectStatusConstants.EVENT_TYPE_CHANGE);
	}

}
