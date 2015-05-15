package com.armedia.acm.services.functionalaccess.web.api;

import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.services.functionalaccess.model.FunctionalAccessConstants;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping(value = { "/api/v1/service/functionalaccess", "/api/latest/service/functionalaccess" } )
public class GetUsersByPrivilegeAndGroupAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPluginManager pluginManager;
    private FunctionalAccessService functionalAccessService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users/{privilege}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AcmUser> usersByPrivilege(@PathVariable(value = "privilege") String privilege)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Looking for users for privilege '" + privilege);
        }

        List<AcmUser> retval = new ArrayList<>();
        
        List<String> rolesForPrivilege = getPluginManager().getRolesForPrivilege(privilege);
        Map<String, List<String>> rolesToGroups = getFunctionalAccessService().getApplicationRolesToGroups();

        // Creating set to avoid duplicates. AcmUser has overrided "equals" and "hasCode" methods
        Set<AcmUser> users = getFunctionalAccessService().getUsersByRolesAndGroups(rolesForPrivilege, rolesToGroups, null, null);
        
        retval.addAll(users);

        return retval;
    }
    
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users/{privilege}/{group}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AcmUser> usersByPrivilegeAndGroup(@PathVariable(value = "privilege") String privilege, @PathVariable(value = "group") String group)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Looking for users for privilege '" + privilege + "' and group " + group);
        }

        List<AcmUser> retval = new ArrayList<>();
        
        List<String> rolesForPrivilege = getPluginManager().getRolesForPrivilege(privilege);
        Map<String, List<String>> rolesToGroups = getFunctionalAccessService().getApplicationRolesToGroups();

        // Creating set to avoid duplicates. AcmUser has overrided "equals" and "hasCode" methods
        Set<AcmUser> users = getFunctionalAccessService().getUsersByRolesAndGroups(rolesForPrivilege, rolesToGroups, group, null);
        
        retval.addAll(users);

        return retval;
    }
    
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users/{privilege}/{group}/{currentAssignee}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AcmUser> usersByPrivilegeAndGroupPlusCurrentAssignee(@PathVariable(value = "privilege") String privilege, 
    																			   @PathVariable(value = "group") String group,
    																			   @PathVariable(value = "currentAssignee") String currentAssignee)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Looking for users for privilege '" + privilege + "', group " + group + " plus current assignee '" + currentAssignee + "'");
        }
        
        if (FunctionalAccessConstants.ALL_GROUPS.equals(group))
        {
        	// This will avoid taking users only for specific group
        	group = null;
        }

        List<AcmUser> retval = new ArrayList<>();
        
        List<String> rolesForPrivilege = getPluginManager().getRolesForPrivilege(privilege);
        Map<String, List<String>> rolesToGroups = getFunctionalAccessService().getApplicationRolesToGroups();

        // Creating set to avoid duplicates. AcmUser has overrided "equals" and "hasCode" methods
        Set<AcmUser> users = getFunctionalAccessService().getUsersByRolesAndGroups(rolesForPrivilege, rolesToGroups, group, currentAssignee);
        
        retval.addAll(users);

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

	public FunctionalAccessService getFunctionalAccessService() {
		return functionalAccessService;
	}

	public void setFunctionalAccessService(
			FunctionalAccessService functionalAccessService) {
		this.functionalAccessService = functionalAccessService;
	}
}
