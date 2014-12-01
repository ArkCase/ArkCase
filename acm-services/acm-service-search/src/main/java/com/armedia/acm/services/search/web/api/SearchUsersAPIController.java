/**
 * 
 */
package com.armedia.acm.services.search.web.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
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

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private MuleClient muleClient;
	
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
		
		MuleMessage response = getUsers(startRow, maxRows, searchKeyword, sortDirection, exclude);
		MuleMessage responseOwner = getOwner(0, 1, exclude);
		
        if ( response.getPayload() instanceof String && responseOwner.getPayload() instanceof String)
        {
        	JSONObject responseObject = new JSONObject((String) response.getPayload());
        	JSONObject responseOwnerObject = new JSONObject((String) responseOwner.getPayload());
        	
        	if (responseObject != null && responseObject.get("response") != null) {
        		JSONObject jsonObject = (JSONObject) responseObject.get("response");
        		jsonObject.put("owner", responseOwnerObject);
        	}
        	
            return responseObject.toString();
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
    }
	
	private MuleMessage getOwner(int startRow, int maxRows, String owner) throws MuleException
	{
		String query = "object_type_s:USER AND object_id_s:" + owner + " AND status_lcs:VALID";
		String sort = "first_name_lcs ASC";
		
		query = query.replaceAll(" ", "+");
		sort = sort.replaceAll(" ", "+");
		
		
		Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        
        MuleMessage response = getMuleClient().send("vm://advancedSearchQuery.in", "", headers);
        
        LOG.debug("Response type: " + response.getPayload().getClass());
        
        return response;
	}
	
	private MuleMessage getUsers(int startRow, int maxRows, String searchKeyword, String sortDirection, String exclude) throws MuleException 
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
		
		
		Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        
        MuleMessage response = getMuleClient().send("vm://advancedSearchQuery.in", "", headers);
        
        LOG.debug("Response type: " + response.getPayload().getClass());
        
        return response;
	}
	
	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}
	
}
