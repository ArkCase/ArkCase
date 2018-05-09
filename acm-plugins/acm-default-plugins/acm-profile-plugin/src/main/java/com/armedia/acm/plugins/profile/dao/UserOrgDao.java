package com.armedia.acm.plugins.profile.dao;

/*-
 * #%L
 * ACM Default Plugin: Profile
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
        UserOrg userOrg = findByUserId(user.getUserId());
        if (userOrg == null)
        {
            throw new AcmObjectNotFoundException(UserOrgConstants.OBJECT_TYPE, null, "Object not found", null);
        }
        return userOrg;
    }

    public UserOrg findByUserId(String userId)
    {
        String jpql = "SELECT uo FROM UserOrg uo where uo.user.userId = :userId";
        TypedQuery<UserOrg> query = getEm().createQuery(jpql, UserOrg.class);
        query.setParameter("userId", userId);
        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException e)
        {
            return null;
        }
    }

    public UserOrg getUserOrgForUserId(String userId) throws AcmObjectNotFoundException
    {
        UserOrg userOrg = findByUserId(userId);
        if (userOrg == null)
        {
            throw new AcmObjectNotFoundException(UserOrgConstants.OBJECT_TYPE, null, "Object not found", null);
        }
        return userOrg;
    }

    @Override
    protected Class<UserOrg> getPersistenceClass()
    {
        return UserOrg.class;
    }

}
