package com.armedia.acm.plugins.ecm.service.lock;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.ObjectLockingProvider;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class DefaultEcmObjectLockingProvider implements ObjectLockingProvider
{
    private final Logger log = LogManager.getLogger(getClass());
    private AcmObjectLockService objectLockService;
    private Long expiryTimeInMilliseconds;
    private UserDao userDao;

    private void throwErrorOnExistingLockExceptForReadLock(Long objectId, String objectType, String lockType, String userId,
            AcmObjectLock existingLock, boolean errorOnSameExistingLockType, boolean acquireLock)
    {
        if (!existingLock.getLockType().equals(FileLockType.READ.name())
                && (errorOnSameExistingLockType || !existingLock.getLockType().equals(lockType)))
        {
            String acmUserFullName = userDao.findByPrefix(existingLock.getCreator())
                    .stream()
                    .findFirst()
                    .map(AcmUser::getFullName)
                    .orElse(existingLock.getCreator());

            log.error(
                    " {} not able to {} object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: {}",
                    userId, acquireLock ? "acquire" : "release", objectId, objectType, lockType, existingLock.getLockType(),
                    existingLock.getCreator());
            throw new AcmObjectLockException("This document is locked by " + acmUserFullName);
        }
    }

    @Override
    public Long getExpiryTimeInMilliseconds()
    {

        return expiryTimeInMilliseconds;
    }

    @Override
    public void checkIfObjectLockCanBeAcquired(Long objectId, String objectType, String lockType, boolean checkChildObjects, String userId)
            throws AcmObjectLockException
    {
        log.trace("Checking if object lock[objectId={}, objectType={}, lockType={}] can be acquired for user: [{}]", objectId,
                objectType, lockType, userId);

        FileLockType objectLockType = FileLockType.fromName(lockType);

        AcmObjectLock existingLock = objectLockService.findLock(objectId, objectType);

        Date now = new Date(System.currentTimeMillis());
        if (existingLock != null && existingLock.getExpiry() != null && now.after(existingLock.getExpiry()))
        {
            // lock has expired and will be removed
            objectLockService.removeLock(existingLock);
        }
        else if (existingLock != null)
        {
            boolean lockCreatorSameAsTheAcquirer = existingLock.getCreator().equals(userId);
            boolean errorOnSameExistingLockType = !lockCreatorSameAsTheAcquirer;

            switch (objectLockType)
            {
            case READ:
                // we always allow getting a READ lock
                break;
            case WRITE:
                throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock,
                        errorOnSameExistingLockType, true);
                break;
            case SHARED_WRITE:
                throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock,
                        false, true);
                break;
            case DELETE:
            {
                if (lockCreatorSameAsTheAcquirer)
                {
                    throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock,
                            false, true);
                }
                else
                {
                    throw new AcmObjectLockException(String.format(
                            "[%s] not able to acquire object lock[objectId=%s, objectType=%s, lockType=%s]. Reason: Object already has a lock of type %s by user: [%s]",
                            userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                }
                break;
            }
            default:
                throw new AcmObjectLockException("Unimplemented handling of lock type: " + lockType);
            }
        }

        log.trace("Object lock[objectId={}, objectType={}, lockType={}] can be acquired for user: [{}]", objectId, objectType,
                lockType, userId);
    }

    @Override
    public AcmObjectLock acquireObjectLock(Long objectId, String objectType, String lockType, Long expiry, boolean lockChildObjects,
            String userId) throws AcmObjectLockException
    {
        log.trace("Acquiring object lock[objectId={}, objectType={}, lockType={}] for user: [{}]", objectId, objectType,
                lockType, userId);

        FileLockType objectLockType = FileLockType.fromName(lockType);

        if (expiry == null || expiry == 0)
        {
            expiry = getExpiryTimeInMilliseconds();
        }

        checkIfObjectLockCanBeAcquired(objectId, objectType, lockType, lockChildObjects, userId);

        AcmObjectLock existingLock = objectLockService.findLock(objectId, objectType);

        String lockedByUserId = userId;

        if (existingLock != null)
        {
            if (objectLockType == FileLockType.READ
                    && !existingLock.getLockType().equals(FileLockType.READ.name()))
            {
                return buildLock(objectId, objectType, lockType, expiry, userId);
            }

            // do not update userId for a shared lock
            if ((objectLockType == FileLockType.SHARED_WRITE)
                    && existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
            {
                lockedByUserId = existingLock.getCreator();
            }
        }
        return objectLockService.createLock(objectId, objectType, lockType, expiry, lockedByUserId);
    }

    @Override
    public void releaseObjectLock(Long objectId, String objectType, String lockType, boolean unlockChildObjects, String userId, Long lockId)
            throws AcmObjectLockException
    {
        log.trace("Releasing object lock[objectId={}, objectType={}, lockType={}] for user: [{}]", objectId, objectType,
                lockType, userId);

        FileLockType objectLockType = FileLockType.fromName(lockType);

        AcmObjectLock existingLock = objectLockService.findLock(objectId, objectType);
        if (existingLock == null)
        {
            // no lock exist, so no lock to release
            return;
        }

        boolean lockCreatorSameAsTheAcquirer = existingLock.getCreator().equals(userId);
        boolean errorOnSameExistingLockType = !lockCreatorSameAsTheAcquirer;

        switch (objectLockType)
        {
        case READ:
            if (lockCreatorSameAsTheAcquirer && !existingLock.getLockType().equals(FileLockType.READ.name()))
            {
                return;
            }
            else if (!lockCreatorSameAsTheAcquirer)
            {
                return;
            }
            break;
        case WRITE:
            throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, errorOnSameExistingLockType, false);
            break;
        case SHARED_WRITE:
            if (existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()) && !existingLock.getId().equals(lockId))
            {
                throw new AcmObjectLockException(String.format(
                        "[%s] not able to release object lock[objectId=%s, objectType=%s, lockType=%s]. Reason: Object lockId mismatch!",
                        userId, objectId, objectType, lockType));
            }
            if (existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
            {
                userId = existingLock.getCreator();
            }
            throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, false, false);
            break;
        case DELETE:
            if (lockCreatorSameAsTheAcquirer)
            {
                throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, false, false);
            }
            else
            {
                log.error(
                        " {} not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: {}",
                        userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator());
                throw new AcmObjectLockException(String.format(
                        "%s not able to release object lock[objectId=%s, objectType=%s, lockType=%s]. Reason: Object already has a lock of type %s by user: %s",
                        userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
            }
            break;
        default:
            throw new AcmObjectLockException("Unimplemented handling of lock type: " + lockType);
        }
        objectLockService.removeLock(objectId, objectType, lockType, userId);
    }

    @Override
    public String getObjectType()
    {
        return null;
    }

    public AcmObjectLockService getObjectLockService()
    {
        return objectLockService;
    }

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }

    public void setExpiryTimeInMilliseconds(Long expiryTimeInMilliseconds)
    {
        this.expiryTimeInMilliseconds = expiryTimeInMilliseconds;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
