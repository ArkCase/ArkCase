package com.armedia.acm.services.authenticationtoken.dao;

/*-
 * #%L
 * ACM Service: Authentication Tokens
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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.List;

/**
 * Created by manoj.dhungana on 7/30/15.
 */
public class AuthenticationTokenDao extends AcmAbstractDao<AuthenticationToken>
{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Class<AuthenticationToken> getPersistenceClass()
    {
        return AuthenticationToken.class;
    }

    public AuthenticationToken findAuthenticationTokenByKey(String key)
    {
        TypedQuery<AuthenticationToken> authenticationToken = getEntityManager().createQuery(
                "SELECT authenticationToken " + "FROM AuthenticationToken authenticationToken " +
                        "WHERE authenticationToken.key = :key ",
                AuthenticationToken.class);

        authenticationToken.setParameter("key", key);
        return authenticationToken.getSingleResult();
    }

    public List<AuthenticationToken> findAuthenticationTokenByTokenFileId(Long fileId)
    {
        TypedQuery<AuthenticationToken> authenticationToken = getEntityManager().createQuery(
                "SELECT authenticationToken " + "FROM AuthenticationToken authenticationToken " +
                        "WHERE authenticationToken.fileId = :fileId ",
                AuthenticationToken.class);

        authenticationToken.setParameter("fileId", fileId);
        return authenticationToken.getResultList();
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
}
