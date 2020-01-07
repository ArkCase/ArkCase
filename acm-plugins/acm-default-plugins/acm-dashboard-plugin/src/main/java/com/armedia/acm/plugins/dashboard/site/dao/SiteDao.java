package com.armedia.acm.plugins.dashboard.site.dao;

/*-
 * #%L
 * ACM Default Plugin: Dashboard
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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.dashboard.site.model.Site;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph.mcgrady on 4/26/2017.
 */
public class SiteDao extends AcmAbstractDao<Site>
{
    private static final Logger LOGGER = LogManager.getLogger(SiteDao.class);

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
