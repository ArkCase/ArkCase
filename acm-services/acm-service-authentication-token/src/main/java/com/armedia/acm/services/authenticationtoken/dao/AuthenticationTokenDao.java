package com.armedia.acm.services.authenticationtoken.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    public List<AuthenticationToken> findAuthenticationTokenByKey(String key)
    {
        Query authenticationToken = getEntityManager().createQuery(
                "SELECT authenticationToken " + "FROM AuthenticationToken authenticationToken "+
                "WHERE authenticationToken.key = :key "
        );

        authenticationToken.setParameter("key", key);
        return authenticationToken.getResultList();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
