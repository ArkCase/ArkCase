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
 * Around aspect targeting annotation: {@link AcmReleaseObjectLock}
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
@Aspect
@Component
public class AcmReleaseObjectLockAspect
{
    private Logger log = LogManager.getLogger(getClass());

    private AcmObjectLockingManager objectLockingManager;

    @Around(value = "@annotation(acmReleaseObjectLocks)")
    public Object aroundReleaseObjectLock(ProceedingJoinPoint pjp, AcmReleaseObjectLock.List acmReleaseObjectLocks)
            throws Throwable, AcmObjectLockException
    {
        String userId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY);
        if (userId == null)
        {
            log.warn("Releasing object lock without userId set in {}.{}", pjp.getSignature().getDeclaringTypeName(),
                    pjp.getSignature().getName());
            userId = "no-user-id";
        }

        try
        {
            Object ret = pjp.proceed();

            for (AcmReleaseObjectLock acmReleaseObjectLock : acmReleaseObjectLocks.value())
            {

                Object[] args = pjp.getArgs();
                String objectType = acmReleaseObjectLock.objectType();
                Long objectId = getObjectId(pjp, acmReleaseObjectLock, args);
                String lockType = acmReleaseObjectLock.lockType();
                boolean unlockChildObjects = acmReleaseObjectLock.unlockChildObjects();
                Long lockId = acmReleaseObjectLock.lockIdArgIndex() != -1 ? (Long) args[acmReleaseObjectLock.lockIdArgIndex()]
                        : null;
                // if objectId is null, probably we are trying to release an object before it is persisted for the first
                // time, we cannot lock nor release a lock from such objects, but we don't raise an error
                if (objectId != null)
                {
                    objectLockingManager.releaseObjectLock(objectId, objectType, lockType, unlockChildObjects, userId, lockId);
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

    @Around(value = "@annotation(acmReleaseObjectLock)")
    public Object aroundReleaseObjectLockSingle(ProceedingJoinPoint pjp, AcmReleaseObjectLock acmReleaseObjectLock)
            throws Throwable, AcmObjectLockException
    {
        AcmReleaseObjectLock.List locks = new AcmReleaseObjectLock.List()
        {

            @Override
            public Class<? extends Annotation> annotationType()
            {
                return AcmReleaseObjectLock.class;
            }

            @Override
            public AcmReleaseObjectLock[] value()
            {
                return new AcmReleaseObjectLock[] {
                        acmReleaseObjectLock
                };
            }
        };
        return aroundReleaseObjectLock(pjp, locks);
    }

    private Long getObjectId(ProceedingJoinPoint pjp, AcmReleaseObjectLock releaseObjectLock, Object[] args)
    {
        if (releaseObjectLock.objectIdArgIndex() != -1)
        {
            if (!(args[releaseObjectLock.objectIdArgIndex()] instanceof Long))
            {
                throw new RuntimeException(String.format("AcmReleaseObjectLock objectIdArgIndex does not resolve to Long argument in {}.{}",
                        pjp.getSignature().getDeclaringTypeName(),
                        pjp.getSignature().getName()));
            }
            return (Long) args[releaseObjectLock.objectIdArgIndex()];
        }
        else if (releaseObjectLock.acmObjectArgIndex() != -1)
        {
            if (!(args[releaseObjectLock.acmObjectArgIndex()] instanceof AcmObject))
            {
                throw new RuntimeException(
                        String.format("AcmReleaseObjectLock acmObjectArgIndex does not resolve to AcmObject argument in {}.{}",
                                pjp.getSignature().getDeclaringTypeName(),
                                pjp.getSignature().getName()));
            }
            return ((AcmObject) args[releaseObjectLock.acmObjectArgIndex()]).getId();
        }
        else
        {
            throw new RuntimeException("AcmReleaseObjectLock requires objectIdArgIndex or acmObjectArgIndex to be specified!");
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
