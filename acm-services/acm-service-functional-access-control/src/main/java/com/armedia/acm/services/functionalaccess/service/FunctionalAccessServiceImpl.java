package com.armedia.acm.services.functionalaccess.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.armedia.acm.files.propertymanager.PropertyFileManager;

/**
 * @author riste.tutureski
 *
 */
public class FunctionalAccessServiceImpl implements FunctionalAccessService 
{
	private Logger LOG = LoggerFactory.getLogger(getClass());

	Map<String, String> applicationRolesProperties;
	Map<String, String> applicationRolesToGroupsProperties;
	private PropertyFileManager propertyFileManager;
	private String rolesToGroupsPropertyFileLocation;
	private FunctionalAccessEventPublisher eventPublisher;
	
	@Override
	public List<String> getApplicationRoles() 
	{
		List<String> applicationRoles = new ArrayList<String>();
		
		try
		{
			applicationRoles = Arrays.asList(getApplicationRolesProperties().get("application.roles").split(","));
		}
		catch(Exception e)
		{
			LOG.error("Cannot read application roles from configuratoin.", e);
		}
		
		return applicationRoles;
	}

	@Override
	public Map<String, List<String>> getApplicationRolesToGroups() 
	{		
		Map<String, String> rolesToGroups = getApplicationRolesToGroupsProperties();
		
		return prepareRoleToGroupsForRetrieving(rolesToGroups);
	}

	@Override
	public boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, Authentication auth) 
	{
		boolean success = false;
		try 
		{
			getPropertyFileManager().storeMultiple(prepareRoleToGroupsForSaving(rolesToGroups), getRolesToGroupsPropertyFileLocation(), true);
			success = true;
		} 
		catch (Exception e) 
		{
			LOG.error("Cannot save roles to groups mapping.", e);
			success = false;
		}
		
		if (success)
		{
			getEventPublisher().publishFunctionalAccessUpdateEvent(rolesToGroups, auth);
		}
		
		return success;
	}
	
	private Map<String, String> prepareRoleToGroupsForSaving(Map<String, List<String>> rolesToGroups)
	{
		Map<String, String> retval = new HashMap<String, String>();
		
		if (rolesToGroups != null && rolesToGroups.size() > 0)
		{
			for (Entry<String, List<String>> entry : rolesToGroups.entrySet())
			{
				retval.put(entry.getKey(), StringUtils.join(entry.getValue(), ","));
			}
		}
		
		return retval;
	}
	
	private Map<String, List<String>> prepareRoleToGroupsForRetrieving(Map<String, String> rolesToGroups)
	{
		Map<String, List<String>> retval = new HashMap<String, List<String>>();
		
		if (rolesToGroups != null && rolesToGroups.size() > 0)
		{
			for (Entry<String, String> entry : rolesToGroups.entrySet())
			{
                if(!("".equals(entry.getValue())) && entry.getValue() != null){
                    retval.put(entry.getKey(), Arrays.asList(entry.getValue().split(",")));
                }
			}
		}
		
		return retval;
	}

	public Map<String, String> getApplicationRolesProperties() 
	{
		return applicationRolesProperties;
	}

	public void setApplicationRolesProperties(Map<String, String> applicationRolesProperties) 
	{
		this.applicationRolesProperties = applicationRolesProperties;
	}

	public Map<String, String> getApplicationRolesToGroupsProperties() 
	{
		return applicationRolesToGroupsProperties;
	}

	public void setApplicationRolesToGroupsProperties(Map<String, String> applicationRolesToGroupsProperties) 
	{
		this.applicationRolesToGroupsProperties = applicationRolesToGroupsProperties;
	}

	public PropertyFileManager getPropertyFileManager() 
	{
		return propertyFileManager;
	}

	public void setPropertyFileManager(PropertyFileManager propertyFileManager) 
	{
		this.propertyFileManager = propertyFileManager;
	}

	public String getRolesToGroupsPropertyFileLocation() 
	{
		return rolesToGroupsPropertyFileLocation;
	}

	public void setRolesToGroupsPropertyFileLocation(
			String rolesToGroupsPropertyFileLocation) 
	{
		this.rolesToGroupsPropertyFileLocation = rolesToGroupsPropertyFileLocation;
	}

	public FunctionalAccessEventPublisher getEventPublisher() 
	{
		return eventPublisher;
	}

	public void setEventPublisher(FunctionalAccessEventPublisher eventPublisher) 
	{
		this.eventPublisher = eventPublisher;
	}

}
