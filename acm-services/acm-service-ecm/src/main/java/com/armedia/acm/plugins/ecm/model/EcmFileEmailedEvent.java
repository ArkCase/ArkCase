package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.core.model.AcmEvent;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by joseph.mcgrady on 3/18/2016.
 */
public class EcmFileEmailedEvent extends AcmEvent
{
    private static final String OBJECT_TYPE = "FILE";
    private static final String EVENT_TYPE = "com.armedia.acm.ecm.file.emailed";

    public EcmFileEmailedEvent(EcmFile uploaded, Authentication authentication)
    {
        super(uploaded);

        setEventDate(new Date());
        setEventType(EVENT_TYPE);
        setObjectType(OBJECT_TYPE);
        setObjectId(uploaded.getFileId());

        // Obtains the user id and ip address required for audit events
        setUserId(authentication.getName());
        if ( authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
    }
}