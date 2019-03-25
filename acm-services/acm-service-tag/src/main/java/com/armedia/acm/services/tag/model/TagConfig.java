package com.armedia.acm.services.tag.model;

/*-
 * #%L
 * ACM Service: Tag
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

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

public class TagConfig
{
    @JsonProperty("tag.plugin.search.name")
    @Value("${tag.plugin.search.name}")
    private String searchName;

    @JsonProperty("tag.plugin.search.filters")
    @Value("${tag.plugin.search.filters}")
    private String searchFilters;

    @JsonProperty("tag.plugin.search.topFacets")
    @Value("${tag.plugin.search.topFacets}")
    private String topFacets;

    @JsonProperty("tag.plugin.associated.by.object.id.and.type")
    @Value("${tag.plugin.associated.by.object.id.and.type}")
    private String tagAssociatedByObjectIdAndTypeQuery;

    @JsonProperty("tag.plugin.tags")
    @Value("${tag.plugin.tags}")
    private String tags;

    public String getSearchName()
    {
        return searchName;
    }

    public void setSearchName(String searchName)
    {
        this.searchName = searchName;
    }

    public String getSearchFilters()
    {
        return searchFilters;
    }

    public void setSearchFilters(String searchFilters)
    {
        this.searchFilters = searchFilters;
    }

    public String getTopFacets()
    {
        return topFacets;
    }

    public void setTopFacets(String topFacets)
    {
        this.topFacets = topFacets;
    }

    public String getTagAssociatedByObjectIdAndTypeQuery()
    {
        return tagAssociatedByObjectIdAndTypeQuery;
    }

    public void setTagAssociatedByObjectIdAndTypeQuery(String tagAssociatedByObjectIdAndTypeQuery)
    {
        this.tagAssociatedByObjectIdAndTypeQuery = tagAssociatedByObjectIdAndTypeQuery;
    }

    public String getTags()
    {
        return tags;
    }

    public void setTags(String tags)
    {
        this.tags = tags;
    }
}
