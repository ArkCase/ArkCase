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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.ObjectLockingProvider;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Implementation of {@link ObjectLockingProvider} that handles locks for objects of type FOLDER.
 * 
 * Created by bojan.milenkoski on 10/05/2018.
 */
public class FolderLockingProvider implements ObjectLockingProvider
{
    private Logger log = LogManager.getLogger(getClass());

    private AcmObjectLockService objectLockService;
    private AcmFolderService folderService;
    private FileLockingProvider fileLockingProvider;
    private Long expiryTimeInMilliseconds;

    @Override
    public String getObjectType()
    {
        return EcmFileConstants.OBJECT_FOLDER_TYPE;
    }

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
            // check if the same lock can be acquired for folder children
            try
            {
                List<AcmObject> folderChildren = folderService.getFolderChildren(objectId);
                for (AcmObject child : folderChildren)
                {
                    if (EcmFileConstants.OBJECT_FOLDER_TYPE.equals(child.getObjectType().toUpperCase()))
                    {
                        checkIfObjectLockCanBeAcquired(child.getId(), objectType, lockType, checkChildObjects, userId);
                    }
                    if (EcmFileConstants.OBJECT_FILE_TYPE.equals(child.getObjectType().toUpperCase()))
                    {
                        fileLockingProvider.checkIfObjectLockCanBeAcquired(child.getId(), objectType, lockType, checkChildObjects, userId);
                    }
                }
            }
            catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
            {
                throw new AcmObjectLockException(e.getMessage());
            }
        }

        log.trace("Object lock[objectId={}, objectType={}, lockType={}] can be aquired for user: [{}]", objectId, objectType,
                lockType, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
            // acquire the same lock for folder children
            try
            {
                List<AcmObject> folderChildren = folderService.getFolderChildren(objectId);
                for (AcmObject child : folderChildren)
                {
                    if (EcmFileConstants.OBJECT_FOLDER_TYPE.equals(child.getObjectType().toUpperCase()))
                    {
                        acquireObjectLock(child.getId(), objectType, lockType, expiry, lockChildObjects, userId);
                    }
                    if (EcmFileConstants.OBJECT_FILE_TYPE.equals(child.getObjectType().toUpperCase()))
                    {
                        fileLockingProvider.acquireObjectLock(child.getId(), objectType, lockType, expiry, lockChildObjects, userId);
                    }
                }
            }
            catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
            {
                throw new AcmObjectLockException(e.getMessage());
            }
        }

        return objectLock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
            // release the same lock from folder children
            List<AcmObject> folderChildren = null;
            try
            {
                folderChildren = folderService.getFolderChildren(objectId);
            }
            catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
            {
                // folder does not exist do nothing
                return;
            }

            for (AcmObject child : folderChildren)
            {
                if (EcmFileConstants.OBJECT_FOLDER_TYPE.equals(child.getObjectType().toUpperCase()))
                {
                    releaseObjectLock(child.getId(), objectType, lockType, unlockChildObjects, userId, lockId);
                }
                if (EcmFileConstants.OBJECT_FILE_TYPE.equals(child.getObjectType().toUpperCase()))
                {
                    fileLockingProvider.releaseObjectLock(child.getId(), objectType, lockType, unlockChildObjects, userId, lockId);
                }
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

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public FileLockingProvider getFileLockingProvider()
    {
        return fileLockingProvider;
    }

    public void setFileLockingProvider(FileLockingProvider fileLockingProvider)
    {
        this.fileLockingProvider = fileLockingProvider;
    }
}
