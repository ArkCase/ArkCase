package com.armedia.acm.plugins.ecm.service.lock;

import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.ObjectLockingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Implementation of {@link ObjectLockingProvider} that handles locks for objects of type FILE.
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
public class FileLockingProvider implements ObjectLockingProvider
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmObjectLockService objectLockService;
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
                                "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
                                userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                    }
                    break;
                case SHARED_WRITE:
                    if (!existingLock.getLockType().equals(FileLockType.READ.name())
                            && !existingLock.getLockType().equals(lockType))
                    {
                        throw new AcmObjectLockException(String.format(
                                "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
                                userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                    }
                    break;
                case DELETE:
                    if (!existingLock.getLockType().equals(FileLockType.READ.name())
                            && !existingLock.getLockType().equals(lockType))
                    {
                        throw new AcmObjectLockException(String.format(
                                "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
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
                                "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
                                userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                    }
                    break;
                case SHARED_WRITE:
                    if (!existingLock.getLockType().equals(FileLockType.READ.name())
                            && !existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
                    {
                        throw new AcmObjectLockException(String.format(
                                "[{}] not able to acquire object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
                                userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                    }
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

        log.trace("Object lock[objectId={}, objectType={}, lockType={}] can be aquired for user: [{}]", objectId, objectType,
                lockType, userId);
    }

    @Override
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

        return objectLockService.createLock(objectId, objectType, lockType, expiry, lockedByUserId);
    }

    @Override
    public synchronized void releaseObjectLock(Long objectId, String objectType, String lockType, boolean unlockChildObjects, String userId,
            Long lockId)
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
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
                            userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                }
                break;
            case SHARED_WRITE:
                if (existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()) && !existingLock.getId().equals(lockId))
                {
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object lockId mismatch!",
                            userId, objectId, objectType, lockType, existingLock.getLockType()));
                }
                if (!existingLock.getLockType().equals(FileLockType.READ.name())
                        && !existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
                {
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
                            userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                }
                break;
            case DELETE:
                if (!existingLock.getLockType().equals(FileLockType.READ.name())
                        && !existingLock.getLockType().equals(FileLockType.DELETE.name()))
                {
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
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
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
                            userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                }
                break;
            case SHARED_WRITE:
                if (existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()) && !existingLock.getId().equals(lockId))
                {
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object lockId mismatch!",
                            userId, objectId, objectType, lockType, existingLock.getLockType()));
                }
                if (existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
                {
                    userId = existingLock.getCreator();
                }
                if (!existingLock.getLockType().equals(FileLockType.READ.name())
                        && !existingLock.getLockType().equals(FileLockType.SHARED_WRITE.name()))
                {
                    throw new AcmObjectLockException(String.format(
                            "[{}] not able to release object lock[objectId={}, objectType={}, lockType={}]. Reason: Object already has a lock of type {} by user: [{}]",
                            userId, objectId, objectType, lockType, existingLock.getLockType(), existingLock.getCreator()));
                }
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

}
