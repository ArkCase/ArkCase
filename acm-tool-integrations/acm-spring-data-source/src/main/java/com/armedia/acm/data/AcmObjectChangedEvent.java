package com.armedia.acm.data;

/**
 * Created by nebojsha on 06.05.2016.
 */
public class AcmObjectChangedEvent
{
    private String objectType;
    private Long objectId;
    private String user;
    private String action;

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

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Action: ").append(action)
                .append(", Object Id: ").append(objectId)
                .append(", Object Type: ").append(objectType)
                .append(", User: ").append(user);
        return sb.toString();
    }
}
