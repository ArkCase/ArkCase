package com.armedia.acm.service.objectlock.model;

import com.armedia.acm.core.model.AcmEvent;

/**
 * Created by riste.tutureski on 4/4/2016.
 */
public class AcmObjectLockEvent extends AcmObjectLockUnlockEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.objectlock.lock";

    public AcmObjectLockEvent(AcmObjectLock source, String userId, Boolean success)
    {
        super(source, userId, success);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
