package com.armedia.acm.services.functionalaccess.service;

import com.armedia.acm.services.users.model.AcmUser;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author riste.tutureski
 */
public interface FunctionalAccessService
{

    public List<String> getApplicationRoles();

    public Map<String, List<String>> getApplicationRolesToGroups();

    public boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, Authentication auth);

    public Set<AcmUser> getUsersByRolesAndGroups(List<String> roles, Map<String, List<String>> rolesToGroups, String group, String currentAssignee);

    public String getGroupsByPrivilege(List<String> roles, Map<String, List<String>> rolesToGroups, int startRow, int maxRows, String sort, Authentication auth) throws MuleException;

}
