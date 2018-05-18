package com.armedia.acm.service.objectlock.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotation used for releasing object lock after the method execution.
 * If method throws an exception the lock is not released.
 * <p>
 * Example usage:
 * 
 * // @formatter:off
 *       @AcmReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "WRITE")
 *       public String uploadFileWithUnlock(Long id) throws MuleException
 *       
 *       @AcmAcquireObjectLock(acmObjectArgIndex = 0, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false)
 *       public String uploadFilesWithUnlock(AcmFolder folder, List<EcmFile> files) throws MuleException
 * // @formatter:on
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface AcmReleaseObjectLock
{
    /**
     * The lock type to release
     */
    String lockType();

    /**
     * The object type from which to release the lock
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
     * The index of the lockId argument in the method parameters.
     */
    int lockIdArgIndex() default -1;

    /**
     * For complex objects that include child objects, like AcmFolder that has multiple subfolders and files. If this is
     * set to true the child objects are unlocked at the end, otherwise, they are not unlocked.
     */
    boolean unlockChildObjects() default true;
}
