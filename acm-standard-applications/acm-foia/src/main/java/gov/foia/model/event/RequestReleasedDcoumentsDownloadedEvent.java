package gov.foia.model.event;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.model.AcmEvent;
import gov.foia.model.FOIARequest;

import java.util.Date;

public class RequestReleasedDcoumentsDownloadedEvent extends AcmEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.casefile.released.documents.downloaded";

    public RequestReleasedDcoumentsDownloadedEvent(FOIARequest source)
    {
        super(source);

        setObjectId(source.getId());
        setObjectType(source.getObjectType());
        setEventDate(new Date());
        setEventType(EVENT_TYPE);
        setSucceeded(true);
        setUserId(AuthenticationUtils.getUsername());
        setIpAddress(AuthenticationUtils.getUserIpAddress());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
