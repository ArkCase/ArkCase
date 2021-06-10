package com.armedia.acm.services.search.web.api;

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

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.FacetedSearchService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by marst on 10/10/16.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/search", "/api/latest/plugin/search" })
public class SuggesterAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    private FacetedSearchService facetedSearchService;

    @RequestMapping(value = "/suggest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String suggest(
            @RequestParam(value = "q") String query,
            @RequestParam(value = "core", defaultValue = "ADVANCED") String core,
            @RequestParam(value = "filter", required = false) String[] filter,
            Authentication authentication) throws SolrException
    {
        String filterQueries = "";
        if (filter != null)
        {
            filterQueries = Arrays.asList(filter).stream().map(f -> getFacetedSearchService().buildSolrQuery(f))
                    .collect(Collectors.joining(""));
        }
        filterQueries += filterQueries.trim().length() > 0 ? "&fq=hidden_b:false" : "fq=hidden_b:false";

        switch (core)
        {
        case SearchConstants.CORE_ADVANCED:
            return getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SUGGESTER_SEARCH, query,
                    0, 10, "",
                    filterQueries);
        default:
            return getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SUGGESTER_SEARCH, query, 0,
                    10, "",
                    filterQueries);
        }
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public FacetedSearchService getFacetedSearchService()
    {
        return facetedSearchService;
    }

    public void setFacetedSearchService(FacetedSearchService facetedSearchService)
    {
        this.facetedSearchService = facetedSearchService;
    }
}
