package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.plugins.task.model.AcmTasksForAPeriod;
import com.armedia.acm.plugins.task.model.TaskByUser;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class RetrieveTasksAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

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

    private String getSolrResponse(Authentication authentication, String dueDate)
    {
        String solrQuery = "object_type_s:TASK+AND+status_s:ACTIVE";
        String solrResponse = "";
        switch (AcmTasksForAPeriod.getTasksForPeriodByText(dueDate))
        {
        case PAST_DUE:
            solrQuery += "+AND+due_tdt:[* TO NOW]";
            break;
        case DUE_TOMORROW:
            solrQuery += "+AND+due_tdt:[NOW TO NOW+1DAY]";
            break;
        case DUE_IN_7_DAYS:
            solrQuery += "+AND+due_tdt:[NOW TO NOW+7DAYS]";
            break;
        case DUE_IN_30_DAYS:
            solrQuery += "+AND+due_tdt:[NOW TO NOW+30DAYS]";
            break;
        case ALL:
        default:
        }

        solrQuery += "&rows=0&fl=id&wt=json&indent=true&facet=true&facet.mincount=1&facet.field=" + SearchConstants.PROPERTY_ASSIGNEE_ID;

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, solrQuery, 0, 1, "");
        } catch (MuleException e)
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
