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
 * Method annotation used for releasing object lock after the method execution.
 * If method throws an exception the lock is not released.
 * <p>
 * Example usage:
 * 
 * // @formatter:off
 *       @AcmReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "WRITE")
 *       public String uploadFileWithUnlock(Long id)
 *       
 *       @AcmAcquireObjectLock(acmObjectArgIndex = 0, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false)
 *       public String uploadFilesWithUnlock(AcmFolder folder, List<EcmFile> files)
 * // @formatter:on
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
@Repeatable(AcmReleaseObjectLock.List.class)
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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(METHOD)
    @interface List
    {
        AcmReleaseObjectLock[] value();
    }
}
