package com.armedia.acm.services.signature.model;

import com.armedia.acm.event.AcmEvent;

public class ApplicationSignatureEvent extends AcmEvent
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -320828483774515322L;

	public ApplicationSignatureEvent(Signature source, String signatureEvent, boolean succeeded, String ipAddress)
    {
        super(source);
        
        setObjectId(source.getObjectId());
        setObjectType(source.getObjectType());
        setEventDate(source.getSignedDate());
        setUserId(source.getSignedBy());
        setEventType(signatureEvent);
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
    }
}
