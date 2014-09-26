package com.armedia.acm.services.dataaccess.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlEntry;
import com.armedia.acm.services.dataaccess.model.enums.EntryAccessControlSavePolicy;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class AcmAccessControlEntryDao extends AcmAbstractDao<AcmAccessControlEntry> {
    @PersistenceContext
    private EntityManager entityManager;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String lookupByFields =
            "SELECT d " +
                    "FROM AcmAccessControlEntry d " +
                    "WHERE d.objectId = :objectId AND " +
                    "d.objectType  = :objectType AND " +
                    "d.objectState = :objectState AND " +
                    "d.accessLevel = :accessLevel";

    @Transactional(propagation = Propagation.REQUIRED)
    public AcmAccessControlEntry save(AcmAccessControlEntry in, EntryAccessControlSavePolicy savePolicy) throws PersistenceException {
        Preconditions.checkNotNull(in, "Cannot save a null access control default!");

        AcmAccessControlEntry existingEntry = find(in.getId());

        if (log.isDebugEnabled()) {
            log.debug("Existing entry ACL is null? " + (existingEntry == null));
        }

        return savePolicy.persist(in, existingEntry, getEntityManager());
    }

    public List<AcmAccessControlEntry> findByFields(Long objectId, String objectType, String objectState, String accessLevel) {
        Preconditions.checkNotNull(objectId, "Object Id cannot be null");
        Preconditions.checkNotNull(objectState, "Object type cannot be null");
        Preconditions.checkNotNull(objectState, "Object state cannot be null");
        Preconditions.checkNotNull(accessLevel, "Access level cannot be cannot be null");

        TypedQuery<AcmAccessControlEntry> lookupQuery =
                getEntityManager().createQuery(lookupByFields, AcmAccessControlEntry.class);
        lookupQuery.setParameter("objectId", objectId);
        lookupQuery.setParameter("objectType", objectType);
        lookupQuery.setParameter("objectState", objectState);
        lookupQuery.setParameter("accessLevel", accessLevel);

        List<AcmAccessControlEntry> results = lookupQuery.getResultList();
        if (null == results) {
            results = new ArrayList();
        }
        return results;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }


    @Override
    protected Class<AcmAccessControlEntry> getPersistenceClass() {
        return AcmAccessControlEntry.class;
    }
}
