package com.armedia.acm.plugins.dashboard.dao;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.List;

/**
 * Created by marst on 7/29/14.
 */

public class DashboardDao extends AcmAbstractDao<Dashboard>
{

    public Dashboard getDashboardConfigForUserAndModuleName(AcmUser user, String moduleName) throws AcmObjectNotFoundException
    {
        String queryString = "SELECT d FROM Dashboard d WHERE  d.dashboardOwner = :dashboardOwner AND d.moduleName = :moduleName ";

        TypedQuery<Dashboard> query = getEm().createQuery(queryString, Dashboard.class);

        query.setParameter("dashboardOwner", user);
        query.setParameter("moduleName", moduleName);

        List<Dashboard> results;
        results = query.getResultList();

        if (results.isEmpty())
        {
            throw new AcmObjectNotFoundException("dashboard", null, "Object not found", null);
        }
        return results.get(0);
    }

    @Transactional
    public int setDashboardConfigForUserAndModule(AcmUser user, DashboardDto newDashboardDto, String moduleName)
    {
        Query updateStatusQuery = getEm().createQuery(
                "UPDATE Dashboard " +
                        "SET dashboardConfig = :dashboardConfig, collapsed = :collapsed " +
                        "WHERE dashboardOwner = :dashboardOwner AND moduleName = :moduleName");
        updateStatusQuery.setParameter("dashboardConfig", newDashboardDto.getDashboardConfig());
        updateStatusQuery.setParameter("dashboardOwner", user);
        updateStatusQuery.setParameter("moduleName", moduleName);
        updateStatusQuery.setParameter("collapsed", new Boolean(newDashboardDto.isCollapsed()));

        return updateStatusQuery.executeUpdate();
    }

    @Override
    protected Class<Dashboard> getPersistenceClass()
    {
        return Dashboard.class;
    }
}
