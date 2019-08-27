package com.armedia.acm.services.functionalaccess.service;

/*-
 * #%L
 * ACM Service: Functional Access Control
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
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

    List<String> getApplicationRoles();

    /**
     * Retrieve Application roles
     *
     * @param sortDirection,
     *            startRow, maxRows
     * @return application roles
     */
    List<String> getApplicationRolesPaged(String sortDirection, Integer startRow, Integer maxRows);

    /**
     * Retrieve Application roles filtered by name & paged & sorted(natural sort)
     *
     * @param sortDirection,
     *            startRow, maxRows, filterQuery
     * @return application roles
     */
    List<String> getApplicationRolesByName(String sortDirection, Integer startRow, Integer maxRows, String filterName);

    List<String> getGroupsByRole(Authentication auth, String role, Integer startRow, Integer maxRows,
            String sortDirection,
            Boolean authorized, String filterName) throws MuleException;

    /**
     * Retrieve groups for an application roles paged & sorted(SOLR sort)
     *
     * @param auth,
     *            role,
     *            startRow, maxRows, sortDirection, authorized
     * @return groups
     */
    List<String> getGroupsByRolePaged(Authentication auth, String role, Integer startRow, Integer maxRows,
            String sortDirection,
            Boolean authorized) throws MuleException;

    /**
     * Retrieve groups for an application roles filtered by name & paged & sorted(SOLR sort)
     *
     * @param auth,
     *            role,
     *            startRow, maxRows, sortDirection, authorized
     * @return groups
     */
    List<String> getGroupsByRoleByName(Authentication auth, String role, Integer startRow, Integer maxRows,
            String sortDirection,
            Boolean authorized, String filterQuery) throws MuleException;

    Map<String, List<String>> getApplicationRolesToGroups();

    boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, Authentication auth);

    /**
     * Retrieve success(boolean) if the saving was successful
     *
     * @description saves list of groups to an application role
     *
     * @param groups,
     *            roleName, auth
     * @return
     */
    boolean saveGroupsToApplicationRole(List<String> groups, String roleName, Authentication auth) throws AcmEncryptionException;

    /**
     * Retrieve success(boolean) if the removing was successful
     *
     * @description saves list of groups to an application role
     *
     * @param groups,
     *            roleName, auth
     * @return
     */
    boolean removeGroupsToApplicationRole(List<String> groups, String roleName, Authentication auth);

    boolean saveApplicationRolesToGroups(Map<String, List<String>> rolesToGroups, String userId);

    Set<AcmUser> getUsersByRolesAndGroups(List<String> roles, Map<String, List<String>> rolesToGroups, String group,
            String currentAssignee);

    /**
     * Retrieve groups by privilege
     *
     * @param role,
     *            rolesToGroup, startRow, maxRows, startRow, sort, auth
     * @return users
     */
    String getGroupsByPrivilege(List<String> roles, Map<String, List<String>> rolesToGroups, int startRow, int maxRows, String sort,
            Authentication auth) throws MuleException;

}
