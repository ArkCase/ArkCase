package com.armedia.acm.audit.dao;

import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.data.AcmAbstractDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 9/4/14.
 */
public class AuditDao extends AcmAbstractDao<AuditEvent>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public int purgeAudits(Date threshold)
    {
        int retval = 0;

        Query update = getEm().createQuery("UPDATE " +
                "AuditEvent audit " +
                "SET " +
                "audit.status = 'DELETE' " +
                "WHERE " +
                "audit.status != 'DELETE' " +
                "AND " +
                "audit.eventDate <= :threshold");

        update.setParameter("threshold", threshold);

        try
        {
            retval = update.executeUpdate();
        } catch (Exception e)
        {
            LOG.error("Cannot purge audits.", e);
        }

        return retval;
    }

    public List<AuditEvent> findAuditsByEventPatternAndObjectId(String objectType, Long objectId)
    {
        String queryText = "SELECT ae " +
                "FROM   AuditEvent ae " +
                "WHERE  ae.objectType = :objectType " +
                "AND    ae.objectId = :objectId " +
                "AND 	ae.status != 'DELETE' " +
                "ORDER BY ae.eventDate";

        Query findAudits = getEm().createQuery(queryText);
        findAudits.setParameter("objectId", objectId);
        findAudits.setParameter("objectType", objectType);
        return findAudits.getResultList();
    }

    public List<AuditEvent> findPagedResults(Long objectId, String objectType, int startRow, int maxRows, List<String> eventTypes, String sort, String direction)
    {

        //lets change order by string because of sql injection
        if (!direction.toUpperCase().equals("ASC"))
        {
            direction = "DESC";
        }

        String sortBy;
        switch (sort)
        {
            case "eventType":
                sortBy = "COALESCE(lu.auditBuisinessName, ae.fullEventType)";
                break;
            case "userId":
                sortBy = "ae.userId";
                break;
            default:
                sortBy = "ae.eventDate";
                break;
        }

        String queryText = "SELECT ae " +
                "FROM   AuditEvent ae LEFT OUTER JOIN AcmAuditLookup lu ON ae.fullEventType=lu.auditEventName " +
                "WHERE  ae.status != 'DELETE' " +
                "AND ((ae.objectType = :objectType AND ae.objectId = :objectId AND ae.parentObjectId = NULL) " +
                "OR (ae.parentObjectType = :objectType AND ae.parentObjectId = :objectId)) " +
                (eventTypes != null ? "AND ae.fullEventType IN :eventTypes " : "") +
                "AND ae.eventResult = 'success' " +
                "ORDER BY " + sortBy + " " + direction;
        Query query = getEm().createQuery(queryText);
        query.setFirstResult(startRow);
        query.setMaxResults(maxRows);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        if (eventTypes != null)
        {
            query.setParameter("eventTypes", eventTypes);
        }

        List<AuditEvent> results = query.getResultList();
        return results;
    }

    public int countAll(Long objectId, String objectType, List<String> eventTypes)
    {
        String queryText = "SELECT COUNT(ae.fullEventType) " +
                "FROM   AuditEvent ae " +
                "WHERE  ae.status != 'DELETE' " +
                "AND ((ae.objectType = :objectType AND ae.objectId = :objectId) " +
                "OR (ae.parentObjectType = :objectType AND ae.parentObjectId = :objectId)) " +
                (eventTypes != null ? "AND ae.fullEventType IN :eventTypes " : "") +
                "AND ae.eventResult = 'success'";

        Query query = getEm().createQuery(queryText);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        if (eventTypes != null)
        {
            query.setParameter("eventTypes", eventTypes);
        }

        Long count = (Long) query.getSingleResult();

        return count.intValue();
    }

    public List<AuditEvent> findPage(int startRow, int maxRows, String sortBy, String sort)
    {
        String queryText = "SELECT ae " +
                "FROM   AuditEvent ae " +
                "WHERE ae.status != 'DELETE' " +
                "ORDER BY ae." + sortBy + " " + sort;
        Query query = getEm().createQuery(queryText);
        query.setFirstResult(startRow);
        query.setMaxResults(maxRows);

        List<AuditEvent> results = query.getResultList();

        return results;

    }

    public int count()
    {
        String queryText = "SELECT COUNT(ae.fullEventType) " +
                "FROM   AuditEvent ae " +
                "WHERE ae.status != 'DELETE'";
        Query query = getEm().createQuery(queryText);

        Long count = (Long) query.getSingleResult();

        return count.intValue();
    }

    public EntityManager getEm()
    {
        return em;
    }

    public void setEm(EntityManager em)
    {
        this.em = em;
    }

    @Override
    protected Class<AuditEvent> getPersistenceClass()
    {
        return AuditEvent.class;
    }
}
