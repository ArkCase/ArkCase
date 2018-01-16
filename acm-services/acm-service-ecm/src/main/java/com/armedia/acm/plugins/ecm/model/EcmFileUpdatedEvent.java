package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.core.model.AcmEvent;

import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * @author riste.tutureski
 */
public class EcmFileUpdatedEvent extends AcmEvent
{
    private static final long serialVersionUID = 1L;

    public EcmFileUpdatedEvent(EcmFile updated, Authentication auth)
    {
        super(updated);
        setEventType("com.armedia.acm.ecm.file.updated");
        setObjectType(EcmFileConstants.OBJECT_FILE_TYPE);
        setObjectId(updated.getFileId());
        setEventDate(new Date());
        if (auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails)
        {
            setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        setUserId(auth.getName());
        setParentObjectType(updated.getContainer().getContainerObjectType());
        setParentObjectId(updated.getContainer().getContainerObjectId());
    }
}
