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

/**
 * Around aspect targeting annotation: {@link AcmAcquireObjectLock}
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
@Aspect
@Component
public class AcmAcquireObjectLockAspect
{
    private Logger log = LogManager.getLogger(getClass());

    private AcmObjectLockingManager objectLockingManager;

    @Around(value = "@annotation(acmAcquireObjectLocks)")
    public Object aroundAcquireObjectLock(ProceedingJoinPoint pjp, AcmAcquireObjectLock.List acmAcquireObjectLocks)
            throws Throwable, AcmObjectLockException
    {
        Object[] args = pjp.getArgs();
        String userId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY);
        if (userId == null)
        {
            log.warn("Acquiring object lock without userId set in {}.{}", pjp.getSignature().getDeclaringTypeName(),
                    pjp.getSignature().getName());
            userId = "no-user-id";
        }

        try
        {
            Object ret = pjp.proceed();

            for (AcmAcquireObjectLock acmAcquireObjectLock : acmAcquireObjectLocks.value())
            {
                String objectType = acmAcquireObjectLock.objectType();
                Long objectId = getObjectId(pjp, acmAcquireObjectLock, args);
                String lockType = acmAcquireObjectLock.lockType();
                Long expiry = acmAcquireObjectLock.expiryTimeInMilliseconds();
                boolean lockChildObjects = acmAcquireObjectLock.lockChildObjects();
                // if objectId is null, probably we are trying to lock an object before it is persisted for the first
                // time, we cannot lock such objects, but we don't raise an error
                if (objectId != null)
                {
                    objectLockingManager.acquireObjectLock(objectId, objectType, lockType, expiry, lockChildObjects, userId);
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

    @Around(value = "@annotation(acmAcquireObjectLock)")
    public Object aroundAcquireObjectLockSingle(ProceedingJoinPoint pjp, AcmAcquireObjectLock acmAcquireObjectLock)
            throws Throwable, AcmObjectLockException
    {
        AcmAcquireObjectLock.List locks = new AcmAcquireObjectLock.List()
        {

            @Override
            public Class<? extends Annotation> annotationType()
            {
                return AcmAcquireObjectLock.class;
            }

            @Override
            public AcmAcquireObjectLock[] value()
            {
                return new AcmAcquireObjectLock[] {
                        acmAcquireObjectLock
                };
            }
        };
        return aroundAcquireObjectLock(pjp, locks);
    }

    private Long getObjectId(ProceedingJoinPoint pjp, AcmAcquireObjectLock acquireObjectLock, Object[] args)
    {
        if (acquireObjectLock.objectIdArgIndex() != -1)
        {
            if (!(args[acquireObjectLock.objectIdArgIndex()] instanceof Long))
            {
                throw new RuntimeException(String.format("AcmAcquireObjectLock objectIdArgIndex does not resolve to Long argument in {}.{}",
                        pjp.getSignature().getDeclaringTypeName(),
                        pjp.getSignature().getName()));
            }
            return (Long) args[acquireObjectLock.objectIdArgIndex()];
        }
        else if (acquireObjectLock.acmObjectArgIndex() != -1)
        {
            if (!(args[acquireObjectLock.acmObjectArgIndex()] instanceof AcmObject))
            {
                throw new RuntimeException(
                        String.format("AcmAcquireObjectLock acmObjectArgIndex does not resolve to AcmObject argument in {}.{}",
                                pjp.getSignature().getDeclaringTypeName(),
                                pjp.getSignature().getName()));
            }
            return ((AcmObject) args[acquireObjectLock.acmObjectArgIndex()]).getId();
        }
        else
        {
            throw new RuntimeException("AcmAcquireObjectLock requires objectIdArgIndex or acmObjectArgIndex to be specified!");
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
}
