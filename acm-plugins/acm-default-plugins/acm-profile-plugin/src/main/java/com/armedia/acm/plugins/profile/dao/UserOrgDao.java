package com.armedia.acm.plugins.profile.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.plugins.profile.model.UserOrgConstants;
import com.armedia.acm.services.users.model.AcmUser;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class UserOrgDao extends AcmAbstractDao<UserOrg>
{

    public UserOrg getUserOrgForUser(AcmUser user) throws AcmObjectNotFoundException
    {
        UserOrg userOrg = getUserOrgForUserId(user.getUserId());
        if (userOrg == null)
        {
            throw new AcmObjectNotFoundException(UserOrgConstants.OBJECT_TYPE, null, "Object not found", null);
        }
        return userOrg;
    }

    public UserOrg getUserOrgForUserId(String userId)
    {
        String jpql = "SELECT uo FROM UserOrg uo where uo.user.userId = :userId";
        TypedQuery<UserOrg> query = getEm().createQuery(jpql, UserOrg.class);
        query.setParameter("userId", userId);
        try
        {
            return query.getSingleResult();
        } catch (NoResultException e)
        {
            return null;
        }
    }

    @Override
    protected Class<UserOrg> getPersistenceClass()
    {
        return UserOrg.class;
    }

}
