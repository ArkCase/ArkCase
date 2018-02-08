package com.armedia.acm.services.users.service;

import com.armedia.acm.services.users.model.AcmUser;

import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;

import java.util.List;

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
     * @return USER groups
     */
    String getUsersByName(Authentication auth, String searchFilter, String sortBy, String sortDirection, int startRow, int maxRows)
            throws MuleException;

    /**
     * Retrieve n valid USERS
     *
     * @param auth,
     *            sortBy, sortDirection, startRow, maxRows
     * @return USER groups
     */
    String getNUsers(Authentication auth, String sortBy, String sortDirection, int startRow, int maxRows)
            throws MuleException;
}
