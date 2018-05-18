package com.armedia.acm.service.objectlock.annotation;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.web.api.MDCConstants;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Around aspect targeting annotation: {@link AcmAcquireAndReleaseObjectLock}
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
@Aspect
@Component
public class AcmAcquireAndReleaseObjectLockAspect
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmObjectLockingManager objectLockingManager;
    private AcmObjectLockService objectLockService;

    @Around(value = "@annotation(acmAcquireAndReleaseObjectLock)")
    public Object aroundAcquireObjectLock(ProceedingJoinPoint pjp, AcmAcquireAndReleaseObjectLock acmAcquireAndReleaseObjectLock)
            throws Throwable, AcmObjectLockException
    {
        Object[] args = pjp.getArgs();
        String objectType = acmAcquireAndReleaseObjectLock.objectType();
        Long objectId = getObjectId(pjp, acmAcquireAndReleaseObjectLock, args);
        String lockType = acmAcquireAndReleaseObjectLock.lockType();
        boolean lockChildObjects = acmAcquireAndReleaseObjectLock.lockChildObjects();
        boolean unlockChildObjects = acmAcquireAndReleaseObjectLock.unlockChildObjects();
        Long lockId = acmAcquireAndReleaseObjectLock.lockIdArgIndex() != -1 ? (Long) args[acmAcquireAndReleaseObjectLock.lockIdArgIndex()]
                : null;

        String userId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY);
        if (userId == null)
        {
            log.warn("Acquiring and Releasing object lock without userId set in {}.{}", pjp.getSignature().getDeclaringTypeName(),
                    pjp.getSignature().getName());
            userId = "no-user-id";
        }

        try
        {
            AcmObjectLock objectLock = null;

            // if objectId is null, probably we are trying to lock an object before it is persisted for the first time,
            // we cannot lock such objects, but we don't raise an error
            if (objectId != null)
            {
                // acquire lock only if the user doesn't have the lock already
                objectLock = objectLockService.findLock(objectId, objectType);
                if (objectLock == null
                        || (objectLock != null && objectLock.getLockType().equals(lockType) && !objectLock.getCreator().equals(userId)))
                {
                    objectLockingManager.acquireObjectLock(objectId, objectType, lockType, null, lockChildObjects, userId);
                }
            }
            Object ret = null;
            try
            {
                ret = pjp.proceed();
            }
            finally
            {
                if (objectId != null)
                {
                    // release the lock only if it was acquired previously
                    if (objectLock == null
                            || (objectLock != null && objectLock.getLockType().equals(lockType) && !objectLock.getCreator().equals(userId)))
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
