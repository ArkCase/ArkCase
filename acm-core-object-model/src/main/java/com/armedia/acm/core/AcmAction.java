package com.armedia.acm.core;

import java.io.Serializable;

/**
 *
 */
public class AcmAction implements Serializable
{
    private static final long serialVersionUID = 7818426146639850396L;
    private String actionName;
    private String requiredPrivilege;


    public String getActionName()
    {
        return actionName;
    }

    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    public String getRequiredPrivilege()
    {
        return requiredPrivilege;
    }

    public void setRequiredPrivilege(String requiredPrivilege)
    {
        this.requiredPrivilege = requiredPrivilege;
    }
}
