package com.armedia.acm.service.objectlock.model;

/**
 * Created by riste.tutureski on 4/4/2016.
 */
public class AcmObjectUnlockEvent extends AcmObjectLockUnlockEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.objectlock.unlock";
    private static final String CHECKIN_TYPE = "com.armedia.acm.objectlock.checkin";
    private static final String CANCEL_TYPE = "com.armedia.acm.objectlock.cancel";

    public AcmObjectUnlockEvent(AcmObjectLock source, String userId, Boolean success)
    {
        super(source, userId, success);
    }

    @Override
    public String getEventType()
    {
        String eventType;
        switch (getObjectType())
        {
        case AcmObjectLockConstants.CHECKIN_LOCK:
            eventType = CHECKIN_TYPE;
            break;
        case AcmObjectLockConstants.CANCEL_LOCK:
            eventType = CANCEL_TYPE;
            break;
        default:
            eventType = EVENT_TYPE;
        }

        return eventType;
    }
}
