package com.armedia.acm.services.dataaccess.model;

import java.util.List;
import java.util.Map;

/**
 * Access Control List entry.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 05.11.2015.
 */
public class AccessControlEntry
{
    private String actionName;

    private String objectType;

    private String objectSubType;

    private Map<String, String> objectProperties;

    private List<String> userRoles;

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

    public Map<String, String> getObjectProperties()
    {
        return objectProperties;
    }

    public void setObjectProperties(Map<String, String> objectProperties)
    {
        this.objectProperties = objectProperties;
    }

    public List<String> getUserRoles()
    {
        return userRoles;
    }

    public void setUserRoles(List<String> userRoles)
    {
        this.userRoles = userRoles;
    }
}
