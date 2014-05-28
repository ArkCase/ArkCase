package com.armedia.acm.services.users.model;

import java.io.Serializable;


public class AcmUser implements Serializable
{
    private static final long serialVersionUID = 3399640646540732944L;

    private String fullName;
    private String userId;

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }
}
