package com.armedia.acm.service.objectlock.annotation;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
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
 * Around aspect targeting annotation: {@link AcmAcquireObjectLock}
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
@Aspect
@Component
public class AcmAcquireObjectLockAspect
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmObjectLockingManager objectLockingManager;

    @Around(value = "@annotation(acmAcquireObjectLock)")
    public Object aroundAcquireObjectLock(ProceedingJoinPoint pjp, AcmAcquireObjectLock acquireObjectLock)
            throws Throwable, AcmObjectLockException
    {
        Object[] args = pjp.getArgs();
        String objectType = acquireObjectLock.objectType();
        Long objectId = getObjectId(pjp, acquireObjectLock, args);
        String lockType = acquireObjectLock.lockType();
        Long expiry = acquireObjectLock.expiryTime();
        boolean lockChildObjects = acquireObjectLock.lockChildObjects();
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

            // if objectId is null, probably we are trying to lock an object before it is persisted for the first time,
            // we cannot lock such objects, but we don't raise an error
            if (objectId != null)
            {
                objectLockingManager.acquireObjectLock(objectId, objectType, lockType, expiry, lockChildObjects, userId);
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
