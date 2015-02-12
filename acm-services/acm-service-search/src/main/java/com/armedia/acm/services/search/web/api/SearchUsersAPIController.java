/**
 * 
 */
package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.SolrSearchService;
import org.apache.commons.lang3.StringUtils;
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
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class SearchUsersAPIController {

	private transient final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private SolrSearchService solrSearchService;
	
	@RequestMapping(value = "/usersSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String users(
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "searchKeyword", required = false, defaultValue = "") String searchKeyword,
            @RequestParam(value = "exclude", required = false, defaultValue = "") String exclude,
            Authentication authentication
    ) throws MuleException
    {
		
		String response = getUsers(startRow, maxRows, searchKeyword, sortDirection, exclude, authentication);
		String responseOwner = getOwner(0, 1, exclude, authentication);
		
        JSONObject responseObject = new JSONObject(response);
        JSONObject responseOwnerObject = new JSONObject(responseOwner);
        	
        if (responseObject != null && responseObject.get("response") != null)
        {
            JSONObject jsonObject = responseObject.getJSONObject("response");
            jsonObject.put("owner", responseOwnerObject);
        }

        return responseObject.toString();
    }
	
	private String getOwner(int startRow, int maxRows, String owner, Authentication authentication) throws MuleException
	{
		String query = "object_type_s:USER AND object_id_s:" + owner + " AND status_lcs:VALID";
		String sort = "first_name_lcs ASC";
		
		query = query.replaceAll(" ", "+");
		sort = sort.replaceAll(" ", "+");

        String results = getSolrSearchService().search(authentication, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort);
        
        return results;
	}
	
	private String getUsers(int startRow, int maxRows, String searchKeyword, String sortDirection, String exclude,
								 Authentication authentication) throws MuleException
	{
		String searchQuery = "";
		if (StringUtils.isNotEmpty(searchKeyword))
		{
			searchQuery = "(first_name_lcs:" + searchKeyword + " OR last_name_lcs:" + searchKeyword + ") AND ";
		}
		
		String query = "object_type_s:USER AND " + searchQuery + "-object_id_s:" + exclude + " AND status_lcs:VALID";
		String sort = "first_name_lcs " + sortDirection + ", last_name_lcs " + sortDirection;
		
		query = query.replaceAll(" ", "+");
		sort = sort.replaceAll(" ", "+");

        String results = getSolrSearchService().search(authentication, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort);
		
		return results;
	}

    public SolrSearchService getSolrSearchService()
    {
        return solrSearchService;
    }

    public void setSolrSearchService(SolrSearchService solrSearchService)
    {
        this.solrSearchService = solrSearchService;
    }
}
