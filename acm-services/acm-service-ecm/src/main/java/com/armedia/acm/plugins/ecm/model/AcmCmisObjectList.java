package com.armedia.acm.plugins.ecm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 3/12/15.
 */
public class AcmCmisObjectList implements Serializable
{
    private static final long serialVersionUID = -1305624697384553192L;

    private String containerObjectType;
    private Long containerObjectId;
    private Long folderId;

    private List<AcmCmisObject> children = new ArrayList<>();
    private int totalChildren;
    private String category;
    private String sortBy;
    private String sortDirection;
    private int startRow;
    private int maxRows;

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

    public Long getFolderId()
    {
        return folderId;
    }

    public void setFolderId(Long folderId)
    {
        this.folderId = folderId;
    }

    public List<AcmCmisObject> getChildren()
    {
        return children;
    }

    public void setChildren(List<AcmCmisObject> children)
    {
        this.children = children;
    }

    public void setTotalChildren(int totalChildren)
    {
        this.totalChildren = totalChildren;
    }

    public int getTotalChildren()
    {
        return totalChildren;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategory()
    {
        return category;
    }

    public void setSortBy(String sortBy)
    {
        this.sortBy = sortBy;
    }

    public String getSortBy()
    {
        return sortBy;
    }

    public void setSortDirection(String sortDirection)
    {
        this.sortDirection = sortDirection;
    }

    public String getSortDirection()
    {
        return sortDirection;
    }

    public void setStartRow(int startRow)
    {
        this.startRow = startRow;
    }

    public int getStartRow()
    {
        return startRow;
    }

    public void setMaxRows(int maxRows)
    {
        this.maxRows = maxRows;
    }

    public int getMaxRows()
    {
        return maxRows;
    }
}
