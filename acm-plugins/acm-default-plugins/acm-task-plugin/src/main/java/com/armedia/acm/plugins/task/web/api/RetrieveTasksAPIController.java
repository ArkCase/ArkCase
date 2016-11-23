package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.plugins.task.model.AcmTasksForAPeriod;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

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
    public String getActiveTasksByDueDate(@PathVariable("due") String due, Authentication authentication)
    {

        String solrQuery = "object_type_s:TASK AND NOT status_lcs:CLOSED";
        String solrResponse = "";

        switch (AcmTasksForAPeriod.getTasksForPeriodByText(due))
        {
        case PAST_DUE:
            solrQuery += " AND dueDate_tdt:[* TO NOW]";
            break;
        case DUE_TOMORROW:
            solrQuery += " AND dueDate_tdt:[NOW TO NOW+1DAY]";
            break;
        case DUE_IN_7_DAYS:
            solrQuery += " AND dueDate_tdt:[NOW TO NOW+7DAYS]";
            break;
        case DUE_IN_30_DAYS:
            solrQuery += " AND dueDate_tdt:[NOW TO NOW+30DAYS]";
            break;
        case ALL:
        default:
        }

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, solrQuery, 0, 100000,
                    "");
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
