package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
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

@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class SearchChildrenAPIController
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(value = "/children", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String children(           
            @RequestParam(value = "parentType", required = true) String parentType,
            @RequestParam(value = "parentId", required = true) Long parentId,
            @RequestParam(value = "childType", required = false, defaultValue = "") String childType,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication
    ) throws MuleException
    {
        String query = "parent_object_type_s:" + parentType + " AND parent_object_id_i:"+ parentId;
        
         if (!"".equals(childType))
        {
         query = query + " AND object_type_s:" + childType;
        }   
        
        
        if ( log.isDebugEnabled() )
        {
            log.debug("User '" + authentication.getName() + "' is searching for '" + query + "'");
        }

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query,
                startRow, maxRows, sort);
     
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
