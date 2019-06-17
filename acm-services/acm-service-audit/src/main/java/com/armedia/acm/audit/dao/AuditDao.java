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

import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.data.AcmAbstractDao;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
    private final Logger LOG = LogManager.getLogger(getClass());

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

        // lets change order by string because of sql injection
        if (!direction.toUpperCase().equals("ASC"))
        {
            direction = "DESC";
        }

        /*
         * we need to create native query because of nesting queries.
         * Because of MySQL "late row lookup" problem for slow performance when using order by and limit
         * we need to use this trick to increase performance when there are lot of rows.
         */
        String eventTypeClause = "";
        if ( eventTypes != null && !eventTypes.isEmpty() )
        {
            StringBuilder builder = new StringBuilder(" AND ae.cm_audit_activity IN ( ");
            int paramIndex = 3;
            boolean first = true;
            for ( String eventType : eventTypes )
            {
                if ( first )
                {
                    first = false;
                }
                else
                {
                    builder.append(", ");
                }
                builder.append("?").append(paramIndex);
                paramIndex++;
            }
            builder.append(") ");
            eventTypeClause = builder.toString();
        }
        String queryText = "SELECT al.* " +
            "FROM (SELECT ae.cm_audit_id AS id" +
            " FROM acm_audit_log ae" +
            " WHERE ae.cm_audit_status != 'DELETE'" +
            " AND ((ae.cm_object_type = ?2 AND ae.cm_object_id = ?1) OR" +
            " (ae.cm_parent_object_type = ?2 AND ae.cm_parent_object_id = ?1))" +
            eventTypeClause + 
            "      AND ae.cm_audit_activity_result = 'success'" +
            "  ) tmp" +
            " JOIN acm_audit_log al" +
            "    ON al.cm_audit_id = tmp.id" +
            "  LEFT OUTER JOIN acm_audit_event_type_lu lu ON al.cm_audit_activity = lu.cm_key";

        switch (sort)
        {
        case "eventType":
            queryText += " ORDER BY COALESCE(lu.cm_value, al.cm_audit_activity) ";
            break;
        case "userId":
            queryText += " ORDER BY al.cm_audit_user ";
            break;
        default:
            queryText += " ORDER BY al.cm_audit_datetime ";
            break;
        }

        if ( "DESC".equals(direction) )
        {
            queryText += " DESC, al.cm_audit_id DESC";
        }
        else
        {
            queryText += " ASC, al.cm_audit_id ASC";
        }

        Query query = getEm().createNativeQuery(queryText, AuditEvent.class);
        query.setFirstResult(startRow);
        query.setMaxResults(maxRows);
        query.setParameter(1, objectId);
        query.setParameter(2, objectType);
        if ( eventTypes != null && !eventTypes.isEmpty() )
        {
            int paramIndex = 3;
            for ( String event : eventTypes )
            {
                query.setParameter(paramIndex, event);
                paramIndex++;
            }
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
                "AND ae.eventResult = 'success'";
        if (eventTypes != null && !eventTypes.isEmpty())
        {
            queryText += " AND ae.fullEventType IN :eventTypes ";
        }

        Query query = getEm().createQuery(queryText);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);

        if (eventTypes != null && !eventTypes.isEmpty())
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

    public AuditEvent getLastAuditEventByObjectIdAndTrackId(Long objectId, String trackId)
    {
        String queryText = "SELECT ae " +
                "FROM   AuditEvent ae " +
                "WHERE  ae.objectId = :objectId " +
                "AND 	ae.trackId = :trackId " +
                "AND 	ae.parentObjectType = 'FILE' " +
                "ORDER BY ae.eventDate desc ";

        Query findAudits = getEm().createQuery(queryText);
        findAudits.setParameter("objectId", objectId);
        findAudits.setParameter("trackId", trackId);
        findAudits.setMaxResults(1);

        List<AuditEvent> resultList = findAudits.getResultList();
        if (!resultList.isEmpty())
        {
            return resultList.get(0);
        }

        throw new NoResultException();
    }
}
