package com.armedia.acm.service.objectlock.service;

import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link ObjectLockingProvider}. It only checks if the same lock type on the same object is
 * acquired by another user and throws an exception if that is the case.
 * 
 * Created by bojan.milenkoski on 15/05/2018.
 */
public class DefaultObjectLockingProvider implements ObjectLockingProvider
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, Object> locks = new ConcurrentHashMap<>();

    private AcmObjectLockService objectLockService;
    private Long expiryTime;

    @Override
    public void checkIfObjectLockCanBeAcquired(Long objectId, String objectType, String lockType, boolean checkChildObjects, String userId)
            throws AcmObjectLockException
    {
        log.trace("Checking if object lock[objectId={}, objectType={}, lockType={}] can be aquired for user: [{}]", objectId, objectType,
                lockType, userId);

        objectLockService.removeExpiredLocks();

        AcmObjectLock existingLock = objectLockService.findLock(objectId, objectType);

        if (existingLock != null)
        {
            // if current user is different then the creator throw an exception
            if (!existingLock.getCreator().equals(userId))
            {
                throw new AcmObjectLockException(String.format(
                        "[{}] not able to aquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object lock already exists for: [{}]",
                        userId, objectId, objectType, lockType, existingLock.getCreator()));
            }
        }

        log.trace("Object lock[objectId={}, objectType={}, lockType={}] can be aquired for user: [{}]", objectId, objectType,
                lockType, userId);
    }

    @Override
    public AcmObjectLock acquireObjectLock(Long objectId, String objectType, String lockType, Long expiry, boolean lockChildObjects,
            String userId)
            throws AcmObjectLockException
    {
        log.trace("Acquiring object lock[objectId={}, objectType={}, lockType={}] for user: [{}]", objectId, objectType,
                lockType, userId);

        if (expiry == null || expiry == 0)
        {
            expiry = getExpiryTime();
        }

        synchronized (getLock(objectId.toString() + objectType))
        {
            checkIfObjectLockCanBeAcquired(objectId, objectType, lockType, lockChildObjects, userId);

            AcmObjectLock existingLock = objectLockService.findLock(objectId, objectType);

            if (existingLock != null)
            {
                // if current user is same as creator of the lock than just return existingLock, else throw an exception
                if (existingLock.getCreator().equals(userId))
                {
                    return existingLock;
                }
                else
                {
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to aquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object lock already exists for: [{}]",
                            userId, objectId, objectType, lockType, existingLock.getCreator()));
                }
            }

            return objectLockService.createLock(objectId, objectType, lockType, expiry, userId);
        }
    }

    @Override
    public void releaseObjectLock(Long objectId, String objectType, String lockType, boolean unlockChildObjects, String userId)
            throws AcmObjectLockException
    {
        log.trace("Releasing object lock[objectId={}, objectType={}, lockType={}] for user: [{}]", objectId, objectType,
                lockType, userId);

        synchronized (getLock(objectId.toString() + objectType))
        {
            objectLockService.removeLock(objectId, objectType, lockType, userId);
        }
    }

    @Override
    public Long getExpiryTime()
    {
        return expiryTime;
    }

    public void setExpiryTime(Long expiryTime)
    {
        this.expiryTime = expiryTime;
    }

    private synchronized Object getLock(String lockKey)
    {
        Object lock = locks.get(lockKey);
        if (lock == null)
        {
            // we're the only one looking for this
            lock = new Object();
            locks.put(lockKey, lock);
        }
        return lock;
    }

    public AcmObjectLockService getObjectLockService()
    {
        return objectLockService;
    }

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }

}
