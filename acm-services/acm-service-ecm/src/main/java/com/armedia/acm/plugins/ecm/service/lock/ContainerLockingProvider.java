package com.armedia.acm.plugins.ecm.service.lock;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.ObjectLockingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Implementation of {@link ObjectLockingProvider} that handles locks for objects of type CONTAINER.
 * 
 * Created by bojan.milenkoski on 10/05/2018.
 */
public class ContainerLockingProvider implements ObjectLockingProvider
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmObjectLockService objectLockService;
    private AcmContainerDao containerDao;
    private FolderLockingProvider folderLockingProvider;
    private Long expiryTimeInMilliseconds;

    @Override
    public void checkIfObjectLockCanBeAcquired(Long objectId, String objectType, String lockType, boolean checkChildObjects, String userId)
            throws AcmObjectLockException
    {
        log.trace("Checking if object lock[objectId={}, objectType={}, lockType={}] can be aquired for user: [{}]", objectId, objectType,
                lockType, userId);

        FileLockType objectLockType = FileLockType.fromName(lockType);

        AcmObjectLock existingLock = objectLockService.findLock(objectId, objectType);

        Date now = new Date(System.currentTimeMillis());
        if (existingLock != null && now.after(existingLock.getExpiry()))
        {
            //lock has expired and will be removed
            objectLockService.removeLock(existingLock);
            existingLock = null;
        }

        if (existingLock != null)
        {
            if (existingLock.getCreator().equals(userId))
            {
                switch (objectLockType)
                {
                case READ:
                    // we always allow getting a READ lock
                    break;
                case WRITE:
                    throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, false);
                    break;
                case SHARED_WRITE:
                    throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, false);
                    break;
                case DELETE:
                    throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, false);
                    break;
                default:
                    throw new AcmObjectLockException("Unimplemented handling of lock type: " + lockType);
                }
            }
            else
            {
                switch (objectLockType)
                {
                case READ:
                    // we always allow getting a READ lock
                    break;
                case WRITE:
                    throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, true);
                    break;
                case SHARED_WRITE:
                    throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, false);
                    break;
                case DELETE:
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
                            userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                default:
                    throw new AcmObjectLockException("Unimplemented handling of lock type: " + lockType);
                }
            }
        }

        if (checkChildObjects)
        {
            // check if the same lock can be acquired for root and attachment folders
            AcmContainer container = containerDao.find(objectId);
            folderLockingProvider.checkIfObjectLockCanBeAcquired(container.getFolder().getId(), objectType, lockType, checkChildObjects,
                    userId);
            if (!container.getFolder().equals(container.getAttachmentFolder()))
            {
                folderLockingProvider.checkIfObjectLockCanBeAcquired(container.getAttachmentFolder().getId(), objectType, lockType,
                        checkChildObjects, userId);
            }

            log.trace("Object lock[objectId={}, objectType={}, lockType={}] can be aquired for user: [{}]", objectId, objectType,
                    lockType, userId);
        }
    }

    @Override
    @Transactional
    public synchronized AcmObjectLock acquireObjectLock(Long objectId, String objectType, String lockType, Long expiry,
            boolean lockChildObjects, String userId)
            throws AcmObjectLockException
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
                return getLock(objectId, objectType, lockType, expiry, userId);
            }

            // do not update userId for a shared lock
            if ((objectLockType == FileLockType.SHARED_WRITE)
                    && existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
            {
                lockedByUserId = existingLock.getCreator();
            }
        }

        AcmObjectLock objectLock = objectLockService.createLock(objectId, objectType, lockType, expiry, lockedByUserId);

        if (lockChildObjects)
        {
            // acquire the same lock for root and attachment folders
            AcmContainer container = containerDao.find(objectId);
            folderLockingProvider.acquireObjectLock(container.getFolder().getId(), objectType, lockType, expiry, lockChildObjects, userId);
            if (!container.getFolder().equals(container.getAttachmentFolder()))
            {
                folderLockingProvider.acquireObjectLock(container.getAttachmentFolder().getId(), objectType, lockType, expiry,
                        lockChildObjects,
                        userId);
            }
        }

        return objectLock;
    }

    @Override
    @Transactional
    public synchronized void releaseObjectLock(Long objectId, String objectType, String lockType, boolean unlockChildObjects, String userId,
            Long lockId)
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

        if (existingLock.getCreator().equals(userId))
        {
            switch (objectLockType)
            {
            case READ:
                if (!existingLock.getLockType().equals(FileLockType.READ.name()))
                {
                    return;
                }
                break;
            case WRITE:
                throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, false);
                break;
            case SHARED_WRITE:
                throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, false);
                break;
            case DELETE:
                throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, false);
                break;
            default:
                throw new AcmObjectLockException("Unimplemented handling of lock type: " + lockType);
            }
        }
        else
        {
            switch (objectLockType)
            {
            case READ:
                return;
            case WRITE:
                throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, true);
                break;
            case SHARED_WRITE:
                if (existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
                {
                    userId = existingLock.getCreator();
                }
                throwErrorOnExistingLockExceptForReadLock(objectId, objectType, lockType, userId, existingLock, false);
                break;
            case DELETE:
                throw new AcmObjectLockException(String.format(
                        "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
                        userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
            default:
                throw new AcmObjectLockException("Unimplemented handling of lock type: " + lockType);
            }
        }

        objectLockService.removeLock(objectId, objectType, lockType, userId);

        if (unlockChildObjects)
        {
            // release the same lock from root and attachment folders
            AcmContainer container = containerDao.find(objectId);
            if (container == null)
            {
                // container does not exist do nothing
                return;
            }

            folderLockingProvider.releaseObjectLock(container.getFolder().getId(), objectType, lockType, unlockChildObjects, userId,
                    lockId);
            if (!container.getFolder().equals(container.getAttachmentFolder()))
            {
                folderLockingProvider.releaseObjectLock(container.getAttachmentFolder().getId(), objectType, lockType, unlockChildObjects,
                        userId, lockId);
            }
        }
    }

    private void throwErrorOnExistingLockExceptForReadLock(Long objectId, String objectType, String lockType, String userId,
            AcmObjectLock existingLock, boolean errorOnSameExistingLockType)
    {
        if (!existingLock.getLockType().equals(FileLockType.READ.name())
                && (errorOnSameExistingLockType || !existingLock.getLockType().equals(lockType)))
        {
            throw new AcmObjectLockException(String.format(
                    "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
                    userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
        }
    }

    private AcmObjectLock getLock(Long objectId, String objectType, String lockType, Long expiry, String userId)
    {
        AcmObjectLock lock = new AcmObjectLock();
        lock.setObjectId(objectId);
        lock.setObjectType(objectType);
        lock.setLockType(lockType);
        lock.setCreated(new Date());
        lock.setExpiry(new Date(lock.getCreated().getTime() + expiry));
        lock.setCreator(userId);
        return lock;
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

    public AcmObjectLockService getObjectLockService()
    {
        return objectLockService;
    }

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }

    public AcmContainerDao getContainerDao()
    {
        return containerDao;
    }

    public void setContainerDao(AcmContainerDao containerDao)
    {
        this.containerDao = containerDao;
    }

    public FolderLockingProvider getFolderLockingProvider()
    {
        return folderLockingProvider;
    }

    public void setFolderLockingProvider(FolderLockingProvider folderLockingProvider)
    {
        this.folderLockingProvider = folderLockingProvider;
    }
}
