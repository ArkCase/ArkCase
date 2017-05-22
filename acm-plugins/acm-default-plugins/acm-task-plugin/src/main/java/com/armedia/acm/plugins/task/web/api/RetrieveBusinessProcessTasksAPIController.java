package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author sasko.tanaskoski
 */
@Controller
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class RetrieveBusinessProcessTasksAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    /**
     * REST controller for retrieving business process tasks.
     *
     * @param authentication
     *            authentication object
     * @return list of objects
     */
    @RequestMapping(value = "/businessProcessTasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getBusinessProcessTasks(Authentication authentication,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "5") int maxRows)
    {

        log.info("Getting business process tasks");

        String results = getTasks(authentication, start, maxRows);
        SearchResults searchResults = new SearchResults();
        JSONArray tasks = searchResults.getDocuments(results);
        for (int i = 0; i < tasks.length(); i++)
        {
            JSONObject task = tasks.getJSONObject(i);
            String parentResult = getTaskParent(authentication, task.getString("parent_ref_s"));
            JSONObject parent = searchResults.getDocuments(parentResult).getJSONObject(0);
            task.put("parent_name", parent.get("name"));
        }

        return tasks.toString();
    }

    /**
     * This method returns Solr response for business process tasks.
     *
     * @param authentication
     *            - authentication object
     * @return - Solr response in string representation
     */
    private String getTasks(Authentication authentication, int start, int maxRows)
    {
        String solrQuery = "object_type_s:TASK+AND+adhocTask_b:FALSE";
        String solrResponse = "";

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, solrQuery, start,
                    maxRows, "create_date_tdt DESC");
        } catch (MuleException e)
        {
            log.error("Error while executing Solr query: {}", solrQuery, e);
        }
        return solrResponse;
    }

    /**
     * This method returns Solr response for parent of business process task.
     *
     * @param authentication
     *            - authentication object
     * @param taskParentRef
     *            - task parent reference string
     * @return - Solr response in string representation
     */
    private String getTaskParent(Authentication authentication, String taskParentRef)
    {
        String solrQuery = "id:" + taskParentRef;
        String solrResponse = "";

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, solrQuery, 0, 1, "");
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
