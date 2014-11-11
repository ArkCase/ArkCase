/**
 * 
 */
package com.armedia.acm.plugins.complaint.model;

/**
 * @author riste.tutureski
 *
 */
public class ComplaintClosedEvent extends ComplaintPersistenceEvent {

	private static final long serialVersionUID = 1L;
	
	private static final String EVENT_TYPE = "com.armedia.acm.complaint.closed";
	
	public ComplaintClosedEvent(Complaint source)
	{
		super(source);
	}
	
	@Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
	
}
