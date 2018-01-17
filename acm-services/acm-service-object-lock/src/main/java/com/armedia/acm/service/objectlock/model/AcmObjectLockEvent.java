package com.armedia.acm.service.objectlock.model;

/**
 * Created by riste.tutureski on 4/4/2016.
 */
public class AcmObjectLockEvent extends AcmObjectLockUnlockEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.objectlock.lock";
    private static final String CHECKOUT_TYPE = "com.armedia.acm.objectlock.checkout";

    public AcmObjectLockEvent(AcmObjectLock source, String userId, Boolean success)
    {
        super(source, userId, success);

    }

    @Override
    public String getEventType()
    {
        String eventType;
        switch (getObjectType())
        {
        case AcmObjectLockConstants.WORD_EDIT_LOCK:
        case AcmObjectLockConstants.CHECKOUT_LOCK:
            eventType = CHECKOUT_TYPE;
            break;
        default:
            eventType = EVENT_TYPE;
        }
        return eventType;
    }
}
