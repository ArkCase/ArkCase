package com.armedia.acm.plugins.ecm.model.sync;

import org.springframework.context.ApplicationEvent;

/**
 * Created by dmiller on 5/15/17.
 */
public class EcmDeleteEvent extends ApplicationEvent
{
    private String userId;

    private String nodeId;

    public EcmDeleteEvent(Object source)
    {
        super(source);
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(String nodeId)
    {
        this.nodeId = nodeId;
    }

    @Override
    public String toString()
    {
        return "EcmDeleteEvent{" +
                "userId='" + userId + '\'' +
                ", nodeId='" + nodeId + '\'' +
                '}';
    }
}
