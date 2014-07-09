package com.armedia.acm.services.dataaccess.dao;

import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import com.armedia.acm.services.dataaccess.model.enums.DefaultAccessControlSavePolicy;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by armdev on 7/9/14.
 */
public class AcmAccessControlDefaultDao
{
    @PersistenceContext
    private EntityManager entityManager;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String lookupQueryJpql =
            "SELECT d " +
                    "FROM AcmAccessControlDefault d " +
                    "WHERE d.objectState = :objectState " +
                    "AND d.objectType = :objectType " +
                    "AND d.accessLevel = :accessLevel " +
                    "AND d.accessorType = :accessorType";

    public AcmAccessControlDefault save(AcmAccessControlDefault in, DefaultAccessControlSavePolicy savePolicy) throws PersistenceException
    {
        Preconditions.checkNotNull(in, "Cannot save a null access control default!");

        AcmAccessControlDefault existingDefault = findByFields(in.getObjectType(), in.getObjectState(),
                in.getAccessorType(), in.getAccessLevel());

        if ( log.isDebugEnabled() )
        {
            log.debug("Existing default ACL is null? " + ( existingDefault == null ));
        }

        return savePolicy.persist(in, existingDefault, getEntityManager());


    }

    public AcmAccessControlDefault findByFields(String objectType, String objectState, String accessorType,
                                                 String accessLevel)
    {
        TypedQuery<AcmAccessControlDefault> lookupQuery =
                getEntityManager().createQuery(lookupQueryJpql, AcmAccessControlDefault.class);
        lookupQuery.setParameter("objectType", objectType);
        lookupQuery.setParameter("objectState", objectState);
        lookupQuery.setParameter("accessorType", accessorType);
        lookupQuery.setParameter("accessLevel", accessLevel);

        // unique constraint ensures there is only one max result, but JPA doesn't know that so we have to get a list
        List<AcmAccessControlDefault> existingDefaults = lookupQuery.getResultList();

        return existingDefaults == null || existingDefaults.isEmpty() ?
                null :
                existingDefaults.get(0);
    }


    public EntityManager getEntityManager()
    {
        return entityManager;
    }
}
