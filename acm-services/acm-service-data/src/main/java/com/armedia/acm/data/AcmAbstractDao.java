package com.armedia.acm.data;

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

public abstract class AcmAbstractDao<T>
{
    @PersistenceContext
    private EntityManager em;

    @Transactional(propagation = Propagation.REQUIRED)
    public T save(T toSave)
    {
        T saved = em.merge(toSave);
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
        TypedQuery<T> allRecords = em.createQuery("SELECT e FROM " + getPersistenceClass().getSimpleName() + " e order by e." + column,
                getPersistenceClass());
        List<T> retval = allRecords.getResultList();
        return retval;
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
                } else
                {
                    queryBuilder.orderBy(criteriaBuilder.desc(site.get(fieldName)));
                }
            } else
            {
                // default sort order is descending by created date
                queryBuilder.orderBy(criteriaBuilder.desc(site.get("created")));
            }
        } else
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
}
