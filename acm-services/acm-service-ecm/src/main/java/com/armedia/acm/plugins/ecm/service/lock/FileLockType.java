package com.armedia.acm.plugins.ecm.service.lock;

import com.armedia.acm.core.exceptions.AcmObjectLockException;

/**
 * If new locking types are added in this enumeration, then the {@link FileLockingProvider} must be updated to
 * handle these types.
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
public enum FileLockType
{
    READ, WRITE, DELETE, SHARED_WRITE;

    public static FileLockType fromName(String lockType) throws AcmObjectLockException
    {
        FileLockType objectLockType = null;
        try
        {
            objectLockType = FileLockType.valueOf(lockType);
        }
        catch (Exception e)
        {
            throw new AcmObjectLockException("Unknown lock type: " + lockType);
        }

        return objectLockType;
    }
}
