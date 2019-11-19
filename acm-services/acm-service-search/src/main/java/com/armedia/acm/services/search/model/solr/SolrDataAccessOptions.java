package com.armedia.acm.services.search.model.solr;

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

import com.armedia.acm.services.search.model.QueryParameter;

import org.apache.solr.client.solrj.SolrQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SolrDataAccessOptions
{
    private boolean filterSubscriptionEvents;
    private boolean includeDACFilter;
    private boolean includeDenyAccessFilter;
    private boolean enableDocumentACL;
    private List<QueryParameter> queryParameters;
    private SolrQuery query;
    private Map<String, Object> parameters = new HashMap<>();

    public boolean isFilterSubscriptionEvents()
    {
        return filterSubscriptionEvents;
    }

    public void setFilterSubscriptionEvents(boolean filterSubscriptionEvents)
    {
        this.filterSubscriptionEvents = filterSubscriptionEvents;
        parameters.put("filterSubscriptionEvents", filterSubscriptionEvents);
    }

    public Map<String, Object> getParameters()
    {
        return parameters;
    }

    public Optional<?> getParameter(String key)
    {
        return Optional.ofNullable(parameters.get(key));
    }

    public void setParameter(String key, Object value)
    {
        if (!"filterSubscriptionEvents".equalsIgnoreCase(key))
        {
            parameters.put(key, value);
        }
    }

    public boolean isIncludeDACFilter()
    {
        return includeDACFilter;
    }

    public void setIncludeDACFilter(boolean includeDACFilter)
    {
        this.includeDACFilter = includeDACFilter;
    }

    public boolean isIncludeDenyAccessFilter()
    {
        return includeDenyAccessFilter;
    }

    public void setIncludeDenyAccessFilter(boolean includeDenyAccessFilter)
    {
        this.includeDenyAccessFilter = includeDenyAccessFilter;
    }

    public boolean isEnableDocumentACL()
    {
        return enableDocumentACL;
    }

    public void setEnableDocumentACL(boolean enableDocumentACL)
    {
        this.enableDocumentACL = enableDocumentACL;
    }

    public List<QueryParameter> getQueryParameters()
    {
        return queryParameters;
    }

    public void setQueryParameters(List<QueryParameter> queryParameters)
    {
        this.queryParameters = queryParameters;
    }
}
