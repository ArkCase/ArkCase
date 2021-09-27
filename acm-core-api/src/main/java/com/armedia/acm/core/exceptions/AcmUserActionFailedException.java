package com.armedia.acm.core.exceptions;

/*-
 * #%L
 * ACM Core API
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
 * Created by armdev on 6/12/14.
 */
public class AcmUserActionFailedException extends Exception
{
    private String actionName;
    private String objectType;
    private Long objectId;

    public AcmUserActionFailedException(String actionName, String objectType, Long objectId, String message, Throwable cause)
    {
        super(message, cause);
        this.actionName = actionName;
        this.objectType = objectType;
        this.objectId = objectId;
    }

    public String getActionName()
    {
        return actionName;
    }

    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    @Override
    public String getMessage()
    {
        String message = "";
        if (getActionName() != null && getObjectId() != null && getObjectType() != null)
        {
            message += "Could not " + getActionName() + " " + getObjectType() + " with ID = " + getObjectId() + ".\n";
        }
        if (super.getMessage() != null)
        {
            message += "Server encountered exception: " + super.getMessage() + "\n";
        }

        message += "Exception type was: '" + getClass().getSimpleName() + "'.";

        return message;
    }

    public String getShortMessage()
    {
        return super.getMessage();
    }
}
