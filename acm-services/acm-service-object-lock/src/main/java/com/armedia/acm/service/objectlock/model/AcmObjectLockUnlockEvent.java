package com.armedia.acm.service.objectlock.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by riste.tutureski on 4/4/2016.
 */
public class AcmObjectLockUnlockEvent extends AcmEvent
{
    public AcmObjectLockUnlockEvent(AcmObjectLock source, String userId, Boolean success)
    {
        super(source);
        setObjectId(source.getId());
        setObjectType(source.getLockType());
        setParentObjectId(source.getObjectId());
        setParentObjectType(source.getObjectType());
        setEventDate(new Date());
        setUserId(userId);
        setSucceeded(success);
    }
}
