/**
 * 
 */
package com.armedia.acm.service.objecthistory.model;

import java.util.HashMap;
import java.util.Map;

import com.armedia.acm.event.AcmEvent;

/**
 * @author riste.tutureski
 *
 */
public class AcmAssigneeChangeEvent extends AcmEvent{

	private static final long serialVersionUID = -969356637766220472L;
	
	private static final String EVENT_TYPE = "com.armedia.acm.object.assagnee.change";
	
	public AcmAssigneeChangeEvent(AssigneeChangeInfo source) {
		super(source);
		
		setObjectId(source.getObjectId());
		setObjectType(source.getObjectType());
		
		Map<String, Object> eventProperties = new HashMap<String, Object>();
		
		eventProperties.put("objectNumber", source.getObjectNumber());	
		eventProperties.put("objectName", source.getObjectName());
		eventProperties.put("newAssignee", source.getNewAssignee());
		eventProperties.put("oldAssignee", source.getOldAssignee());
		
		setEventProperties(eventProperties);
	}
	
	@Override
	public String getEventType()
	{
		return EVENT_TYPE;
	}

}
