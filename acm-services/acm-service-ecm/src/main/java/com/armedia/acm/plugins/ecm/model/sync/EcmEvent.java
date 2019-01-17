package com.armedia.acm.plugins.ecm.model.sync;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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

    private String sourceParentNodeId;

    private String sourceParentNodeType;

    private String targetParentNodeId;

    private String targetParentNodeType;

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

    public String getSourceParentNodeId()
    {
        return sourceParentNodeId;
    }

    public void setSourceParentNodeId(String sourceParentNodeId)
    {
        this.sourceParentNodeId = sourceParentNodeId;
    }

    public String getSourceParentNodeType()
    {
        return sourceParentNodeType;
    }

    public void setSourceParentNodeType(String sourceParentNodeType)
    {
        this.sourceParentNodeType = sourceParentNodeType;
    }

    public String getTargetParentNodeId()
    {
        return targetParentNodeId;
    }

    public void setTargetParentNodeId(String targetParentNodeId)
    {
        this.targetParentNodeId = targetParentNodeId;
    }

    public String getTargetParentNodeType()
    {
        return targetParentNodeType;
    }

    public void setTargetParentNodeType(String targetParentNodeType)
    {
        this.targetParentNodeType = targetParentNodeType;
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
                ", sourceParentNodeId='" + sourceParentNodeId + '\'' +
                ", sourceParentNodeType='" + sourceParentNodeType + '\'' +
                ", targetParentNodeId='" + targetParentNodeId + '\'' +
                ", targetParentNodeType='" + targetParentNodeType + '\'' +
                ", ecmEventType=" + ecmEventType +
                ", auditId=" + auditId +
                '}';
    }
}
