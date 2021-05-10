package com.armedia.acm.services.users.dao;

/*-
 * #%L
 * ACM Service: Authentication Tokens
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.users.model.UserAccessToken;

public class UserAccessTokenDao extends AcmAbstractDao<UserAccessToken>
{
    private static final Logger logger = LogManager.getLogger(UserAccessTokenDao.class);

    public UserAccessToken getAccessTokenByTenantAndProvider(String tenant, String provider)
    {
        TypedQuery<UserAccessToken> tokenQuery = getEm().createQuery(
                "SELECT token FROM UserAccessToken token WHERE token.tenant = :tenant AND token.provider = :provider",
                UserAccessToken.class);

        tokenQuery.setParameter("tenant", tenant);
        tokenQuery.setParameter("provider", provider);
        try
        {
            return tokenQuery.getSingleResult();
        }
        catch (NoResultException e)
        {
            logger.warn("Token not found for tenant [{}] and provider [{}]", tenant, provider);
            return null;
        }

    }

    @Transactional
    public void deleteAccessTokenForTenantAndProvider(String tenant, String provider)
    {
        TypedQuery<UserAccessToken> deleteTokenQuery = getEm().createQuery(
                "DELETE FROM UserAccessToken token WHERE token.tenant = :tenant AND token.provider = :provider",
                UserAccessToken.class);
        deleteTokenQuery.setParameter("tenant", tenant);
        deleteTokenQuery.setParameter("provider", provider);
        deleteTokenQuery.executeUpdate();
    }

    @Override
    protected Class<UserAccessToken> getPersistenceClass()
    {
        return UserAccessToken.class;
    }
}
