package com.armedia.acm.service.objectlock.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;

/**
 * Interface for object locking providers.
 * 
 * Created by bojan.milenkoski on 03/05/20186.
 */
public interface ObjectLockingProvider
{
    /**
     * Returns the default lock expiry time in milliseconds.
     * 
     * @return the default lock expiry time in milliseconds
     */
    Long getExpiryTimeInMilliseconds();

    /**
     * Checks if a lock type can be acquired for a given {@link AcmObject}, specified by the objectId and object type.
     * 
     * @param objectId
     *            the Id of the object to check if lock can be acquired
     * @param objectType
     *            the type of the object to check if lock can be acquired
     * @param lockType
     *            the required lock type
     * @param checkChildObjects
     *            for complex objects if this flag is set to true, the required lock is checked for the child objects
     * @param userId
     *            the ID of the user that requires the lock
     * @throws AcmObjectLockException
     *             when the required lock cannot be acquired for the specified object
     */
    void checkIfObjectLockCanBeAcquired(Long objectId, String objectType, String lockType, boolean checkChildObjects, String userId)
            throws AcmObjectLockException;

    /**
     * Acquires a lock type for a given {@link AcmObject}, specified by the objectId and object type.
     * 
     * @param objectId
     *            the Id of the object to acquire the lock for
     * @param objectType
     *            the type of the object to acquire the lock for
     * @param lockType
     *            the required lock type
     * @param expiry
     *            the lock expiry time in milliseconds. If it's set to null or 0, then the default expire time set in
     *            the {@link ObjectLockingProvider} for the given object type is used
     * @param lockChildObjects
     *            for complex objects if this flag is set to true, the required lock is acquired for the child objects
     * @param userId
     *            the ID of the user that requires the lock
     * @return the acquired lock
     * @throws AcmObjectLockException
     *             when the required lock cannot be acquired for the specified object
     */
    AcmObjectLock acquireObjectLock(Long objectId, String objectType, String lockType, Long expiry, boolean lockChildObjects, String userId)
            throws AcmObjectLockException;

    /**
     * Releases a lock for a given {@link AcmObject}, specified by the objectId and object type.
     * 
     * @param objectId
     *            the Id of the object to release the lock for
     * @param objectType
     *            the type of the object to release the lock for
     * @param lockType
     *            the required lock type
     * @param unlockChildObjects
     *            for complex objects if this flag is set to true, the required lock is released on the child objects
     * @param userId
     *            the ID of the user that requires the lock to be released
     * @param lockId
     *            the ID of the lock to be released. Most of the implementations don't require this argument.
     *            SHARED_WRITE implementation requires it.
     * @throws AcmObjectLockException
     *             when the required lock cannot be released for the specified object
     */
    void releaseObjectLock(Long objectId, String objectType, String lockType, boolean unlockChildObjects, String userId, Long lockId)
            throws AcmObjectLockException;
}
