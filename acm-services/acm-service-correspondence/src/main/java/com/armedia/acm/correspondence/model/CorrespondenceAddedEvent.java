package com.armedia.acm.correspondence.model;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.springframework.security.core.Authentication;

import java.util.Date;

public class CorrespondenceAddedEvent extends AcmEvent
{

    public static final String EVENT_TYPE = "com.armedia.acm.correspondence.file.added";

    public CorrespondenceAddedEvent(EcmFile source, Authentication authentication, boolean succeeded)
    {
        super(source);

        setEventType(EVENT_TYPE);
        setObjectType("FILE");
        setObjectId(source.getFileId());
        setEventDate(new Date());
        setUserId(source.getModifier());
        setSucceeded(succeeded);
        setParentObjectId(source.getContainer().getContainerObjectId());
        setParentObjectType(source.getContainer().getContainerObjectType());

        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }

    }
}
