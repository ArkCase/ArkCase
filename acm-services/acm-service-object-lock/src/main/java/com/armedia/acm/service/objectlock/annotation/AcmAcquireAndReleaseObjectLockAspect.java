package com.armedia.acm.service.objectlock.annotation;

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
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.web.api.MDCConstants;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Around aspect targeting annotation: {@link AcmAcquireAndReleaseObjectLock}
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
@Aspect
@Component
public class AcmAcquireAndReleaseObjectLockAspect
{
    private Logger log = LogManager.getLogger(getClass());

    private AcmObjectLockingManager objectLockingManager;
    private AcmObjectLockService objectLockService;

    @Around(value = "@annotation(acmAcquireAndReleaseObjectLocks)")
    public Object aroundAcquireObjectLock(ProceedingJoinPoint pjp, AcmAcquireAndReleaseObjectLock.List acmAcquireAndReleaseObjectLocks)
            throws Throwable, AcmObjectLockException
    {
        Object[] args = pjp.getArgs();
        String userId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY);
        if (userId == null)
        {
            log.warn("Acquiring and Releasing object lock without userId set in {}.{}", pjp.getSignature().getDeclaringTypeName(),
                    pjp.getSignature().getName());
            userId = "no-user-id";
        }

        List<AcmAcquireAndReleaseObjectLock> acquiredLocks = new ArrayList<>();
        try
        {
            for (AcmAcquireAndReleaseObjectLock acmAcquireAndReleaseObjectLock : acmAcquireAndReleaseObjectLocks.value())
            {
                String objectType = acmAcquireAndReleaseObjectLock.objectType();
                Long objectId = getObjectId(pjp, acmAcquireAndReleaseObjectLock, args);
                String lockType = acmAcquireAndReleaseObjectLock.lockType();
                boolean lockChildObjects = acmAcquireAndReleaseObjectLock.lockChildObjects();

                AcmObjectLock objectLock = null;

                // if objectId is null, probably we are trying to lock an object before it is persisted for the first
                // time, we cannot lock such objects, but we don't raise an error
                if (objectId != null)
                {
                    // acquire lock only if the user doesn't have the lock already
                    objectLock = objectLockService.findLock(objectId, objectType);
                    if (objectLock == null
                            || (objectLock != null && objectLock.getLockType().equals(lockType) && !objectLock.getCreator().equals(userId)))
                    {
                        objectLockingManager.acquireObjectLock(objectId, objectType, lockType, null, lockChildObjects, userId);
                    }
                    acquiredLocks.add(acmAcquireAndReleaseObjectLock);
                }
            }
            Object ret = null;
            try
            {
                ret = pjp.proceed();
            }
            finally
            {
                for (AcmAcquireAndReleaseObjectLock acmAcquireAndReleaseObjectLock : acmAcquireAndReleaseObjectLocks.value())
                {
                    String objectType = acmAcquireAndReleaseObjectLock.objectType();
                    Long objectId = getObjectId(pjp, acmAcquireAndReleaseObjectLock, args);
                    String lockType = acmAcquireAndReleaseObjectLock.lockType();
                    boolean unlockChildObjects = acmAcquireAndReleaseObjectLock.unlockChildObjects();
                    Long lockId = acmAcquireAndReleaseObjectLock.lockIdArgIndex() != -1
                            ? (Long) args[acmAcquireAndReleaseObjectLock.lockIdArgIndex()]
                            : null;
                    // release the lock only if it was acquired previously
                    if (objectId != null && acquiredLocks.contains(acmAcquireAndReleaseObjectLock))
                    {
                        objectLockingManager.releaseObjectLock(objectId, objectType, lockType, unlockChildObjects, userId, lockId);
                    }
                }
            }

            return ret;
        }
        catch (Exception e)
        {
            // log exception and re-throw
            log.error(e.getMessage());
            throw e;
        }
    }

    @Around(value = "@annotation(acmAcquireAndReleaseObjectLock)")
    public Object aroundAcquireAndReleaseObjectLockSingle(ProceedingJoinPoint pjp,
            AcmAcquireAndReleaseObjectLock acmAcquireAndReleaseObjectLock)
            throws Throwable, AcmObjectLockException
    {
        AcmAcquireAndReleaseObjectLock.List locks = new AcmAcquireAndReleaseObjectLock.List()
        {

            @Override
            public Class<? extends Annotation> annotationType()
            {
                return AcmAcquireAndReleaseObjectLock.class;
            }

            @Override
            public AcmAcquireAndReleaseObjectLock[] value()
            {
                return new AcmAcquireAndReleaseObjectLock[] {
                        acmAcquireAndReleaseObjectLock
                };
            }
        };
        return aroundAcquireObjectLock(pjp, locks);
    }

    private Long getObjectId(ProceedingJoinPoint pjp, AcmAcquireAndReleaseObjectLock acquireAndReleaseObjectLock, Object[] args)
    {
        if (acquireAndReleaseObjectLock.objectIdArgIndex() != -1)
        {
            if (!(args[acquireAndReleaseObjectLock.objectIdArgIndex()] instanceof Long))
            {
                throw new RuntimeException(
                        String.format("AcmAcquireAndReleaseObjectLock objectIdArgIndex does not resolve to Long argument in {}.{}",
                                pjp.getSignature().getDeclaringTypeName(),
                                pjp.getSignature().getName()));
            }
            return (Long) args[acquireAndReleaseObjectLock.objectIdArgIndex()];
        }
        else if (acquireAndReleaseObjectLock.acmObjectArgIndex() != -1)
        {
            if (!(args[acquireAndReleaseObjectLock.acmObjectArgIndex()] instanceof AcmObject))
            {
                throw new RuntimeException(
                        String.format("AcmAcquireAndReleaseObjectLock acmObjectArgIndex does not resolve to AcmObject argument in {}.{}",
                                pjp.getSignature().getDeclaringTypeName(),
                                pjp.getSignature().getName()));
            }
            return ((AcmObject) args[acquireAndReleaseObjectLock.acmObjectArgIndex()]).getId();
        }
        else
        {
            throw new RuntimeException("AcmAcquireAndReleaseObjectLock requires objectIdArgIndex or acmObjectArgIndex to be specified!");
        }
    }

    public AcmObjectLockingManager getObjectLockingManager()
    {
        return objectLockingManager;
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
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
