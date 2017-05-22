package com.armedia.acm.plugins.ecm.model.sync;

import org.json.JSONObject;
import org.springframework.context.ApplicationEvent;

/**
 * Created by dmiller on 5/12/17.
 */
public class EcmEvent extends ApplicationEvent
{

    private String userId;

    private String nodeId;

    private String nodeName;

    private String nodeType;

    private String parentNodeId;

    private String parentNodeType;

    private EcmEventType ecmEventType;

    private long auditId;

    public EcmEvent(JSONObject source)
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

    public EcmEventType getEcmEventType()
    {
        return ecmEventType;
    }

    public void setEcmEventType(EcmEventType ecmEventType)
    {
        this.ecmEventType = ecmEventType;
    }

    public long getAuditId()
    {
        return auditId;
    }

    public void setAuditId(long auditId)
    {
        this.auditId = auditId;
    }

    @Override
    public String toString()
    {
        return "EcmEvent{" +
                "userId='" + userId + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", parentNodeId='" + parentNodeId + '\'' +
                ", parentNodeType='" + parentNodeType + '\'' +
                ", ecmEventType=" + ecmEventType +
                ", auditId=" + auditId +
                '}';
    }
}
