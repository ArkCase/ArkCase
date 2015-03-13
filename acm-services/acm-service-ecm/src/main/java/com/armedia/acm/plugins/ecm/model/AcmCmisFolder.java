package com.armedia.acm.plugins.ecm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 3/12/15.
 */
public class AcmCmisFolder implements Serializable
{
    private static final long serialVersionUID = -1305624697384553192L;

    private String containerObjectType;
    private Long containerObjectId;
    private String cmisFolderId;
    private List<AcmCmisObject> children = new ArrayList<>();

    public String getContainerObjectType()
    {
        return containerObjectType;
    }

    public void setContainerObjectType(String containerObjectType)
    {
        this.containerObjectType = containerObjectType;
    }

    public Long getContainerObjectId()
    {
        return containerObjectId;
    }

    public void setContainerObjectId(Long containerObjectId)
    {
        this.containerObjectId = containerObjectId;
    }

    public String getCmisFolderId()
    {
        return cmisFolderId;
    }

    public void setCmisFolderId(String cmisFolderId)
    {
        this.cmisFolderId = cmisFolderId;
    }

    public List<AcmCmisObject> getChildren()
    {
        return children;
    }

    public void setChildren(List<AcmCmisObject> children)
    {
        this.children = children;
    }
}
