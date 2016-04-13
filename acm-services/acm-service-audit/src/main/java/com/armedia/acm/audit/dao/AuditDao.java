package com.armedia.acm.audit.dao;

import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.data.AcmAbstractDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

    public List<AuditEvent> findPagedResults(Long objectId, String objectType, int startRow, int maxRows, List<String> eventTypes, String sortBy, String direction)
    {
        List<AuditEvent> results;

        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        CriteriaQuery<AuditEvent> query = cb.createQuery(AuditEvent.class);
        Root<AuditEvent> ae = query.from(AuditEvent.class);
        query.select(ae);

        Predicate isNotDelete = cb.notEqual(ae.get("status"), "DELETE");
        Predicate isSuccess = cb.equal(ae.get("eventResult"), "success");

        //check by objectType and objectId parameters
        Predicate isObjectType = cb.equal(ae.get("objectType"), objectType);
        Predicate isObjectId = cb.equal(ae.get("objectId"), objectId);
        Predicate isObjectTypeAndId = cb.and(isObjectId, isObjectType);

        //check the same but for parent object id and type
        Predicate isParentObjectType = cb.equal(ae.get("parentObjectType"), objectType);
        Predicate isParentObjectId = cb.equal(ae.get("parentObjectId"), objectId);
        Predicate isParentObjectTypeAndId = cb.and(isParentObjectType, isParentObjectId);

        //result should be same as object OR parent
        Predicate isObjectOrParent = cb.or(isObjectTypeAndId, isParentObjectTypeAndId);

        //filter only events from eventTypes array
        Predicate inEventTypes = ae.get("fullEventType").in(eventTypes);

        //check all predicates with AND
        query.where(cb.and(isNotDelete, isSuccess, isObjectOrParent, inEventTypes));

        if (sortBy.equals("eventType"))
        {
            Join lu = ae.join("auditLookup", JoinType.LEFT);
            Order order;
            if (direction.toUpperCase().equals("ASC"))
                order = cb.asc(lu.get("auditBuisinessName"));
            else
                order = cb.desc(lu.get("auditBuisinessName"));
            query.orderBy(order);
            /*//we will get all results and sort them in memory
            TypedQuery<AuditEvent> dbQuery = getEm().createQuery(query);
            results = dbQuery.getResultList();

            Stream<AuditEvent> streamAudit;

            //sort them in memory
            if (direction.toUpperCase().equals("ASC"))
                streamAudit = results.stream().sorted(comparing(AuditEvent::getEventType));
            else
                streamAudit = results.stream().sorted(comparing(AuditEvent::getEventType).reversed());

            //get by paging parameters
            results = streamAudit.skip(startRow).limit(maxRows).collect(Collectors.toList());*/
        } else
        {
            Order order;
            if (direction.toUpperCase().equals("ASC"))
                order = cb.asc(ae.get(sortBy));
            else
                order = cb.desc(ae.get(sortBy));
            query.orderBy(order);
        }
        TypedQuery<AuditEvent> dbQuery = getEm().createQuery(query);
        dbQuery.setFirstResult(startRow);
        dbQuery.setMaxResults(maxRows);
        results = dbQuery.getResultList();

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
