package com.armedia.acm.services.dataupdate.model;

/*-
 * #%L
 * ACM Service: Data Update Service
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

/**
 * Exception to be thrown in case of failed update operations in
 * any of {@link com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor}
 * implementations and ensure transaction rollback.
 */
public class AcmDataUpdateFailedException extends RuntimeException
{
    public AcmDataUpdateFailedException(String message)
    {
        super(message);
    }

    public AcmDataUpdateFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
