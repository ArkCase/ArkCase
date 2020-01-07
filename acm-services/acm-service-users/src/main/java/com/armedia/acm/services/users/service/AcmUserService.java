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

import com.armedia.acm.services.users.model.AcmUser;

import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

/**
 * Created by nebojsha on 25.01.2017.
 */
public interface AcmUserService
{
    /**
     * queries each user for given id's and returns list of users
     * 
     * @param usersIds
     *            given id's
     * @return List of users
     */
    List<AcmUser> getUserListForGivenIds(List<String> usersIds);

    /**
     * extracts userId from User and returns a list of id's
     * 
     * @param users
     *            given users
     * @return List of users id's
     */
    List<String> extractIdsFromUserList(List<AcmUser> users);

    /**
     * Retrieve filtered valid USERS by searchFilter
     *
     * @param auth,
     *            searchFilter, sortBy, sortDirection, startRow, maxRows
     * @return users
     */
    String getUsersByName(Authentication auth, String searchFilter, String sortBy, String sortDirection, int startRow, int maxRows)
            throws MuleException;

    /**
     * Retrieve n valid users
     *
     * @param auth,
     *            sortBy, sortDirection, startRow, maxRows
     * @return users
     */
    String getNUsers(Authentication auth, String sortBy, String sortDirection, int startRow, int maxRows)
            throws MuleException;

    /**
     * Retrieve list of user privileges
     *
     * @param name
     *
     * @return userPrivileges
     */
    Set<String> getUserPrivileges(String name);
}
