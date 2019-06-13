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

import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.mule.api.MuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by marjan.stefanoski on 19.12.2014.
 */

@Controller
@RequestMapping({ "/api/v1/plugin/search", "/api/latest/plugin/search" })
public class AdvancedSearchAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    /**
     * Solr Advanced search, HTTP GET variant (limited query length).
     *
     * @param query
     *            Solr query
     * @param sort
     *            Solr sorting parameter
     * @param startRow
     *            start from
     * @param maxRows
     *            number of results
     * @param authentication
     *            authentication token
     * @return Solr response
     * @throws MuleException
     *             on error
     */
    @RequestMapping(value = "/advancedSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String advancedSearchGet(
            @RequestParam(value = "q", required = true) String query,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "fl", required = false) String fields,
            Authentication authentication) throws MuleException
    {
        return advancedSearch(query, sort, startRow, maxRows, fields, authentication);
    }

    /**
     * Solr Advanced search, HTTP POST variant that allows indefinite query length.
     * <p>
     * the request body should look like:
     * <p>
     * <code>
     * {
     *   "q":"catch_all:test*+AND+object_type_S:CASE_FILE",
     *   "start":0,
     *   "n":10,
     * "  s":"create_date_tdt DESC"
     * }
     * </code>
     *
     * @param requestParams
     *            parameter map, as explained above
     * @param authentication
     *            authentication token
     * @return Solr response
     * @throws MuleException
     *             on error
     */
    @RequestMapping(value = "/advancedSearch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String advancedSearchPost(
            @RequestBody Map<String, Object> requestParams,
            Authentication authentication) throws MuleException
    {
        String query = (String) requestParams.get("q");
        String sort = "";
        if (requestParams.get("s") != null)
        {
            sort = (String) requestParams.get("s");
        }
        int startRow = 0;
        if (requestParams.get("start") != null)
        {
            startRow = (int) requestParams.get("start");
        }
        int maxRows = 10;
        if (requestParams.get("n") != null)
        {
            maxRows = (int) requestParams.get("n");
        }
        return advancedSearch(query, sort, startRow, maxRows, null, authentication);
    }

    /**
     * Trigger Solr advanced search.
     *
     * @param query
     *            Solr query
     * @param sort
     *            Solr sorting parameter
     * @param startRow
     *            start from
     * @param maxRows
     *            number of results
     * @param authentication
     *            authentication token
     * @return Solr response
     * @throws MuleException
     *             on error
     */
    private String advancedSearch(String query, String sort, int startRow, int maxRows, String fields, Authentication authentication)
            throws MuleException
    {
        log.debug("User [{}] is searching for [{}]", authentication.getName(), query);

        // Solr wants the '+' sign in the "facet.range.gap" part of the query to be encoded
        // It is because the '+' shouldn't be interpreted/used as a space but as '+' on solr side and that's why need to
        // be sent as %2B.
        //
        //
        // Encoding the entire content of the query does not work and it is possible to brake other queries also. That's
        // why we are replacing only the '+' sign in the "facet.range.gap=+" part of the query with
        // "facet.range.gap=%2B".
        //
        // Example UI -> ArkCase (advancedSearch) request that is using facet.range in the query:
        //
        // ..advancedSearch?q=object_type_s:COMPLAINT+AND+creator_lcs:ann-acm%26facet.range=create_date_tdt%26facet.range.start=NOW-6MONTHS%26facet.range.end=NOW%26facet.range.gap=%2B1MONTH
        //

        if (query.contains("facet.range.gap=+"))
        {
            query = query.replace("facet.range.gap=+", "facet.range.gap=%2B");
        }

        return getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort,
                true, "", true, false, SearchConstants.DEFAULT_FIELD, fields);
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
