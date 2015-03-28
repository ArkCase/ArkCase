package com.armedia.acm.services.functionalaccess.web.api;

import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping(value = { "/api/v1/service/functionalaccess", "/api/latest/service/functionalaccess" } )
public class GetGroupsByPrivilegeAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPluginManager pluginManager;
    private UserDao userDao;
    private FunctionalAccessService functionalAccessService;
    private AcmGroupDao acmGroupDao;
    private MuleClient muleClient;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/groups/{privilege}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String groupsByPrivilege(@PathVariable(value = "privilege") String privilege,
    		@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,			
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
			Authentication auth) throws MuleException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Looking for users for privilege '" + privilege);
        }
        
        List<String> rolesForPrivilege = getPluginManager().getRolesForPrivilege(privilege);
        Map<String, List<String>> rolesToGroups = getFunctionalAccessService().getApplicationRolesToGroups();

        // Creating set to avoid duplicates
        Set<String> groups = new HashSet<>();
        if (rolesForPrivilege != null && rolesToGroups != null)
        {
        	for (String role : rolesForPrivilege)
        	{
        		List<String> groupNames = rolesToGroups.get(role);
        		
        		if (groupNames != null)
        		{
        			// We need first to get unique group names (because groups can be repeated in different roles)
        			groups.addAll(new HashSet<>(groupNames));
        		}
        	}
        }
        
        String retval = getGroupsFromSolr(new ArrayList<>(groups), startRow, maxRows, sort, auth);

        return retval;
    }
    
    private String getGroupsFromSolr(List<String> groupNames, int startRow, int maxRows, String sort, Authentication auth) throws MuleException
    {
		if (log.isInfoEnabled()) 
		{
			log.info("Taking group from Solr with IDs = " + groupNames);
		}
		
		String queryGroupNames = "";
		if (groupNames != null)
		{
			for (int i = 0; i < groupNames.size(); i++)
			{
				if (i == groupNames.size() - 1)
				{
					queryGroupNames += groupNames.get(i);
				}
				else
				{
					queryGroupNames += groupNames.get(i) + " OR ";
				}
			}
		}
		
		String query = "object_id_s:(" + queryGroupNames + ") AND object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";
		
		Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
		headers.put("acmUser", auth);
        
        MuleMessage response = getMuleClient().send("vm://advancedSearchQuery.in", "", headers);

        log.debug("Response type: " + response.getPayload().getClass());

        if ( response.getPayload() instanceof String )
        {
            String responsePayload = (String) response.getPayload();
          
            return responsePayload;
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
    }

    public void setPluginManager(AcmPluginManager pluginManager)
    {
        this.pluginManager = pluginManager;
    }

    public AcmPluginManager getPluginManager()
    {
        return pluginManager;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

	public FunctionalAccessService getFunctionalAccessService() {
		return functionalAccessService;
	}

	public void setFunctionalAccessService(
			FunctionalAccessService functionalAccessService) {
		this.functionalAccessService = functionalAccessService;
	}

	public AcmGroupDao getAcmGroupDao() {
		return acmGroupDao;
	}

	public void setAcmGroupDao(AcmGroupDao acmGroupDao) {
		this.acmGroupDao = acmGroupDao;
	}

	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}
}
