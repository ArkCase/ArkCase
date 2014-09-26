package com.armedia.acm.services.signature.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.signature.model.Signature;
import com.google.common.base.Preconditions;

public class SignatureDao extends AcmAbstractDao<Signature>{
    @PersistenceContext
    private EntityManager entityManager;
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String lookupByObjectIdObjectType =
            "SELECT d " +
                    "FROM Signature d " +
                    "WHERE d.objectId = :objectId AND " +
                    "d.objectType  = :objectType";
    
    public List<Signature> findByObjectIdObjectType(Long objectId, String objectType) {
        Preconditions.checkNotNull(objectId, "Object Id cannot be null");
        Preconditions.checkNotNull(objectType, "Object type cannot be null");

        TypedQuery<Signature> lookupQuery =
                getEntityManager().createQuery(lookupByObjectIdObjectType, Signature.class);
        lookupQuery.setParameter("objectId", objectId);
        lookupQuery.setParameter("objectType", objectType);

        List<Signature> results = lookupQuery.getResultList();
        if (null == results) {
            results = new ArrayList();
        }
        return results;
    }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    @Override
    protected Class<Signature> getPersistenceClass()
    {
        return Signature.class;
    }
}
