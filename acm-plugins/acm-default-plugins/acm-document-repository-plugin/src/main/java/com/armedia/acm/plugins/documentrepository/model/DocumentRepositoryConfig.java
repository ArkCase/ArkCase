package com.armedia.acm.plugins.documentrepository.model;

/*-
 * #%L
 * ACM Default Plugin: Document Repository
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

import com.armedia.acm.pluginmanager.service.AcmPluginConfigBean;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

public class DocumentRepositoryConfig implements AcmPluginConfigBean
{
    @JsonProperty("docRepo.search.tree.filter")
    @Value("${docRepo.plugin.search.tree.filter}")
    private String searchTreeFilter;

    @JsonProperty("docRepo.search.tree.sort")
    @Value("${docRepo.plugin.search.tree.sort}")
    private String searchTreeSort;

    @JsonProperty("docRepo.search.tree.searchQuery")
    @Value("${docRepo.plugin.search.tree.searchQuery}")
    private String searchTreeSearchQuery;

    public void setSearchTreeFilter(String searchTreeFilter)
    {
        this.searchTreeFilter = searchTreeFilter;
    }

    public void setSearchTreeSort(String searchTreeSort)
    {
        this.searchTreeSort = searchTreeSort;
    }

    public String getSearchTreeSearchQuery()
    {
        return searchTreeSearchQuery;
    }

    public void setSearchTreeSearchQuery(String searchTreeSearchQuery)
    {
        this.searchTreeSearchQuery = searchTreeSearchQuery;
    }

    @Override
    public String getSearchTreeFilter()
    {
        return searchTreeFilter;
    }

    @Override
    public String getSearchTreeQuery()
    {
        return searchTreeSearchQuery;
    }

    @Override
    public String getSearchTreeSort()
    {
        return searchTreeSort;
    }
}
