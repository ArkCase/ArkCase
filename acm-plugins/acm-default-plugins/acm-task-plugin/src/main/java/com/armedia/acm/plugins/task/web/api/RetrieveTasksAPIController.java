package com.armedia.acm.plugins.task.web.api;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.armedia.acm.plugins.task.model.AcmTasksForAPeriod;
import com.armedia.acm.plugins.task.model.TaskByUser;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author sasko.tanaskoski
 */
@Controller
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class RetrieveTasksAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    /**
     * REST controller for retrieving tasks by user for due date.
     *
     * @param authentication
     *            authentication object
     * @param due
     *            due date
     * @return list of TaskByUser objects
     */
    @RequestMapping(value = "/getListByDueDate/{due}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskByUser> getActiveTasksByDueDate(@PathVariable("due") String dueDate, Authentication authentication)
    {

        List<TaskByUser> tasksByUser = new ArrayList<>();
        log.info("Getting grouped tasks by user for due period");

        List<Object> facet = retrieveUserTasksCount(authentication, dueDate);

        Iterator<Object> iterator = facet.iterator();
        while (iterator.hasNext())
        {
            TaskByUser taskByUser = new TaskByUser();
            taskByUser.setUser((String) iterator.next());
            taskByUser.setTaskCount((Integer) iterator.next());
            tasksByUser.add(taskByUser);
        }

        return tasksByUser;

    }

    /**
     * This method will return grouping results in the list from the Solr
     *
     * @param authentication
     *            - authentication object
     * @param dueDate
     *            - dueDate string
     * @return - list of user tasks pairs
     */
    private List<Object> retrieveUserTasksCount(Authentication authentication, String dueDate)
    {
        final List<Object> userTaskCount = new ArrayList<>();

        // Get grouping response from Solr
        String solrGroupingResponse = getSolrResponse(authentication, dueDate);
        if (solrGroupingResponse != null)
        {
            SearchResults searchResults = new SearchResults();
            JSONObject groupingFields = searchResults.getFacetFields(solrGroupingResponse);
            if (groupingFields != null)
            {
                userTaskCount.addAll(searchResults.extractObjectList(groupingFields, SearchConstants.PROPERTY_ASSIGNEE_ID));
            }
        }

        return userTaskCount;
    }

    /**
     * This method returns Solr response for grouping search
     *
     * @param authentication
     *            - authentication object
     * @param dueDate
     *            - due date string
     * @return - Solr response in string representation
     */
    private String getSolrResponse(Authentication authentication, String dueDate)
    {
        String solrQuery = "object_type_s:TASK+AND+status_lcs:ACTIVE";
        String solrResponse = "";
        switch (AcmTasksForAPeriod.getTasksForPeriodByText(dueDate))
        {
        case PAST_DUE:
            solrQuery += "+AND+dueDate_tdt:[* TO NOW]";
            break;
        case DUE_TOMORROW:
            solrQuery += "+AND+dueDate_tdt:[NOW TO NOW%2B1DAY]";
            break;
        case DUE_IN_7_DAYS:
            solrQuery += "+AND+dueDate_tdt:[NOW TO NOW%2B7DAYS]";
            break;
        case DUE_IN_30_DAYS:
            solrQuery += "+AND+dueDate_tdt:[NOW TO NOW%2B30DAYS]";
            break;
        case ALL:
        default:
        }

        solrQuery += "&rows=0&fl=id&wt=json&indent=true&facet=true&facet.mincount=1&facet.field=" + SearchConstants.PROPERTY_ASSIGNEE_ID;

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, solrQuery, 0, 1, "");
        }
        catch (SolrException e)
        {
            log.error("Error while executing Solr query: {}", solrQuery, e);
        }
        return solrResponse;
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
