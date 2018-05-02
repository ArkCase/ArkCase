package com.armedia.acm.services.functionalaccess.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
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

    public List<String> getApplicationRolesPaged(String sortDirection, Integer startRow, Integer maxRows);

    public List<String> getApplicationRolesByName(String sortDirection, Integer startRow, Integer maxRows, String filterQuery);

    public List<String> getGroupsByRole(Authentication auth, String role, Integer startRow, Integer maxRows,
            String sortDirection,
            Boolean authorized, String filterQuery) throws MuleException;

    public List<String> getGroupsByRolePaged(Authentication auth, String role, Integer startRow, Integer maxRows,
            String sortDirection,
            Boolean authorized) throws MuleException;

    public List<String> getGroupsByRoleByName(Authentication auth, String role, Integer startRow, Integer maxRows,
            String sortDirection,
            Boolean authorized, String filterQuery) throws MuleException;

    public Map<String, List<String>> getApplicationRolesToGroups();

    public boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, Authentication auth);

    public boolean saveGroupsToApplicationRole(List<String> groups, String roleName, Authentication auth) throws AcmEncryptionException;

    public boolean removeGroupsToApplicationRole(List<String> groups, String roleName, Authentication auth);
    public boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, String userId);

    public Set<AcmUser> getUsersByRolesAndGroups(List<String> roles, Map<String, List<String>> rolesToGroups, String group,
            String currentAssignee);

    /**
     * Retrieve groups by privilege
     *
     * @param role,
     *            rolesToGroup, startRow, maxRows, startRow, sort, auth
     * @return users
     */
    public String getGroupsByPrivilege(List<String> roles, Map<String, List<String>> rolesToGroups, int startRow, int maxRows, String sort,
            Authentication auth) throws MuleException;

}
