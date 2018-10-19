package gov.foia.model.event;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class EcmFilePublicFlagUpdatedEvent extends AcmEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.ecm.file.publicFlag.updated";

    public EcmFilePublicFlagUpdatedEvent(Object source, String userId, String ipAddress)
    {
        super(source);
        setUserId(userId);
        setIpAddress(ipAddress);
        setEventDate(new Date());
        setEventType(EVENT_TYPE);
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
