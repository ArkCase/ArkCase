package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.event.AcmEvent;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * @author riste.tutureski
 */
public class EcmFileUpdatedEvent extends AcmEvent
{
	private static final long serialVersionUID = 1L;

	public EcmFileUpdatedEvent(EcmFile updated, Authentication authentication)
    {
    	super(updated);
    	setEventType("com.armedia.acm.ecm.file.updated");
        setObjectType("FILE");
        setObjectId(updated.getFileId());
        setEventDate(new Date());
    }
}
