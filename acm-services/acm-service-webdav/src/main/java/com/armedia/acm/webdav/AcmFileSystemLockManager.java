package com.armedia.acm.webdav;

import com.armedia.acm.service.objectlock.service.AcmObjectLockService;

import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import io.milton.http.LockInfo;
import io.milton.http.LockInfo.LockDepth;
import io.milton.http.LockInfo.LockScope;
import io.milton.http.LockInfo.LockType;
import io.milton.http.LockManager;
import io.milton.http.LockResult;
import io.milton.http.LockTimeout;
import io.milton.http.LockToken;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.LockableResource;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public class AcmFileSystemLockManager implements LockManager
{

    private static final class CurrentLock
    {
        final LockToken token;
        final String lockedByUser;

        public CurrentLock(LockToken token)
        {
            this.token = token;
            this.lockedByUser = token.info.lockedByUser;
        }

    }

    // This should be changed with a distributed collection like ones from Hazelcast or Apache Ignite
    private final ConcurrentMap<String, CurrentLock> locks = new ConcurrentHashMap<>();

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private AcmObjectLockService objectLockService;

    @Override
    public LockResult lock(LockTimeout timeout, LockInfo lockInfo, LockableResource resource) throws NotAuthorizedException
    {
        ReadLock readLock = readWriteLock.readLock();
        WriteLock writeLock = readWriteLock.writeLock();

        String resourceUniqueId = resource.getUniqueId();
        readLock.lock();
        CurrentLock lock = null;
        try
        {
            lock = locks.get(resourceUniqueId);
        } finally
        {
            readLock.unlock();
        }
        if (lock != null)
        {
            LockToken token = lock.token;
            writeLock.lock();
            try
            {
                if (token.isExpired())
                {
                    locks.remove(resourceUniqueId);
                } else if (lockInfo.lockedByUser.equals(lock.lockedByUser))
                {
                    token.setFrom(new Date());
                    return LockResult.success(token);
                } else
                {
                    return LockResult.failed(LockResult.FailureReason.ALREADY_LOCKED);
                }
            } finally
            {
                writeLock.unlock();
            }
        }

        LockToken token = new LockToken(UUID.randomUUID().toString(), lockInfo, timeout);
        lock = new CurrentLock(token);

        if (locks.putIfAbsent(resourceUniqueId, lock) != null)
        {
            return LockResult.failed(LockResult.FailureReason.ALREADY_LOCKED);
        }

        if (resource instanceof AcmFileResource)
        {
            AcmFileResource fileResource = (AcmFileResource) resource;
            // Add authentication to WebDav map
            Authentication authentication = fileResource.getResourceFactory().getSecurityManager()
                    .getAuthenticationForTicket(fileResource.getAcmTicket());
            // Create lock in Arkcase DB
            getObjectLockService().createLock(fileResource.getId(), fileResource.getFileType(), fileResource.getLockType(), true,
                    authentication);
        }

        return LockResult.success(token);
    }

    @Override
    public LockResult refresh(String tokenId, LockableResource resource) throws NotAuthorizedException
    {
        ReadLock readLock = readWriteLock.readLock();
        readLock.lock();
        CurrentLock lock = null;
        try
        {
            lock = locks.get(resource.getUniqueId());
            if (lock == null)
            {
                return LockResult.failed(LockResult.FailureReason.PRECONDITION_FAILED);
            } else if (!lock.token.tokenId.equals(tokenId))
            {
                throw new NotAuthorizedException(resource);
            } else
            {
                lock.token.setFrom(new Date());
                return LockResult.success(lock.token);
            }
        } finally
        {
            readLock.unlock();
        }
    }

    @Override
    public void unlock(String tokenId, LockableResource resource) throws NotAuthorizedException
    {
        ReadLock readLock = readWriteLock.readLock();
        WriteLock writeLock = readWriteLock.writeLock();

        String resourceUniqueId = resource.getUniqueId();
        readLock.lock();
        CurrentLock lock = null;
        try
        {
            lock = locks.get(resourceUniqueId);
        } finally
        {
            readLock.unlock();
        }
        if (lock != null)
        {
            LockToken token = lock.token;
            writeLock.lock();
            try
            {
                if (token.isExpired() || token.tokenId.equals(tokenId))
                {
                    locks.remove(resourceUniqueId);
                } else if (!token.tokenId.equals(tokenId))
                {
                    throw new NotAuthorizedException(resource);
                }
            } finally
            {
                writeLock.unlock();
            }
        }
        if (resource instanceof AcmFileResource)
        {
            AcmFileResource fileResource = (AcmFileResource) resource;
            Authentication authentication = fileResource.getResourceFactory().getSecurityManager()
                    .getAuthenticationForTicket(fileResource.getAcmTicket());
            // Remove lock from Arkcase DB
            getObjectLockService().removeLock(fileResource.getId(), fileResource.getFileType(), fileResource.getLockType(), authentication);
            // Remove authentication from WebDav map
            fileResource.getResourceFactory().getSecurityManager().removeAuthenticationForTicket(fileResource.getAcmTicket());
        }
    }

    @Override
    public LockToken getCurrentToken(LockableResource resource)
    {
        ReadLock readLock = readWriteLock.readLock();
        readLock.lock();
        CurrentLock lock = null;
        try
        {
            lock = locks.get(resource.getUniqueId());
        } finally
        {
            readLock.unlock();
        }

        if (lock == null)
        {
            return null;
        }

        LockToken token = new LockToken();
        token.info = new LockInfo(LockScope.EXCLUSIVE, LockType.WRITE, lock.lockedByUser, LockDepth.ZERO);
        token.info.lockedByUser = lock.lockedByUser;
        token.timeout = lock.token.timeout;
        token.tokenId = lock.token.tokenId;

        return token;
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
