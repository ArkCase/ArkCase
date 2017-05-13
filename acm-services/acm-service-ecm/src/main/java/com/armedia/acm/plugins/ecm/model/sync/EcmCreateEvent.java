package com.armedia.acm.plugins.ecm.model.sync;

import org.json.JSONObject;
import org.springframework.context.ApplicationEvent;

/**
 * Created by dmiller on 5/12/17.
 */
public class EcmCreateEvent extends ApplicationEvent
{
    private String userId;

    private String nodeId;

    private String nodeName;

    private String nodeType;

    private String parentNodeId;

    private String parentNodeType;

    public EcmCreateEvent(JSONObject source)
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

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public String getNodeType()
    {
        return nodeType;
    }

    public void setNodeType(String nodeType)
    {
        this.nodeType = nodeType;
    }

    public String getParentNodeId()
    {
        return parentNodeId;
    }

    public void setParentNodeId(String parentNodeId)
    {
        this.parentNodeId = parentNodeId;
    }

    public String getParentNodeType()
    {
        return parentNodeType;
    }

    public void setParentNodeType(String parentNodeType)
    {
        this.parentNodeType = parentNodeType;
    }

    @Override
    public String toString()
    {
        return "EcmCreateEvent{" +
                "userId='" + userId + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", parentNodeId='" + parentNodeId + '\'' +
                ", parentNodeType='" + parentNodeType + '\'' +
                '}';
    }
}
