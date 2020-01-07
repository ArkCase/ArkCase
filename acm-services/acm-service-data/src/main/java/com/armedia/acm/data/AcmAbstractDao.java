package com.armedia.acm.data;

/*-
 * #%L
 * ACM Service: Data Tools
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

import com.armedia.acm.data.event.AcmSequenceEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AcmAbstractDao<T> implements ApplicationEventPublisherAware
{
    @PersistenceContext
    private EntityManager em;

    private ApplicationEventPublisher applicationEventPublisher;

    @Transactional(propagation = Propagation.REQUIRED)
    public T save(T toSave)
    {
        T saved = em.merge(toSave);
        applicationEventPublisher.publishEvent(new AcmSequenceEvent(saved));
        return saved;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public T find(Long id)
    {
        T found = em.find(getPersistenceClass(), id);
        return found;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> findAll()
    {
        TypedQuery<T> allRecords = em.createQuery("SELECT e FROM " + getPersistenceClass().getSimpleName() + " e", getPersistenceClass());
        List<T> retval = allRecords.getResultList();
        return retval;
    }

    /**
     * Retrieve all entities of a given type, sorted by particular column
     *
     * @param column
     *            column name (entity field name) to sort by
     * @return list of entities, sorted
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> findAllOrderBy(String column)
    {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> allRecords = criteriaBuilder.createQuery(getPersistenceClass());
        Root<T> entityRoot = allRecords.from(getPersistenceClass());
        allRecords.select(entityRoot);
        allRecords.orderBy(criteriaBuilder.asc(entityRoot.get(column)));

        return em.createQuery(allRecords).getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> findModifiedSince(Date lastModified, int startRow, int pageSize)
    {
        TypedQuery<T> sinceWhen = getEm().createQuery("SELECT e " + "FROM " + getPersistenceClass().getSimpleName() + " e "
                + "WHERE e.modified >= :lastModified " + "ORDER BY e.created", getPersistenceClass());
        sinceWhen.setParameter("lastModified", lastModified);
        sinceWhen.setFirstResult(startRow);
        sinceWhen.setMaxResults(pageSize);

        List<T> retval = sinceWhen.getResultList();
        return retval;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public TypedQuery<T> getSortedQuery(String sort)
    {
        TypedQuery<T> siteTypedQuery = null;
        CriteriaBuilder criteriaBuilder = getEm().getCriteriaBuilder();
        CriteriaQuery<T> queryBuilder = criteriaBuilder.createQuery(getPersistenceClass());
        Root<T> site = queryBuilder.from(getPersistenceClass());

        // Query will retrieve all sites associated with the user
        queryBuilder.select(site);

        // Can be sorted by any of the fields ascending or descending
        if (sort != null)
        {
            Pattern sortPattern = Pattern.compile("^([a-zA-Z0-9_]+)\\s+([a-zA-Z0-9_]+)$");
            Matcher sortMatcher = sortPattern.matcher(sort);
            if (sortMatcher.matches())
            {
                String fieldName = sortMatcher.group(1);
                String direction = sortMatcher.group(2);
                if ("ASC".equalsIgnoreCase(direction))
                {
                    queryBuilder.orderBy(criteriaBuilder.asc(site.get(fieldName)));
                }
                else
                {
                    queryBuilder.orderBy(criteriaBuilder.desc(site.get(fieldName)));
                }
            }
            else
            {
                // default sort order is descending by created date
                queryBuilder.orderBy(criteriaBuilder.desc(site.get("created")));
            }
        }
        else
        {
            queryBuilder.orderBy(criteriaBuilder.desc(site.get("created")));
        }
        siteTypedQuery = getEm().createQuery(queryBuilder);
        return siteTypedQuery;
    }

    protected abstract Class<T> getPersistenceClass();

    /**
     * This method should be implemented under appropriate DAO. It should return OBJECT_TYPE
     *
     * @return
     */
    public String getSupportedObjectType()
    {
        return null;
    }

    public EntityManager getEm()
    {
        return em;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(org.springframework.
     * context.ApplicationEventPublisher)
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

}
