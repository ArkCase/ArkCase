package com.armedia.acm.services.users.service;

import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nebojsha on 25.01.2017.
 */
public class AcmUserServiceImpl implements AcmUserService
{
    private UserDao userDao;

    /**
     * queries each user for given id's and returns list of users
     *
     * @param usersIds given id's
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
                .map(userId ->
                {
                    AcmUser user = userDao.findByUserId(userId);
                    return user;
                })
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }

    /**
     * extracts userId from User and returns a list of id's
     *
     * @param users given users
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

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
