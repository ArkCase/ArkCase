package com.armedia.acm.service.objectlock.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by riste.tutureski on 4/4/2016.
 */
public class AcmObjectUnlockEvent extends AcmObjectLockUnlockEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.objectlock.unlock";

    public AcmObjectUnlockEvent(AcmObjectLock source, String userId, Boolean success)
    {
        super(source, userId, success);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
