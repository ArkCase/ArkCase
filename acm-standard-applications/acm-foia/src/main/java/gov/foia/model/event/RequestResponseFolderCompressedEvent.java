package gov.foia.model.event;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.model.AcmEvent;
import gov.foia.model.FOIARequest;

import java.util.Date;

public class RequestResponseFolderCompressedEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.casefile.response.folder.compressed";

    public RequestResponseFolderCompressedEvent(Object source)
    {
        super(source);

        if(source instanceof FOIARequest)
        {
            FOIARequest foiaRequest = (FOIARequest)source;

            setObjectId(foiaRequest.getId());
            setObjectType(foiaRequest.getObjectType());
            setUserId(AuthenticationUtils.getUsername());
            setIpAddress(AuthenticationUtils.getUserIpAddress());
            setEventType(EVENT_TYPE);
            setEventDate(new Date());
            setSucceeded(true);
        }
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
