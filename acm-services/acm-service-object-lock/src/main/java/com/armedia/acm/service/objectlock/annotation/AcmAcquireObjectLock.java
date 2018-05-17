package com.armedia.acm.service.objectlock.annotation;

import static java.lang.annotation.ElementType.METHOD;

import com.armedia.acm.service.objectlock.service.ObjectLockingProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotation used for acquiring object lock before the method execution.
 * If the lock cannot be acquired the method isn't executed.
 * The expiry time is optional. If left to 0, the expiry time of the lock will be set by the {@link ObjectLockingProvider} implementation.
 * <p>
 * Example usage:
 * 
 * // @formatter:off
 *       @AcmAcquireObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "WRITE", expiryTime = 600_000)
 *       public String downloadWithLock(Long id) throws MuleException
 *       
 *       @AcmAcquireObjectLock(acmObjectArgIndex = 0, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false)
 *       public String downloadWithLock(AcmFolder folder) throws MuleException
 * // @formatter:on
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface AcmAcquireObjectLock
{
    /**
     * The lock type to acquire
     */
    String lockType();

    /**
     * The object type on which to acquire the lock
     */
    String objectType();

    /**
     * The index of the objectId argument in the method parameters. Leave it at default value (-1), if acmObjectArgIndex
     * should be used.
     */
    int objectIdArgIndex() default -1;

    /**
     * The index of the AcmObject argument in the method parameters. Leave it at default value (-1), if objectIdArgIndex
     * should be used.
     */
    int acmObjectArgIndex() default -1;

    /**
     * The lock expiry time in milliseconds. If left to 0, the expiry time of the lock will be set by the
     * {@link ObjectLockingProvider} implementation.
     */
    long expiryTime() default 0;

    /**
     * For complex objects that include child objects, like AcmFolder that has multiple subfolders and files. If this is
     * set to true the child objects are locked with the same lock type, otherwise, they are not locked.
     */
    boolean lockChildObjects() default true;
}
