/**
 * 
 */
package com.armedia.acm.plugins.complaint.model;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class ComplaintClosedEvent extends ComplaintPersistenceEvent {

	private static final long serialVersionUID = 1L;
	
	private static final String EVENT_TYPE = "com.armedia.acm.complaint.closed";
	
	public ComplaintClosedEvent(Complaint source, boolean succeeded, String user, Date closeDate)
	{
		super(source);
		setObjectId(source.getComplaintId());
		setObjectType("COMPLAINT");
		setSucceeded(succeeded);
		setComplaintNumber(source.getComplaintNumber());
		setEventDate(closeDate);
		setUserId(user);
	}
	
	@Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
	
}
