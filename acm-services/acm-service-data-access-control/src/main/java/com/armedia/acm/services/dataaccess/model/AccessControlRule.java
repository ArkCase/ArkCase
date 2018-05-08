package com.armedia.acm.services.dataaccess.model;

/*-
 * #%L
 * ACM Service: Data Access Control
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

import java.util.List;
import java.util.Map;

/**
 * Access Control rule entry.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 05.11.2015.
 */
public class AccessControlRule
{
    private String actionName;

    private String objectType;

    private String objectSubType;

    private Map<String, Object> objectProperties;

    private List<String> userRolesAll;

    private List<String> userRolesAny;

    private List<String> userIsParticipantTypeAny;

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

    public String getObjectSubType()
    {
        return objectSubType;
    }

    public void setObjectSubType(String objectSubType)
    {
        this.objectSubType = objectSubType;
    }

    public Map<String, Object> getObjectProperties()
    {
        return objectProperties;
    }

    public void setObjectProperties(Map<String, Object> objectProperties)
    {
        this.objectProperties = objectProperties;
    }

    public List<String> getUserRolesAll()
    {
        return userRolesAll;
    }

    public void setUserRolesAll(List<String> userRolesAll)
    {
        this.userRolesAll = userRolesAll;
    }

    public List<String> getUserRolesAny()
    {
        return userRolesAny;
    }

    public void setUserRolesAny(List<String> userRolesAny)
    {
        this.userRolesAny = userRolesAny;
    }

    public List<String> getUserIsParticipantTypeAny()
    {
        return userIsParticipantTypeAny;
    }

    public void setUserIsParticipantTypeAny(List<String> userIsParticipantTypeAny)
    {
        this.userIsParticipantTypeAny = userIsParticipantTypeAny;
    }
}
