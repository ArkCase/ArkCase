package com.armedia.acm.calendar.config.service;

/*-
 * #%L
 * ACM Service: Calendar Service
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
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 21, 2017
 *
 */
public class CalendarConfigurationValidationException extends Exception
{

    private static final long serialVersionUID = 3670682245924498573L;

    private String objectType;

    public CalendarConfigurationValidationException(String message, String objectType)
    {
        super(message);
        this.objectType = objectType;
    }

    public CalendarConfigurationValidationException(Throwable t, String objectType)
    {
        super(t);
        this.objectType = objectType;
    }

    public CalendarConfigurationValidationException(String message, Throwable t, String objectType)
    {
        super(message, t);
        this.objectType = objectType;
    }

    /**
     * @return the objectType
     */
    public String getObjectType()
    {
        return objectType;
    }

}
