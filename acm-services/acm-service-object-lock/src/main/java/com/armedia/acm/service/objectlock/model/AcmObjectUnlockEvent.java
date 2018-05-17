package com.armedia.acm.service.objectlock.model;

/**
 * Created by riste.tutureski on 4/4/2016.
 */
public class AcmObjectUnlockEvent extends AcmObjectLockUnlockEvent
{
    private static final String UNLOCK_EVENT_TYPE = "com.armedia.acm.objectlock.unlock";

    public AcmObjectUnlockEvent(AcmObjectLock source, String userId, Boolean success)
    {
        super(source, userId, success);
    }

    @Override
    public String getEventType()
    {
        return UNLOCK_EVENT_TYPE + "." + getObjectType().toLowerCase();
    }
}
