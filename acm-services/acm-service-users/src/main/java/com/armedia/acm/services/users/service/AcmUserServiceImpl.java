package com.armedia.acm.services.users.service;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nebojsha on 25.01.2017.
 */
public class AcmUserServiceImpl implements AcmUserService
{
    private UserDao userDao;

    private ExecuteSolrQuery executeSolrQuery;

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
                .filter(user -> user != null)
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
    public String test(Authentication auth, String searchFilter, String sortBy, String sortDirection, int startRow, int maxRows)
            throws MuleException
    {

        String query = "object_type_s:USER AND status_lcs:VALID";

        String fq = String.format("fq=name_partial:%s", searchFilter);

        return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                sortBy + " " + sortDirection, fq);
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

}
