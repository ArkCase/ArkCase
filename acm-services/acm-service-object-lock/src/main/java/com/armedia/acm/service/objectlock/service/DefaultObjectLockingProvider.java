package com.armedia.acm.service.objectlock.service;

/*-
 * #%L
 * ACM Service: Object lock
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link ObjectLockingProvider}. It only checks if the same lock type on the same object is
 * acquired by another user and throws an exception if that is the case.
 * <p>
 * Created by bojan.milenkoski on 15/05/2018.
 */
public class DefaultObjectLockingProvider implements ObjectLockingProvider
{
    private final Logger log = LogManager.getLogger(getClass());

    private final Map<String, Object> locks = new ConcurrentHashMap<>();

    private AcmObjectLockService objectLockService;
    private Long expiryTimeInMilliseconds;

    @Override
    public void checkIfObjectLockCanBeAcquired(Long objectId, String objectType, String lockType, boolean checkChildObjects, String userId)
            throws AcmObjectLockException
    {
        log.trace("Checking if object lock[objectId={}, objectType={}, lockType={}] can be acquired for user: [{}]", objectId, objectType,
                lockType, userId);

        AcmObjectLock existingLock = objectLockService.findLock(objectId, objectType);

        Date now = new Date(System.currentTimeMillis());
        if (existingLock != null && now.after(existingLock.getExpiry()))
        {
            // lock has expired and will be removed
            objectLockService.removeLock(existingLock);
        }
        else if (existingLock != null)
        {
            // if current user is different then the creator throw an exception
            if (!existingLock.getCreator().equals(userId))
            {
                throw new AcmObjectLockException(String.format(
                        "[%s] not able to acquire object lock[objectId=%s, objectType=%s, lockType=%s]. Reason: Object lock already exists for: [%s]",
                        userId, objectId, objectType, lockType, existingLock.getCreator()));
            }
        }

        log.trace("Object lock[objectId={}, objectType={}, lockType={}] can be acquired for user: [{}]", objectId, objectType,
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
            expiry = getExpiryTimeInMilliseconds();
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
                            "[%s] not able to acquire object lock[objectId=%s, objectType=%s, lockType=%s]. Reason: Object lock already exists for: [%s]",
                            userId, objectId, objectType, lockType, existingLock.getCreator()));
                }
            }

            return objectLockService.createLock(objectId, objectType, lockType, expiry, userId);
        }
    }

    @Override
    public void releaseObjectLock(Long objectId, String objectType, String lockType, boolean unlockChildObjects, String userId, Long lockId)
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
    public String getObjectType()
    {
        return "DEFAULT";
    }

    @Override
    public Long getExpiryTimeInMilliseconds()
    {
        return expiryTimeInMilliseconds;
    }

    public void setExpiryTimeInMilliseconds(Long expiryTimeInMilliseconds)
    {
        this.expiryTimeInMilliseconds = expiryTimeInMilliseconds;
    }

    private synchronized Object getLock(String lockKey)
    {
        // we're the only one looking for this
        return locks.computeIfAbsent(lockKey, k -> new Object());
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
