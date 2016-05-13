package com.armedia.acm.data;

import java.util.Date;

/**
 * Created by nebojsha on 06.05.2016.
 */
public class AcmObjectEvent
{
    private String objectType;
    private Long objectId;
    private String user;
    private String action;
    private String className;
    private Long parentObjectId;
    private String parentObjectType;
    private Date date;

    public AcmObjectEvent(String action)
    {
        this.action = action;
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

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public Long getParentObjectId()
    {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

    public String getParentObjectType()
    {
        return parentObjectType;
    }

    public void setParentObjectType(String parentObjectType)
    {
        this.parentObjectType = parentObjectType;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Action: ").append(action)
                .append(", Object Id: ").append(objectId)
                .append(", Object Type: ").append(objectType)
                .append(", Parent Object Id: ").append(parentObjectId)
                .append(", Parent Object Type: ").append(parentObjectType)
                .append(", Class Name: ").append(className)
                .append(", User: ").append(user);
        return sb.toString();
    }
}
