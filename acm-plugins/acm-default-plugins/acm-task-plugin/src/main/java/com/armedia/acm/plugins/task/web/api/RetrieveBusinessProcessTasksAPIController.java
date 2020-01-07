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

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private Logger log = LogManager.getLogger(getClass());

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
            @RequestParam(value = "n", required = false, defaultValue = "5") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "create_date_tdt DESC") String sort)
    {

        log.info("Getting business process tasks");

        String results = getTasks(authentication, start, maxRows, sort);
        SearchResults searchResults = new SearchResults();
        JSONArray tasks = searchResults.getDocuments(results);
        for (int i = 0; i < tasks.length(); i++)
        {
            JSONObject task = tasks.getJSONObject(i);
            String parentResult = getTaskParent(authentication, task.getString("parent_ref_s"));
            JSONObject parent = searchResults.getDocuments(parentResult).getJSONObject(0);
            task.put("parent_name", parent.get("name"));
        }
        JSONObject businessProcessTasks = new JSONObject();
        businessProcessTasks.put("numFound", searchResults.getNumFound(results));
        businessProcessTasks.put("start", start);
        businessProcessTasks.put("data", tasks);
        return businessProcessTasks.toString();
    }

    /**
     * This method returns Solr response for business process tasks.
     *
     * @param authentication
     *            - authentication object
     * @return - Solr response in string representation
     */
    private String getTasks(Authentication authentication, int start, int maxRows, String sort)
    {
        String solrQuery = "object_type_s:TASK+AND+adhocTask_b:FALSE";
        String solrResponse = "";

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, solrQuery, start,
                    maxRows, sort);
        }
        catch (MuleException e)
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
        }
        catch (MuleException e)
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
