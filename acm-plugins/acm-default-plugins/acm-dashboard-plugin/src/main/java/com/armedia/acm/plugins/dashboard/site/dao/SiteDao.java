package com.armedia.acm.plugins.dashboard.site.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.dashboard.site.model.Site;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joseph.mcgrady on 4/26/2017.
 */
public class SiteDao extends AcmAbstractDao<Site>
{
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteDao.class);

    @Override
    public Class<Site> getPersistenceClass()
    {
        return Site.class;
    }

    public Site findByUser(String user)
    {
        Preconditions.checkNotNull(user, "user cannot be null");

        TypedQuery<Site> findByIdQuery = getEntityManager().createQuery(
                "SELECT site from Site site WHERE site.user = :user", Site.class);
        findByIdQuery.setParameter("user", user);
        return findByIdQuery.getSingleResult();
    }

    public Site findById(Long id)
    {
        Preconditions.checkNotNull(id, "id cannot be null");

        TypedQuery<Site> findByIdQuery = getEntityManager().createQuery(
                "SELECT site from Site site WHERE site.id = :id", Site.class);
        findByIdQuery.setParameter("id", id);
        return findByIdQuery.getSingleResult();
    }

    @Transactional
    public void deleteByUser(String user)
    {
        Preconditions.checkNotNull(user, "user cannot be null");

        TypedQuery<Site> deleteByUserQuery = getEntityManager().createQuery(
                "SELECT site from Site site " +
                        "WHERE site.user = :user", Site.class);
        deleteByUserQuery.setParameter("user", user);
        getEntityManager().remove(deleteByUserQuery.getSingleResult());
    }

    @Transactional
    public void deleteById(Long id)
    {
        Preconditions.checkNotNull(id, "id cannot be null");

        TypedQuery<Site> deleteByIdQuery = getEntityManager().createQuery(
                "SELECT site from Site site " +
                        "WHERE site.id = :id", Site.class);
        deleteByIdQuery.setParameter("id", id);
        getEntityManager().remove(deleteByIdQuery.getSingleResult());
    }

    public int countAll()
    {
        int numSites = 0;

        try
        {
            TypedQuery<Long> siteCountQuery = getEntityManager().createQuery(
                    "SELECT COUNT(site) FROM Site site ", Long.class);
            numSites = siteCountQuery.getSingleResult().intValue();
        } catch (Exception e)
        {
            LOGGER.error("Failed to get count for Sites", e);
        }
        return numSites;
    }

    public List<Site> listAllSites(String user)
    {
        TypedQuery<Site> siteTypedQuery;
        if (user != null && !user.trim().isEmpty())
        {
            siteTypedQuery = getEntityManager().createQuery(
                    "SELECT site " +
                            "FROM Site site " +
                            " WHERE site.user = :user " +
                            " ORDER BY site.created DESC", Site.class);
            siteTypedQuery.setParameter("user", user);
        } else
        {
            siteTypedQuery = getEntityManager().createQuery(
                    "SELECT site " +
                            "FROM Site site " +
                            " ORDER BY site.created DESC", Site.class);
        }

        List<Site> sites = siteTypedQuery.getResultList();
        if (null == sites)
        {
            sites = new ArrayList<>();
        }
        return sites;
    }

    public List<Site> listSites(Integer firstResult, Integer maxNumResults, String sort)
    {
        TypedQuery<Site> siteTypedQuery = getSortedSiteQuery(sort);

        // Restricts result set to one page of data optionally
        if (firstResult != null)
        {
            siteTypedQuery.setFirstResult(firstResult);
        }
        if (maxNumResults != null)
        {
            siteTypedQuery.setMaxResults(maxNumResults);
        }

        List<Site> sites = siteTypedQuery.getResultList();
        if (null == sites)
        {
            sites = new ArrayList<>();
        }
        return sites;
    }

    private TypedQuery<Site> getSortedSiteQuery(String sort)
    {
        TypedQuery<Site> siteTypedQuery = null;
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Site> queryBuilder = criteriaBuilder.createQuery(Site.class);
        Root<Site> site = queryBuilder.from(Site.class);

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
        siteTypedQuery = getEntityManager().createQuery(queryBuilder);
        return siteTypedQuery;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }
}