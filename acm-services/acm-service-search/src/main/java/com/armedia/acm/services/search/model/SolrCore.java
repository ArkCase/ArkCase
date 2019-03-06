package com.armedia.acm.services.search.model;

/*-
 * #%L
 * ACM Service: Search
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

/**
 * Created by armdev on 2/12/15.
 */
public enum SolrCore
{
    QUICK_SEARCH("vm://quickSearchQuery.in", "acmQuickSearch"),
    ADVANCED_SEARCH(
            "vm://advancedSearchQuery.in",
            "acmAdvancedSearch"),
    ADVANCED_SUGGESTER_SEARCH(
            "vm://advancedSuggesterQuery.in"),
    QUICK_SUGGESTER_SEARCH("vm://quickSuggesterQuery.in");

    private String muleEndpointUrl;
    private String core;

    private SolrCore(String muleEndpointUrl)
    {
        this.muleEndpointUrl = muleEndpointUrl;
    }

    private SolrCore(String muleEndpointUrl, String core)
    {
        this.muleEndpointUrl = muleEndpointUrl;
        this.core = core;
    }

    public String getMuleEndpointUrl()
    {
        return muleEndpointUrl;
    }

    public String getCore()
    {
        return core;
    }

}
