package com.armedia.acm.audit.dao;

import com.armedia.acm.audit.model.AuditEvent;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 9/4/14.
 */
public class AuditDao
{


    @PersistenceContext
    private EntityManager em;

    public List<AuditEvent> findAuditsByEventPatternAndObjectId(
            String objectType,
            Long objectId)
    {
        String queryText =
                "SELECT ae " +
                        "FROM   AuditEvent ae " +
                        "WHERE  ae.objectType = :objectType " +
                        "AND    ae.objectId = :objectId " +
                        "ORDER BY ae.eventDate";

        Query findAudits = getEm().createQuery(queryText);
        findAudits.setParameter("objectId", objectId);
        findAudits.setParameter("objectType", objectType);
        return findAudits.getResultList();
    }

    public List<AuditEvent> findPagedResults(Long objectId, String objectType, int startRow, int maxRows)
    {
        String queryText =
                "SELECT ae " +
                        "FROM   AuditEvent ae " +
                        "WHERE  ae.objectType = :objectType " +
                        "AND    ae.objectId = :objectId " +
                        "ORDER BY ae.eventDate";
        Query query = getEm().createQuery(queryText);
        query.setFirstResult(startRow);
        query.setMaxResults(maxRows);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        List<AuditEvent> results = query.getResultList();
        return results;

    }

    public int countAll(Long objectId, String objectType)
    {
        String queryText =
                "SELECT COUNT(ae.fullEventType) " +
                        "FROM   AuditEvent ae " +
                        "WHERE  ae.objectType = :objectType " +
                        "AND    ae.objectId = :objectId ";
        Query query = getEm().createQuery(queryText);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);


        Long count = (Long) query.getSingleResult();

        return count.intValue();
    }
    
    public List<AuditEvent> findPage(int startRow, int maxRows, String sortBy, String sort)
    {
        String queryText =
                "SELECT ae " +
                        "FROM   AuditEvent ae " +
                        "ORDER BY ae." + sortBy + " " + sort;
        Query query = getEm().createQuery(queryText);
        query.setFirstResult(startRow);
        query.setMaxResults(maxRows);
        
        List<AuditEvent> results = query.getResultList();
        
        return results;

    }
    
    public int count()
    {
        String queryText =
                "SELECT COUNT(ae.fullEventType) " +
                        "FROM   AuditEvent ae ";
        Query query = getEm().createQuery(queryText);


        Long count = (Long) query.getSingleResult();

        return count.intValue();
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
}
