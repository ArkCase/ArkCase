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

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotation used for acquiring object lock before the method execution and releasing the lock after the
 * execution.
 * If the lock cannot be acquired the method isn't executed.
 * If any exception occurs in the method execution the lock is released.
 * <p>
 * Example usage:
 * 
 * // @formatter:off
 *       @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FOLDER", lockType = "DELETE")
 *       public void deleteFolderTreeSafe(Long folderId, Authentication authentication)
 *       
 *       @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
 *       public AcmFolder copyFolder(AcmFolder toBeCopied, AcmFolder dstFolder, Long targetObjectId, String targetObjectType)
 * // @formatter:on
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
@Repeatable(AcmAcquireAndReleaseObjectLock.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface AcmAcquireAndReleaseObjectLock
{
    /**
     * The lock type to acquire and release
     */
    String lockType();

    /**
     * The object type on which to acquire and release the lock
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
     * For complex objects that include child objects, like AcmFolder that has multiple subfolders and files. If this is
     * set to true the child objects are locked with the same lock type, otherwise, they are not locked.
     */
    boolean lockChildObjects() default true;

    /**
     * For complex objects that include child objects, like AcmFolder that has multiple subfolders and files. If this is
     * set to true the child objects are unlocked at the end, otherwise, they are not unlocked.
     */
    boolean unlockChildObjects() default true;

    /**
     * The index of the lockId argument in the method parameters.
     */
    int lockIdArgIndex() default -1;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(METHOD)
    @interface List
    {
        AcmAcquireAndReleaseObjectLock[] value();
    }
}
