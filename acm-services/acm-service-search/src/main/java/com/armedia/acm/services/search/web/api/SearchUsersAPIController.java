/**
 * 
 */
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
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/search", "/api/latest/plugin/search" })
public class SearchUsersAPIController
{

    private transient final Logger LOG = LogManager.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(value = "/usersSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String users(
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "searchKeyword", required = false, defaultValue = "") String searchKeyword,
            @RequestParam(value = "exclude", required = false) String exclude,
            @RequestParam(value = "userId", required = false) String userId,
            Authentication authentication) throws SolrException, UnsupportedEncodingException
    {

        String response = getUsers(startRow, maxRows, searchKeyword, sortDirection, exclude, userId, authentication);
        JSONObject responseObject = new JSONObject(response);

        if (exclude != null && !exclude.trim().isEmpty() && responseObject != null && responseObject.has("response"))
        {
            JSONObject jsonObject = responseObject.getJSONObject("response");

            String responseOwner = getOwner(0, 1, exclude, authentication);
            JSONObject responseOwnerObject = new JSONObject(responseOwner);

            jsonObject.put("owner", responseOwnerObject);
        }

        return responseObject.toString();
    }

    private String getOwner(int startRow, int maxRows, String owner, Authentication authentication) throws SolrException,
            UnsupportedEncodingException
    {
        owner = URLEncoder.encode(owner, StandardCharsets.UTF_8.displayName());
        String query = "object_type_s:USER AND object_id_s:" + owner + " AND status_lcs:VALID";
        String sort = "first_name_lcs ASC";

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                query, startRow, maxRows, sort);

        return results;
    }

    private String getUsers(int startRow, int maxRows, String searchKeyword, String sortDirection, String exclude, String userId,
            Authentication authentication) throws UnsupportedEncodingException, SolrException
    {
        String searchQuery = "object_type_s:USER AND status_lcs:VALID";

        if (StringUtils.isNotEmpty(searchKeyword) && StringUtils.isNotBlank(searchKeyword))
        {
            searchKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8.displayName());
            searchQuery += " AND (first_name_lcs:" + searchKeyword + " OR last_name_lcs:" + searchKeyword + ") ";
        }

        if (StringUtils.isNotEmpty(exclude) && StringUtils.isNotBlank(exclude))
        {
            exclude = URLEncoder.encode(exclude, StandardCharsets.UTF_8.displayName());
            searchQuery += " AND -object_id_s:" + exclude;
        }

        if (StringUtils.isNotEmpty(userId) && StringUtils.isNotBlank(userId))
        {
            userId = URLEncoder.encode(userId, StandardCharsets.UTF_8.displayName());
            searchQuery += " AND object_id_s:" + userId;
        }

        String sort = "first_name_lcs " + sortDirection + ", last_name_lcs " + sortDirection;

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                searchQuery, startRow, maxRows, sort);

        return results;
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
