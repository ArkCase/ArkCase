package com.armedia.acm.services.functionalaccess.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

/**
 * @author riste.tutureski
 *
 */
public interface FunctionalAccessService {

	public List<String> getApplicationRoles();
	public Map<String, List<String>> getApplicationRolesToGroups();
	public boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, Authentication auth);
	
}
