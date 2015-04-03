package com.armedia.acm.services.functionalaccess.web.api;

import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.services.functionalaccess.model.FunctionalAccessConstants;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping(value = { "/api/v1/service/functionalaccess", "/api/latest/service/functionalaccess" } )
public class GetUsersByPrivilegeAndGroupAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPluginManager pluginManager;
    private UserDao userDao;
    private FunctionalAccessService functionalAccessService;
    private AcmGroupDao acmGroupDao;

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
        Set<AcmUser> users = new HashSet<>();
        if (rolesForPrivilege != null && rolesToGroups != null)
        {
        	for (String role : rolesForPrivilege)
        	{
        		List<String> groupNames = rolesToGroups.get(role);
        		
        		if (groupNames != null)
        		{
        			// Passing null to the "getUsers" will retrieve users for all groups in "groupNames"
        			users.addAll(getUsers(null, groupNames));
        		}
        	}
        }
        
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
        Set<AcmUser> users = new HashSet<>();
        if (rolesForPrivilege != null && rolesToGroups != null)
        {
        	for (String role : rolesForPrivilege)
        	{
        		List<String> groupNames = rolesToGroups.get(role);
        		
        		if (groupNames != null)
        		{
        			// Passing group to the "getUsers" will retrieve users for specific group in "groupNames"
        			users.addAll(getUsers(group, groupNames));
        		}
        	}
        }
        
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
        Set<AcmUser> users = new HashSet<>();
        if (rolesForPrivilege != null && rolesToGroups != null)
        {
        	for (String role : rolesForPrivilege)
        	{
        		List<String> groupNames = rolesToGroups.get(role);
        		
        		if (groupNames != null)
        		{
        			// Passing group to the "getUsers" will retrieve users for specific group in "groupNames"
        			users.addAll(getUsers(group, groupNames));
        		}
        	}
        }
        
        // Get current user and add to the list
        if (currentAssignee != null)
        {
        	AcmUser currentUser = getUserDao().findByUserId(currentAssignee);
        	
        	if (currentUser != null)
        	{
        		users.add(currentUser);
        	}
        }
        
        retval.addAll(users);

        return retval;
    }
    
    private Set<AcmUser> getUsers(String group, List<String> groupNames)
    {
    	Set<AcmUser> retval = new HashSet<>();
    	
    	for (String groupName : groupNames)
		{
			if (groupName.equals(group) || group == null)
			{
				AcmGroup acmGroup = getAcmGroupDao().findByName(groupName);
				
				retval.addAll(acmGroup.getMembers());
			}
		}
    	
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

	public AcmGroupDao getAcmGroupDao() {
		return acmGroupDao;
	}

	public void setAcmGroupDao(AcmGroupDao acmGroupDao) {
		this.acmGroupDao = acmGroupDao;
	}
}
