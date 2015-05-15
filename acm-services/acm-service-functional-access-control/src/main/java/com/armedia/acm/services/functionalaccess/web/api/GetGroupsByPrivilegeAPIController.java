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
        
        String retval = getFunctionalAccessService().getGroupsByPrivilege(rolesForPrivilege, rolesToGroups, startRow, maxRows, sort, auth);

        return retval;
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
}
