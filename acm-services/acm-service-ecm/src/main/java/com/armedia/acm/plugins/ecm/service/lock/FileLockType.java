package com.armedia.acm.plugins.ecm.service.lock;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.core.exceptions.AcmObjectLockException;

/**
 * If new locking types are added in this enumeration, then the {@link FileLockingProvider} must be updated to
 * handle these types.
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
public enum FileLockType
{
    READ, WRITE, DELETE, SHARED_WRITE;

    public static FileLockType fromName(String lockType) throws AcmObjectLockException
    {
        FileLockType objectLockType = null;
        try
        {
            objectLockType = FileLockType.valueOf(lockType);
        }
        catch (Exception e)
        {
            throw new AcmObjectLockException("Unknown lock type: " + lockType);
        }

        return objectLockType;
    }
}
