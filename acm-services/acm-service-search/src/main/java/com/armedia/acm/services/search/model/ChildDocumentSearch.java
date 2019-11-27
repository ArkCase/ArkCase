package com.armedia.acm.services.search.model;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
