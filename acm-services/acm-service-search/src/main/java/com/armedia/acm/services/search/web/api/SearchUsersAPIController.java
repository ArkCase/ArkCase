/**
 * 
 */
package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class SearchUsersAPIController {

	private transient final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private ExecuteSolrQuery executeSolrQuery;
	
	@RequestMapping(value = "/usersSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String users(
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "searchKeyword", required = false, defaultValue = "") String searchKeyword,
            @RequestParam(value = "exclude", required = false) String exclude,
            Authentication authentication
    ) throws MuleException,UnsupportedEncodingException
    {
		
		String response = getUsers(startRow, maxRows, searchKeyword, sortDirection, exclude, authentication);
        JSONObject responseObject = new JSONObject(response);
        	
        if ( exclude != null && !exclude.trim().isEmpty() && responseObject != null && responseObject.has("response") )
        {
            JSONObject jsonObject = responseObject.getJSONObject("response");

            String responseOwner = getOwner(0, 1, exclude, authentication);
            JSONObject responseOwnerObject = new JSONObject(responseOwner);

            jsonObject.put("owner", responseOwnerObject);
        }

        return responseObject.toString();
    }
	
	private String getOwner(int startRow, int maxRows, String owner, Authentication authentication) throws MuleException,
            UnsupportedEncodingException
	{
        owner = URLEncoder.encode(owner, StandardCharsets.UTF_8.displayName());
		String query = "object_type_s:USER AND object_id_s:" + owner + " AND status_lcs:VALID";
		String sort = "first_name_lcs ASC";
		
        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                query, startRow, maxRows, sort);
        
        return results;
	}
	
	private String getUsers(int startRow, int maxRows, String searchKeyword, String sortDirection, String exclude,
								 Authentication authentication) throws MuleException, UnsupportedEncodingException
	{
		String searchQuery = "object_type_s:USER AND status_lcs:VALID";

		if (StringUtils.isNotEmpty(searchKeyword) && StringUtils.isNotBlank(searchKeyword))
		{
            searchKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8.displayName());
			searchQuery += " AND (first_name_lcs:" + searchKeyword + " OR last_name_lcs:" + searchKeyword + ") ";
		}
		
        if ( StringUtils.isNotEmpty(exclude) && StringUtils.isNotBlank(exclude) )
        {
            exclude = URLEncoder.encode(exclude, StandardCharsets.UTF_8.displayName());
            searchQuery += " AND -object_id_s:" + exclude;
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
