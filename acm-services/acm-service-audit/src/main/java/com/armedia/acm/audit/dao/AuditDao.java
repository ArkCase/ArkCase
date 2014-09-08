package com.armedia.acm.audit.dao;

import com.armedia.acm.audit.model.AuditEvent;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by armdev on 9/4/14.
 */
public class AuditDao
{
    @PersistenceContext
    private EntityManager em;

    public List<AuditEvent> findAuditsByEventPatternAndObjectId(
            String eventPattern,
            String objectType,
            Long objectId)
    {
        String queryText =
                "SELECT ae " +
                        "FROM   AuditEvent ae " +
                        "WHERE  ae.fullEventType LIKE :eventType " +
                        "AND    ae.objectType = :objectType " +
                        "AND    ae.objectId = :objectId " +
                        "ORDER BY ae.eventDate";

        Query findAudits = em.createQuery(queryText);
        findAudits.setParameter("eventType", eventPattern + "%");
        findAudits.setParameter("objectId", objectId);
        findAudits.setParameter("objectType", objectType);

        return findAudits.getResultList();

    }
}
