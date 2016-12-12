package com.armedia.acm.services.dataaccess.model;

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
