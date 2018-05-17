package com.armedia.acm.plugins.ecm.service.lock;

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
    private Long expiryTime;

    @Override
    public void checkIfObjectLockCanBeAcquired(Long objectId, String objectType, String lockType, boolean checkChildObjects, String userId)
            throws AcmObjectLockException
    {
        log.trace("Checking if object lock[objectId={}, objectType={}, lockType={}] can be aquired for user: [{}]", objectId, objectType,
                lockType, userId);

        FileLockType objectLockType = null;
        try
        {
            objectLockType = FileLockType.valueOf(lockType);
        }
        catch (Exception e)
        {
            throw new AcmObjectLockException("Unknown lock type: " + lockType);
        }

        objectLockService.removeExpiredLocks();

        AcmObjectLock existingLock = objectLockService.findLock(objectId, objectType);

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
                    if (!existingLock.getLockType().equals(FileLockType.READ.name())
                            && !existingLock.getLockType().equals(lockType))
                    {
                        throw new AcmObjectLockException(String.format(
                                "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
                                userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                    }
                    break;
                case SHARED_WRITE:
                    if (!existingLock.getLockType().equals(FileLockType.READ.name())
                            && !existingLock.getLockType().equals(lockType))
                    {
                        throw new AcmObjectLockException(String.format(
                                "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
                                userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                    }
                    break;
                case DELETE:
                    if (!existingLock.getLockType().equals(FileLockType.READ.name())
                            && !existingLock.getLockType().equals(lockType))
                    {
                        throw new AcmObjectLockException(String.format(
                                "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
                                userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                    }
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
                    if (!existingLock.getLockType().equals(FileLockType.READ.name()))
                    {
                        throw new AcmObjectLockException(String.format(
                                "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
                                userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                    }
                    break;
                case SHARED_WRITE:
                    if (!existingLock.getLockType().equals(FileLockType.READ.name())
                            && !existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
                    {
                        throw new AcmObjectLockException(String.format(
                                "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
                                userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                    }
                    break;
                case DELETE:
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
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

        FileLockType objectLockType = null;
        try
        {
            objectLockType = FileLockType.valueOf(lockType);
        }
        catch (Exception e)
        {
            throw new AcmObjectLockException("Unknown lock type: " + lockType);
        }

        if (expiry == null || expiry == 0)
        {
            expiry = getExpiryTime();
        }

        checkIfObjectLockCanBeAcquired(objectId, objectType, lockType, lockChildObjects, userId);

        AcmObjectLock existingLock = objectLockService.findLock(objectId, objectType);

        String lockedByUserId = userId;

        if (existingLock != null)
        {
            if (objectLockType == FileLockType.READ
                    && !existingLock.getLockType().equals(FileLockType.READ.name()))
            {
                return getReadLock(objectId, objectType, lockType, expiry, userId);
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
    public synchronized void releaseObjectLock(Long objectId, String objectType, String lockType, boolean unlockChildObjects, String userId)
            throws AcmObjectLockException
    {
        log.trace("Releasing object lock[objectId={}, objectType={}, lockType={}] for user: [{}]", objectId, objectType,
                lockType, userId);

        FileLockType objectLockType = null;
        try
        {
            objectLockType = FileLockType.valueOf(lockType);
        }
        catch (Exception e)
        {
            throw new AcmObjectLockException("Unknown lock type: " + lockType);
        }

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
                if (!existingLock.getLockType().equals(FileLockType.READ.name())
                        && !existingLock.getLockType().equals(FileLockType.WRITE.name()))
                {
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
                            userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                }
                break;
            case SHARED_WRITE:
                if (!existingLock.getLockType().equals(FileLockType.READ.name())
                        && !existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
                {
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
                            userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                }
                break;
            case DELETE:
                if (!existingLock.getLockType().equals(FileLockType.READ.name())
                        && !existingLock.getLockType().equals(FileLockType.DELETE.name()))
                {
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
                            userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                }
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
                if (!existingLock.getLockType().equals(FileLockType.READ.name()))
                {
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
                            userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                }
                break;
            case SHARED_WRITE:
                if (existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
                {
                    userId = existingLock.getCreator();
                }
                if (!existingLock.getLockType().equals(FileLockType.READ.name())
                        && !existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
                {
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
                            userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                }
                break;
            case DELETE:
                throw new AcmObjectLockException(String.format(
                        "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object is already has a lock of type {} by user: [{}]",
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

            folderLockingProvider.releaseObjectLock(container.getFolder().getId(), objectType, lockType, unlockChildObjects, userId);
            if (!container.getFolder().equals(container.getAttachmentFolder()))
            {
                folderLockingProvider.releaseObjectLock(container.getAttachmentFolder().getId(), objectType, lockType, unlockChildObjects,
                        userId);
            }
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

    private AcmObjectLock getReadLock(Long objectId, String objectType, String lockType, Long expiry, String userId)
    {
        AcmObjectLock readLock = new AcmObjectLock();
        readLock.setObjectId(objectId);
        readLock.setObjectType(objectType);
        readLock.setLockType(lockType);
        readLock.setCreated(new Date());
        readLock.setExpiry(new Date(readLock.getCreated().getTime() + expiry));
        readLock.setCreator(userId);
        return readLock;
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
