package com.armedia.acm.services.users.service;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUsersConstants;
import com.armedia.acm.services.users.model.ApplicationRolesToPrivilegesConfig;

import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by nebojsha on 25.01.2017.
 */
public class AcmUserServiceImpl implements AcmUserService
{
    private UserDao userDao;

    private ExecuteSolrQuery executeSolrQuery;

    private AcmUserRoleService userRoleService;

    private ApplicationRolesToPrivilegesConfig rolesToPrivilegesConfig;

    /**
     * queries each user for given id's and returns list of users
     *
     * @param usersIds
     *            given id's
     * @return List of users
     */
    @Override
    public List<AcmUser> getUserListForGivenIds(List<String> usersIds)
    {
        if (usersIds == null)
        {
            return null;
        }
        return usersIds.stream()
                .map(userId -> {
                    AcmUser user = userDao.findByUserId(userId);
                    return user;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * extracts userId from User and returns a list of id's
     *
     * @param users
     *            given users
     * @return List of users id's
     */
    @Override
    public List<String> extractIdsFromUserList(List<AcmUser> users)
    {
        if (users == null)
        {
            return null;
        }

        return users.stream().map(AcmUser::getUserId).collect(Collectors.toList());
    }

    @Override
    public String getUsersByName(Authentication auth, String searchFilter, String sortBy, String sortDirection, int startRow,
            int maxRows)
            throws SolrException
    {

        String query = "object_type_s:USER AND status_lcs:VALID";

        String fq = String.format("fq=name_partial:%s", searchFilter);

        return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                sortBy + " " + sortDirection, fq);
    }

    @Override
    public String getNUsers(Authentication auth, String sortBy, String sortDirection, int startRow, int maxRows)
            throws SolrException
    {

        String query = "object_type_s:USER AND status_lcs:VALID"
                + " AND -name:" + AcmUsersConstants.OCR_SYSTEM_USER
                + " AND -name:" + AcmUsersConstants.TRANSCRIBE_SYSTEM_USER;

        return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                sortBy + " " + sortDirection);
    }

    @Override
    public Set<String> getUserPrivileges(String name)
    {
        Set<String> userPrivileges = new HashSet<>();
        Set<String> userRoles = getUserRoleService().getUserRoles(name);
        Map<String, List<Object>> rolesPrivileges = getRolesToPrivilegesConfig().getRolesToPrivileges();
        for (Map.Entry<String, List<Object>> entry : rolesPrivileges.entrySet())
        {
            if(userRoles.contains(entry.getKey()))
            {
                userPrivileges.addAll(Arrays.asList(entry.getValue().toArray(new String[0])));
            }
        }
        return userPrivileges;
    }


    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public AcmUserRoleService getUserRoleService() {
        return userRoleService;
    }

    public void setUserRoleService(AcmUserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    public ApplicationRolesToPrivilegesConfig getRolesToPrivilegesConfig() {
        return rolesToPrivilegesConfig;
    }

    public void setRolesToPrivilegesConfig(ApplicationRolesToPrivilegesConfig rolesToPrivilegesConfig) {
        this.rolesToPrivilegesConfig = rolesToPrivilegesConfig;
    }
}
