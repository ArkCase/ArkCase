/**
 * 
 */
package com.armedia.acm.service.objecthistory.model;

import java.util.Date;

import com.armedia.acm.event.AcmEvent;

/**
 * @author riste.tutureski
 *
 */
public class AcmObjectHistoryEvent extends AcmEvent {

	private static final String OBJECT_TYPE = "OBJECT_HISTORY";
	
	private static final long serialVersionUID = 5070129016123205179L;

	public AcmObjectHistoryEvent(AcmObjectHistory source) 
	{
		super(source);
		
		setObjectId(source.getId());
		setEventDate(new Date());
		setUserId(source.getModifier());
	}

	public String getObjectType()
	{
		return OBJECT_TYPE;
	}
}
