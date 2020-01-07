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

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.services.search.model.ApplicationSearchEvent;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.model.solr.SolrResponse;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchEventPublisher;
import com.armedia.acm.services.search.service.SearchResults;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping({ "/api/v1/plugin/search", "/api/latest/plugin/search" })
public class SearchObjectByTypeAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;
    private SearchEventPublisher searchEventPublisher;
    private AcmPluginManager acmPluginManager;

    @RequestMapping(value = "/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String searchObjectByType(
            @PathVariable("objectType") String objectType,
            @RequestParam(value = "objectSubTypes", required = false, defaultValue = "") List<String> objectSubTypes,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "assignee", required = false, defaultValue = "") String assignee,
            @RequestParam(value = "activeOnly", required = false, defaultValue = "true") boolean activeOnly,
            @RequestParam(value = "filters", required = false, defaultValue = "") String filters,
            @RequestParam(value = "searchQuery", required = false, defaultValue = "") String searchQuery,
            Authentication authentication,
            HttpSession httpSession) throws MuleException
    {
        String[] f = null;
        String sortParams = null;
        String params = "";
        String query = "object_type_s:" + objectType;
        String user = authentication.getName();
        if (!objectSubTypes.isEmpty())
        {
            query += " AND object_sub_type_s:(" + String.join(" OR ", objectSubTypes) + ")";
        }
        if (StringUtils.isBlank(filters))
        {
            if (!StringUtils.isBlank(assignee))
            {
                query += " AND assignee_s:" + assignee;
            }

            if (activeOnly)
            {
                query += " AND -status_s:COMPLETE AND -status_s:DELETE AND -status_s:CLOSED AND -status_s:CLOSE" +
                        " AND -status_lcs:INVALID AND -status_lcs:DELETE AND -status_lcs:INACTIVE";
            }
            log.debug("User [{}] is searching for [{}]", authentication.getName(), query);
        }
        else
        {
            f = filters.split(",");
            List<String> testFilters;
            if (f != null)
            {
                testFilters = findFilters(objectType, f);
                StringBuilder stringBuilder = new StringBuilder();
                int i = 0;
                for (String filter : testFilters)
                {
                    if (filter.contains(SearchConstants.USER))
                        filter = filter.replace(SearchConstants.USER, user);
                    if (i > 0)
                    {
                        stringBuilder.append(SearchConstants.AND_SPLITTER);
                        stringBuilder.append(filter);
                    }
                    else
                    {
                        stringBuilder.append(filter);
                    }
                    i++;
                }
                params = stringBuilder.toString();
            }
        }

        if (!StringUtils.isBlank(sort))
        {
            sortParams = findSortValuesAndCreateSotrString(objectType, sort);
        }

        if (!StringUtils.isBlank(searchQuery))
        {
            String[] searchQueryProperties = findSearchQueryProperties(objectType);
            params = addSearchQueryPropertiesToParams(searchQuery, searchQueryProperties, params);
        }

        // try what the user sent, if no sort properties were found
        sortParams = StringUtils.isBlank(sortParams) ? sort : sortParams;

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query,
                startRow, maxRows, sortParams, params);

        publishSearchEvent(authentication, httpSession, true, results);

        return results;
    }

    @RequestMapping(value = "/advanced/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String searchAdvancedObjectByType(
            @PathVariable("objectType") String objectType,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "assignee", required = false, defaultValue = "") String assignee,
            @RequestParam(value = "activeOnly", required = false, defaultValue = "true") boolean activeOnly,
            Authentication authentication,
            HttpSession httpSession) throws MuleException
    {
        String query = "object_type_s:" + objectType;

        if (!StringUtils.isBlank(assignee))
        {
            query += " AND assignee_s:" + assignee;
        }

        if (activeOnly)
        {
            query += " AND -status_s:COMPLETE AND -status_s:DELETE AND -status_s:CLOSED" +
                    " AND -status_lcs:INVALID AND -status_lcs:DELETE AND -status_lcs:INACTIVE";
        }

        log.debug("Advanced Search: User [{}] is searching for [{}]", authentication.getName(), query);

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                query, startRow, maxRows, sort);

        publishSearchEvent(authentication, httpSession, true, results);

        return results;
    }

    @RequestMapping(value = "/advanced/{objectType}/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> searchAllAdvancedObjectByType(
            @PathVariable("objectType") String objectType,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "assignee", required = false, defaultValue = "") String assignee,
            @RequestParam(value = "activeOnly", required = false, defaultValue = "true") boolean activeOnly,
            Authentication authentication,
            HttpSession httpSession) throws MuleException
    {
        String query = "object_type_s:" + objectType;

        if (!StringUtils.isBlank(assignee))
        {
            query += " AND assignee_s:" + assignee;
        }

        if (activeOnly)
        {
            query += " AND -status_s:COMPLETE AND -status_s:DELETE AND -status_s:CLOSED" +
                    " AND -status_lcs:INVALID AND -status_lcs:DELETE AND -status_lcs:INACTIVE";
        }

        log.debug("Advanced Search: User [{}] is searching for [{}]", authentication.getName(), query);

        String results;
        SearchResults searchResults = new SearchResults();
        JSONArray docs;
        List<String> foundObjects = new ArrayList();
        do
        {
            results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                    query, startRow, maxRows, sort);
            docs = searchResults.getDocuments(results);
            if (docs != null && docs.length() > 0)
            {
                for (int i = 0; i < docs.length(); i++)
                {
                    JSONObject doc = docs.getJSONObject(i);
                    if (doc != null && doc.has(SearchConstants.PROPERTY_OBJECT_TYPE))
                    {
                        if (objectType.equals(doc.getString(SearchConstants.PROPERTY_OBJECT_TYPE)))
                        {
                            foundObjects.add(doc.toString());
                        }
                    }
                }
            }
            startRow += maxRows;
        } while (docs != null && docs.length() > 0);

        publishSearchEvent(authentication, httpSession, true, results);
        return foundObjects;
    }

    protected void publishSearchEvent(Authentication authentication,
            HttpSession httpSession,
            boolean succeeded, String jsonPayload)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SolrResponse solrResponse = gson.fromJson(jsonPayload, SolrResponse.class);

        if (solrResponse.getResponse() != null)
        {
            List<SolrDocument> solrDocs = solrResponse.getResponse().getDocs();
            String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
            Long objectId = null;
            for (SolrDocument doc : solrDocs)
            {
                // in case when objectID is not Long like in USER case
                try
                {
                    objectId = Long.parseLong(doc.getObject_id_s());
                }
                catch (NumberFormatException e)
                {
                    objectId = new Long(-1);
                }
                ApplicationSearchEvent event = new ApplicationSearchEvent(objectId, doc.getObject_type_s(),
                        authentication.getName(), succeeded, ipAddress);
                getSearchEventPublisher().publishSearchEvent(event);
            }
        }
    }

    private String findSortValuesAndCreateSotrString(String objectType, String sort)
    {
        String[] srt = sort.split(",");
        Collection<AcmPlugin> plugins = getAcmPluginManager().getAcmPlugins();
        List<String> suportedObjectTypes = null;
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirstSortArgument = true;
        for (AcmPlugin plugin : plugins)
        {
            if (plugin.getSuportedObjectTypesNames() != null)
            {
                suportedObjectTypes = plugin.getSuportedObjectTypesNames();
            }
            else
            {
                continue;
            }
            for (String objectTypeName : suportedObjectTypes)
            {
                if (objectType.equals(objectTypeName))
                {
                    for (String s : srt)
                    {
                        String jsonString = plugin.getPluginConfig().getSearchTreeSort();
                        JSONArray jsonArray = new JSONArray(jsonString);
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jObj = jsonArray.getJSONObject(i);
                            if (jObj.getString("name").equals(s))
                            {
                                if (isFirstSortArgument)
                                {
                                    stringBuilder.append(jObj.getString("value").trim());
                                    isFirstSortArgument = false;
                                }
                                else
                                {
                                    stringBuilder.append(", ");
                                    stringBuilder.append(jObj.getString("value").trim());
                                }
                            }
                        }
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    private List<String> findFilters(String objectType, String[] filterNames)
    {
        Collection<AcmPlugin> plugins = getAcmPluginManager().getAcmPlugins();
        List<String> suportedObjectTypes = null;
        List<String> filters = new ArrayList<>();
        for (AcmPlugin plugin : plugins)
        {
            if (plugin.getSuportedObjectTypesNames() != null)
            {
                suportedObjectTypes = plugin.getSuportedObjectTypesNames();
            }
            else
            {
                continue;
            }
            for (String objectTypeName : suportedObjectTypes)
            {
                if (objectType.equals(objectTypeName))
                {
                    for (String filterName : filterNames)
                    {
                        String jsonString = plugin.getPluginConfig().getSearchTreeFilter();
                        JSONArray jsonArray = new JSONArray(jsonString);
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jObj = jsonArray.getJSONObject(i);
                            if (jObj.getString("name").equals(filterName))
                            {
                                filters.add(jObj.getString("value").trim());
                            }
                        }
                    }
                }
            }
        }
        return filters;
    }

    private String[] findSearchQueryProperties(String objectType)
    {
        Collection<AcmPlugin> plugins = getAcmPluginManager().getAcmPlugins();
        List<String> suportedObjectTypes = null;
        for (AcmPlugin plugin : plugins)
        {
            if (plugin.getSuportedObjectTypesNames() != null)
            {
                suportedObjectTypes = plugin.getSuportedObjectTypesNames();
            }
            else
            {
                continue;
            }
            for (String objectTypeName : suportedObjectTypes)
            {
                if (objectType.equals(objectTypeName))
                {
                    String searchQueryPropertiesAsString =  plugin.getPluginConfig().getSearchTreeQuery();

                    if (StringUtils.isNotEmpty(searchQueryPropertiesAsString))
                    {
                        return searchQueryPropertiesAsString.split(",");
                    }
                }
            }
        }
        return null;
    }

    private String addSearchQueryPropertiesToParams(String searchQuery, String[] searchQueryProperties, String params)
    {
        if (searchQueryProperties != null && StringUtils.isNotEmpty(searchQuery))
        {
            String[] specialChars = { " ", "_", "-" };
            String fqValue = Arrays.stream(searchQueryProperties).map(it -> {
                String query = it.trim() + ":";
                // If the search keywords contains empty space, search for that particular phrase, otherwise find any
                // objects that contains the characters in the searched properties
                if (StringUtils.containsAny(searchQuery, specialChars))
                {
                    query += "\"" + searchQuery + "\"";
                }
                else
                {
                    query += searchQuery;
                }
                return query;
            }).collect(Collectors.joining(" " + SearchConstants.OPERATOR_OR + " ", "fq=", ""));

            String splitter = "";
            if (StringUtils.isNotEmpty(params))
            {
                splitter = SearchConstants.AND_SPLITTER;
            }
            params += splitter + fqValue;
        }
        return params;
    }

    public AcmPluginManager getAcmPluginManager()
    {
        return acmPluginManager;
    }

    public void setAcmPluginManager(AcmPluginManager acmPluginManager)
    {
        this.acmPluginManager = acmPluginManager;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public SearchEventPublisher getSearchEventPublisher()
    {
        return searchEventPublisher;
    }

    public void setSearchEventPublisher(SearchEventPublisher searchEventPublisher)
    {
        this.searchEventPublisher = searchEventPublisher;
    }

}
