package com.armedia.acm.services.users.web.api;

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

@Controller
@RequestMapping(value = {"/api/v1/users", "/api/latest/users"})
public class GetUsersByGroupAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/by-group/{group}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String info(Authentication auth, @PathVariable String group) throws MuleException
    {
        log.debug("Getting users for group {}", group);

        StringBuilder query = new StringBuilder();
        query.append("groups_id_ss").append(":").append(group);
        log.debug("executing query for users in group: {}", query.toString());

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query.toString(), 0, 1000, "");
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
