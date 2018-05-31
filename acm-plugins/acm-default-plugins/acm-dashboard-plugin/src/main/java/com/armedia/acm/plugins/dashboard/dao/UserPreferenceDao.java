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
import com.armedia.acm.plugins.dashboard.model.userPreference.UserPreference;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.List;

/**
 * Created by marjan.stefanoski on 15.01.2016.
 */
public class UserPreferenceDao extends AcmAbstractDao<UserPreference>
{
    public List<Widget> getUserPreferredListOfWidgetsByUserAndModuleName(String user, String moduleName) throws AcmObjectNotFoundException
    {
        String queryString = "SELECT up.widget FROM UserPreference up  WHERE  up.user.userId = :user AND up.module.moduleName = :moduleName";

        TypedQuery<Widget> query = getEm().createQuery(queryString, Widget.class);

        query.setParameter("user", user);
        query.setParameter("moduleName", moduleName);

        List<Widget> results;
        results = query.getResultList();

        if (results.isEmpty())
        {
            throw new AcmObjectNotFoundException("User Preference", null, "Object not found", null);
        }
        return results;
    }

    public List<UserPreference> getUserPreferenceListByUserModuleName(String user, String moduleName) throws AcmObjectNotFoundException
    {
        String queryString = "SELECT up FROM UserPreference up  WHERE  up.user.userId = :user AND up.module.moduleName = :moduleName";

        TypedQuery<UserPreference> query = getEm().createQuery(queryString, UserPreference.class);

        query.setParameter("user", user);
        query.setParameter("moduleName", moduleName);

        List<UserPreference> results;
        results = query.getResultList();

        if (results.isEmpty())
        {
            throw new AcmObjectNotFoundException("User Preference", null, "Object not found", null);
        }
        return results;
    }

    @Transactional
    public int deleteAllUserPreferenceByUserIdAndModuleName(String userId, String moduleName)
    {

        String deleteQueryString = " DELETE FROM UserPreference up WHERE up.user.userId = :userId AND up.module.moduleName = :moduleName";
        Query deleteQuery = getEm().createQuery(deleteQueryString);
        deleteQuery.setParameter("userId", userId);
        deleteQuery.setParameter("moduleName", moduleName);

        int i = deleteQuery.executeUpdate();
        return i;
    }

    @Override
    protected Class<UserPreference> getPersistenceClass()
    {
        return UserPreference.class;
    }

    @Transactional
    public void deleteByWidgetId(Long widgetId)
    {
        String queryString = "DELETE FROM UserPreference up WHERE up.widget.widgetId = :widgetId";
        Query deleteQuery = getEm().createQuery(queryString);
        deleteQuery.setParameter("widgetId", widgetId);
        deleteQuery.executeUpdate();
    }

    public List<UserPreference> findByWidgetId(Long widgetId)
    {
        String queryString = "SELECT up FROM UserPreference up WHERE up.widget.widgetId = :widgetId";
        TypedQuery<UserPreference> selectQuery = getEm().createQuery(queryString, UserPreference.class);
        selectQuery.setParameter("widgetId", widgetId);
        return selectQuery.getResultList();
    }
}
