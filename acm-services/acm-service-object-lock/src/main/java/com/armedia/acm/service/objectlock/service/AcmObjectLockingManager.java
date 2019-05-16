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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.spring.SpringContextHolder;

import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is used throughout the application to acquire and release locks on {@link AcmObject}.
 * It uses implementations of {@link ObjectLockingProvider} to acquire and release locks based on the object types the
 * implementation is for.
 * If no implementation is found for a specific object type, the default implementation is used.
 * 
 * Created by bojan.milenkoski on 03/05/20186.
 */
public class AcmObjectLockingManager implements InitializingBean
{
    private Map<String, ObjectLockingProvider> objectLockingProvidersMap;
    private ObjectLockingProvider defaultObjectLockingProvider;
    private SpringContextHolder springContextHolder;

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
    public void checkIfObjectLockCanBeAquired(Long objectId, String objectType, String lockType, boolean checkChildObjects, String userId)
            throws AcmObjectLockException
    {
        getObjectLockingProvider(objectType).checkIfObjectLockCanBeAcquired(objectId, objectType, lockType, checkChildObjects, userId);
    }

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
    public AcmObjectLock acquireObjectLock(Long objectId, String objectType, String lockType, Long expiry, boolean lockChildObjects,
            String userId)
            throws AcmObjectLockException
    {
        if (objectId == null)
        {
            throw new AcmObjectLockException("Cannot acquire lock object with id=null!");
        }
        if (expiry == null || expiry == 0)
        {
            expiry = getObjectLockingProvider(objectType).getExpiryTimeInMilliseconds();
        }
        return getObjectLockingProvider(objectType).acquireObjectLock(objectId, objectType, lockType, expiry, lockChildObjects, userId);
    }

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
    public void releaseObjectLock(Long objectId, String objectType, String lockType, boolean unlockChildObjects, String userId, Long lockId)
            throws AcmObjectLockException
    {
        if (objectId == null)
        {
            throw new AcmObjectLockException("Cannot release lock from object with id=null!");
        }
        getObjectLockingProvider(objectType).releaseObjectLock(objectId, objectType, lockType, unlockChildObjects, userId, lockId);
    }

    private ObjectLockingProvider getObjectLockingProvider(String objectType)
    {
        return objectLockingProvidersMap.getOrDefault(objectType, defaultObjectLockingProvider);
    }

    public Map<String, ObjectLockingProvider> getObjectLockingProvidersMap()
    {
        return objectLockingProvidersMap;
    }

    public void setObjectLockingProvidersMap(Map<String, ObjectLockingProvider> objectLockingProvidersMap)
    {
        this.objectLockingProvidersMap = objectLockingProvidersMap;
    }

    public ObjectLockingProvider getDefaultObjectLockingProvider()
    {
        return defaultObjectLockingProvider;
    }

    public void setDefaultObjectLockingProvider(ObjectLockingProvider defaultObjectLockingProvider)
    {
        this.defaultObjectLockingProvider = defaultObjectLockingProvider;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    @Override
    public void afterPropertiesSet()
    {
        objectLockingProvidersMap = springContextHolder.getAllBeansOfType(ObjectLockingProvider.class)
                .values()
                .stream()
                .collect(Collectors.toMap(ObjectLockingProvider::getObjectType, Function.identity()));
    }
}
