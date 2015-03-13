package com.armedia.acm.plugins.ecm.model;

import java.io.Serializable;

/**
 * Created by armdev on 3/12/15.
 */
public class AcmCmisObject implements Serializable
{
    private static final long serialVersionUID = -8094324419148482666L;

    private String cmisObjectId;
    private String objectType;
    private String name;

    public String getCmisObjectId()
    {
        return cmisObjectId;
    }

    public void setCmisObjectId(String cmisObjectId)
    {
        this.cmisObjectId = cmisObjectId;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
