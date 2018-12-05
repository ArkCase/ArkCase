package com.armedia.acm.audit.dao;

/*-
 * #%L
 * ACM Service: Audit Library
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

import com.armedia.acm.audit.model.AcmAuditLookup;
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
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        }
        catch (Exception e)
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
                "ORDER BY ae.eventDate, ae.id";

        Query findAudits = getEm().createQuery(queryText);
        findAudits.setParameter("objectId", objectId);
        findAudits.setParameter("objectType", objectType);
        return findAudits.getResultList();
    }

    public List<AuditEvent> findPagedResults(Long objectId, String objectType, int startRow, int maxRows, List<String> eventTypes,
            String sort, String direction)
    {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<AuditEvent> auditEventCriteriaQuery = builder.createQuery(AuditEvent.class);
        Root<AuditEvent> auditEventRoot = auditEventCriteriaQuery.from(AuditEvent.class);

        Subquery<Long> subquery = auditEventCriteriaQuery.subquery(Long.class);
        Root<AuditEvent> rootSubquery = subquery.from(AuditEvent.class);

        subquery.where(
                builder.notEqual(rootSubquery.get("status"), "DELETE"),
                builder.and(
                        builder.and(
                                builder.equal(rootSubquery.get("objectType"), objectType),
                                builder.equal(rootSubquery.get("objectId"), objectId)),
                        builder.or(
                                builder.and(
                                        builder.equal(rootSubquery.get("parentObjectType"), objectType),
                                        builder.equal(rootSubquery.get("cm_parent_object_id"), objectId)))));

        if (eventTypes != null && eventTypes.size() > 0)
        {
            subquery.where(
                    rootSubquery.get("fullEventType").in(eventTypes));
        }
        subquery.where(builder.equal(rootSubquery.get("eventResult"), "success"));

        auditEventCriteriaQuery.select(auditEventRoot);
        subquery.select(rootSubquery.get("id"));

        Join<AuditEvent, AuditEvent> join = rootSubquery.join("id", JoinType.INNER);
        join.on(builder.equal(auditEventRoot.get("id"), rootSubquery.get("id")));

        Root<AcmAuditLookup> acmAuditLookupRoot = auditEventCriteriaQuery.from(AcmAuditLookup.class);

        Join<AuditEvent, AcmAuditLookup> leftJoin = rootSubquery.join("auditLookupId", JoinType.LEFT);
        leftJoin.on(builder.equal(auditEventRoot.get("auditLookup").get("auditLookupId"), acmAuditLookupRoot.get("auditLookupId")));

        if (direction.equals("DESC"))
        {
            switch (sort)
            {
            case "eventType":
                auditEventCriteriaQuery.orderBy(
                        builder.desc(builder.coalesce(acmAuditLookupRoot.get("auditBuisinessName"), auditEventRoot.get("fullEventType"))));
                break;
            case "userId":
                auditEventCriteriaQuery.orderBy(builder.desc(auditEventRoot.get("userId")));
                break;
            default:
                auditEventCriteriaQuery.orderBy(builder.desc(auditEventRoot.get("eventDate")));
                break;
            }
        }
        else
        {
            switch (sort)
            {
            case "eventType":
                auditEventCriteriaQuery.orderBy(
                        builder.asc(builder.coalesce(acmAuditLookupRoot.get("auditBuisinessName"), auditEventRoot.get("fullEventType"))));
                break;
            case "userId":
                auditEventCriteriaQuery.orderBy(builder.asc(auditEventRoot.get("userId")));
                break;
            default:
                auditEventCriteriaQuery.orderBy(builder.desc(auditEventRoot.get("eventDate")));
                break;
            }
        }

        TypedQuery<AuditEvent> query = getEm().createQuery(auditEventCriteriaQuery);
        query.setFirstResult(startRow);
        query.setMaxResults(maxRows);
        query.setParameter(1, objectId);
        query.setParameter(2, objectType);

        List<AuditEvent> results = query.getResultList();
        return results;
    }

    public int countAll(Long objectId, String objectType, List<String> eventTypes)
    {
        String queryText = null;
        if (eventTypes == null)
        {
            queryText = "SELECT COUNT(ae.fullEventType) FROM AuditEvent ae WHERE ae.status != 'DELETE' AND ((ae.objectType = :objectType AND ae.objectId = :objectId) OR (ae.parentObjectType = :objectType AND ae.parentObjectId = :objectId)) AND ae.eventResult = 'success'";
        }
        else
        {
            queryText = "SELECT COUNT(ae.fullEventType) FROM AuditEvent ae WHERE ae.status != 'DELETE' AND ((ae.objectType = :objectType AND ae.objectId = :objectId) OR (ae.parentObjectType = :objectType AND ae.parentObjectId = :objectId)) AND ae.fullEventType IN :eventTypes AND ae.eventResult = 'success'";
        }

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
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<AuditEvent> criteriaQuery = builder.createQuery(AuditEvent.class);
        Root<AuditEvent> auditEvent = criteriaQuery.from(AuditEvent.class);

        criteriaQuery.where(builder.notEqual(auditEvent.<Long> get("status"), "DELETE"));

        if (sort.toUpperCase().equals("ASC"))
        {
            criteriaQuery.orderBy(builder.asc(auditEvent.get(sortBy)));
            criteriaQuery.orderBy(builder.asc(auditEvent.<Long> get("id")));
        }
        else
        {
            criteriaQuery.orderBy(builder.desc(auditEvent.get(sortBy)));
            criteriaQuery.orderBy(builder.desc(auditEvent.<Long> get("id")));
        }

        TypedQuery<AuditEvent> query = getEm().createQuery(criteriaQuery);
        query.setFirstResult(startRow);
        query.setMaxResults(maxRows);

        return query.getResultList();

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

    @Override
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

    public Long getCountAuditEventSince(String eventType, LocalDateTime since, LocalDateTime until)
    {

        String queryText = "SELECT COUNT(ae.fullEventType) " +
                "FROM AuditEvent ae " +
                "WHERE ae.fullEventType = :eventType AND ae.eventDate > :since  AND ae.eventDate < :until";

        Query query = getEm().createQuery(queryText);
        query.setParameter("eventType", eventType);
        query.setParameter("since", Date.from(ZonedDateTime.of(since, ZoneId.systemDefault()).toInstant()));
        query.setParameter("until", Date.from(ZonedDateTime.of(until, ZoneId.systemDefault()).toInstant()));

        Long count = (Long) query.getSingleResult();
        return count;
    }
}
