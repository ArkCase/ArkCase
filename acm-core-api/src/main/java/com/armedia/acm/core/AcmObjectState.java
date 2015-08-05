package com.armedia.acm.core;

import java.util.List;

/**
 * Created by armdev on 7/7/14.
 */
public class AcmObjectState
{
    private String name;
    private String description;
    private List<AcmUserAction> validActions;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<AcmUserAction> getValidActions()
    {
        return validActions;
    }

    public void setValidActions(List<AcmUserAction> validActions)
    {
        this.validActions = validActions;
    }
}
