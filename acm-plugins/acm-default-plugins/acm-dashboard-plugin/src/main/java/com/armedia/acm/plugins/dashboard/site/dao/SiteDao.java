package com.armedia.acm.plugins.dashboard.site.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.dashboard.site.model.Site;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph.mcgrady on 4/26/2017.
 */
public class SiteDao extends AcmAbstractDao<Site>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteDao.class);

    @Override
    public Class<Site> getPersistenceClass()
    {
        return Site.class;
    }

    public List<Site> listAllSites(String user)
    {
        TypedQuery<Site> siteTypedQuery;
        if (user != null && !user.trim().isEmpty())
        {
            siteTypedQuery = getEm().createQuery(
                    "SELECT site " +
                            "FROM Site site " +
                            " WHERE site.user = :user " +
                            " ORDER BY site.created DESC",
                    Site.class);
            siteTypedQuery.setParameter("user", user);
        }
        else
        {
            siteTypedQuery = getEm().createQuery(
                    "SELECT site " +
                            "FROM Site site " +
                            " ORDER BY site.created DESC",
                    Site.class);
        }

        List<Site> sites = siteTypedQuery.getResultList();
        if (null == sites)
        {
            sites = new ArrayList<>();
        }
        return sites;
    }
}