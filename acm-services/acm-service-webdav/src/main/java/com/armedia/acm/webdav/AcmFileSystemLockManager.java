package com.armedia.acm.webdav;

/*-
 * #%L
 * ACM Service: WebDAV Integration Library
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

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.springframework.security.core.Authentication;

import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;

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

    // This should be changed with a distributed collection like ones from Hazelcast or Apache Ignite
    private final ConcurrentMap<String, CurrentLock> locks = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private AcmObjectLockingManager objectLockingManager;

    @Override
    public LockResult refresh(String tokenId, LockTimeout timeout, LockableResource resource) throws NotAuthorizedException
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
            }
            else if (!lock.token.tokenId.equals(tokenId))
            {
                throw new NotAuthorizedException(resource);
            }
            else
            {
                lock.token.setTimeout(timeout);
                lock.token.setFrom(new Date());
                return LockResult.success(lock.token);
            }
        }
        finally
        {
            readLock.unlock();
        }
    }

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
        }
        finally
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
                }
                else if (lockInfo.lockedByUser.equals(lock.lockedByUser))
                {
                    token.setFrom(new Date());
                    return LockResult.success(token);
                }
                else
                {
                    return LockResult.failed(LockResult.FailureReason.ALREADY_LOCKED);
                }
            }
            finally
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
                    .getAuthenticationForTicket(fileResource.getUserId());
            // Create lock in Arkcase DB
            try
            {
                getObjectLockingManager().acquireObjectLock(fileResource.getId(), fileResource.getFileType(), fileResource.getLockType(),
                        null, false, authentication.getName());
            }
            catch (AcmObjectLockException e)
            {
                throw new NotAuthorizedException(fileResource, e);
            }
        }

        return LockResult.success(token);
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
        }
        finally
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
                }
                else if (!token.tokenId.equals(tokenId))
                {
                    throw new NotAuthorizedException(resource);
                }
            }
            finally
            {
                writeLock.unlock();
            }
        }
        if (resource instanceof AcmFileResource)
        {
            AcmFileResource fileResource = (AcmFileResource) resource;
            Authentication authentication = fileResource.getResourceFactory().getSecurityManager()
                    .getAuthenticationForTicket(fileResource.getUserId());

            // Remove lock from Arkcase DB
            try
            {
                getObjectLockingManager().releaseObjectLock(fileResource.getId(), fileResource.getFileType(), fileResource.getLockType(),
                        false, authentication.getName(), null);
            }
            catch (AcmObjectLockException e)
            {
                throw new NotAuthorizedException(fileResource, e);
            }

            // Remove authentication from WebDav map
            fileResource.getResourceFactory().getSecurityManager().removeAuthenticationForTicket(fileResource.getUserId());
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
        }
        finally
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

    public AcmObjectLockingManager getObjectLockingManager()
    {
        return objectLockingManager;
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
    }

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

}
