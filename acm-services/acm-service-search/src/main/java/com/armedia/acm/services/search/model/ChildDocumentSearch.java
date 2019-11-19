package com.armedia.acm.services.search.model;

import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * @author aleksandar.bujaroski
 */
public class ChildDocumentSearch
{
    private String parentType;

    private Long parentId;

    private List<String> childTypes;

    private String sort;

    private Integer startRow;

    private Integer maxRows;

    private Authentication authentication;

    public String getParentType()
    {
        return parentType;
    }

    public void setParentType(String parentType)
    {
        this.parentType = parentType;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public List<String> getChildTypes()
    {
        return childTypes;
    }

    public void setChildTypes(List<String> childTypes)
    {
        this.childTypes = childTypes;
    }

    public String getSort()
    {
        return sort;
    }

    public void setSort(String sort)
    {
        this.sort = sort == null ? "" : sort;
    }

    public Integer getStartRow()
    {
        return startRow;
    }

    public void setStartRow(Integer startRow)
    {
        this.startRow = startRow == null ? 0 : startRow;
    }

    public Integer getMaxRows()
    {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows)
    {
        this.maxRows = maxRows == null ? 0 : maxRows;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }
}
