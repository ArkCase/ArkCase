/**
 * 
 */
package com.armedia.acm.services.users.web.api.group;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.services.users.dao.group.AcmGroupDao;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class GetGroupAPIController {
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmGroupDao groupDao;
	private MuleClient muleClient;
	
	@RequestMapping(value="/group/{groupId}/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGroup(@PathVariable("groupId") String groupId, 
				              @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
				              @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,			
				              @RequestParam(value = "s", required = false, defaultValue = "") String sort,
    						  Authentication auth,
    						  HttpSession httpSession) throws MuleException, Exception
    {
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking group from Solr with ID = " + groupId);
		}
		
		String query = "object_id_s:" + groupId + " AND object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:CLOSED";
		
		if ( LOG.isDebugEnabled() )
        {
			LOG.debug("User '" + auth.getName() + "' is searching for '" + query + "'");
        }
		
		Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        
        MuleMessage response = getMuleClient().send("vm://advancedSearchQuery.in", "", headers);

        LOG.debug("Response type: " + response.getPayload().getClass());

        if ( response.getPayload() instanceof String )
        {
            String responsePayload = (String) response.getPayload();
          
            return responsePayload;
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
		
    }
	
	@RequestMapping(value="/group/{groupId}/get/subgroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getSubGroup(@PathVariable("groupId") String groupId, 
				              @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
				              @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,			
				              @RequestParam(value = "s", required = false, defaultValue = "") String sort,
    						  Authentication auth,
    						  HttpSession httpSession) throws MuleException, Exception
    {
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking subgroups from Solr with ID = " + groupId);
		}
		
        String groupString = getGroup(groupId, 0, 1, sort, auth);
        
        if (groupString != null)
		{
			JSONObject groupJson = new JSONObject(groupString);
			
			if (groupJson != null && groupJson.getJSONObject("response") != null && 
				groupJson.getJSONObject("response").getJSONArray("docs") != null &&
				groupJson.getJSONObject("response").getJSONArray("docs").length() == 1)
			{
				JSONObject doc = groupJson.getJSONObject("response").getJSONArray("docs").getJSONObject(0);
				JSONArray childIds = null;
				try
				{
					childIds = doc.getJSONArray("child_id_ss");
				}
				catch(Exception e)
				{
					throw new IllegalStateException("There are no any subgroups for group with ID = " + groupId);
				}
				if(doc.getJSONArray("child_id_ss") != null)
				{
					return getSubGroups(groupId, childIds, startRow, maxRows, sort, auth);
				}
			}
		}
        
        throw new IllegalStateException("Cannot retrieve subgroups for group with ID = " + groupId);
		
    }
	
	@RequestMapping(value="/group/get/toplevel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTolLevelGroups(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
				              @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,			
				              @RequestParam(value = "s", required = false, defaultValue = "") String sort,
    						  Authentication auth,
    						  HttpSession httpSession) throws MuleException, Exception
    {
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking all top level groups from Solr.");
		}
		
		String query = "object_type_s:GROUP AND -parent_id_s:* AND -status_s:COMPLETE AND -status_s:DELETE AND -status_s:CLOSED";
		
		if ( LOG.isDebugEnabled() )
        {
			LOG.debug("User '" + auth.getName() + "' is searching for '" + query + "'");
        }
		
		Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        
        MuleMessage response = getMuleClient().send("vm://advancedSearchQuery.in", "", headers);

        LOG.debug("Response type: " + response.getPayload().getClass());

        if ( response.getPayload() instanceof String )
        {
            String responsePayload = (String) response.getPayload();
          
            return responsePayload;
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
		
    }
	
	private String getGroup(String groupId, int startRow, int maxRows, String sort, Authentication auth) throws MuleException
	{
		String query = "object_id_s:" + groupId + " AND object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:CLOSED";
		
		if ( LOG.isDebugEnabled() )
        {
			LOG.debug("User '" + auth.getName() + "' is searching for '" + query + "'");
        }
		
		Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        
        MuleMessage response = getMuleClient().send("vm://advancedSearchQuery.in", "", headers);

        LOG.debug("Response type: " + response.getPayload().getClass());

        if ( response.getPayload() instanceof String )
        {
            String responsePayload = (String) response.getPayload();
          
            return responsePayload;
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
	}
	
	private String getSubGroups(String groupId, JSONArray subgroupIds, int startRow, int maxRows, String sort, Authentication auth) throws MuleException
	{
		if (subgroupIds != null && subgroupIds.length() > 0)
		{		
			String query = "object_id_s:(";
			
			for (int i = 0; i < subgroupIds.length(); i++)
			{
				if (i < subgroupIds.length() - 1)
				{
					query += subgroupIds.getString(i) + " OR ";
				}
				else
				{
					query += subgroupIds.getString(i) + ")";
				}
			}
			
			query += " AND object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:CLOSED";
			
			if ( LOG.isDebugEnabled() )
	        {
				LOG.debug("User '" + auth.getName() + "' is searching for '" + query + "'");
	        }
			
			Map<String, Object> headers = new HashMap<>();
	        headers.put("query", query);
	        headers.put("firstRow", startRow);
	        headers.put("maxRows", maxRows);
	        headers.put("sort", sort);
	        
	        MuleMessage response = getMuleClient().send("vm://advancedSearchQuery.in", "", headers);
	
	        LOG.debug("Response type: " + response.getPayload().getClass());
	
	        if ( response.getPayload() instanceof String )
	        {
	            String responsePayload = (String) response.getPayload();
	          
	            return responsePayload;
	        }
	
	        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
		}
		
		throw new IllegalStateException("There are no any subgroups for group with ID = " + groupId);
	}

	public AcmGroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(AcmGroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}
	
}
