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
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRole;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleName;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRolePrimaryKey;
import com.armedia.acm.services.users.model.AcmRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by marjan.stefanoski on 9/19/2014.
 */
public class WidgetDao extends AcmAbstractDao<Widget>
{

    @PersistenceContext
    private EntityManager entityManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    public Widget saveWidget(Widget in)
    {
        Widget existing;
        if (in.getWidgetId() != null)
        {
            existing = getEntityManager().find(Widget.class, in.getWidgetId());
        }
        else
        {
            try
            {
                existing = getWidgetByWidgetName(in.getWidgetName());
            }
            catch (AcmObjectNotFoundException e)
            {
                existing = null;
            }
        }
        if (existing == null)
        {
            in = getEntityManager().merge(in);
            getEntityManager().flush();
        }
        else
        {
            in = existing;
        }
        return in;
    }

    @Transactional
    public WidgetRole saveWidgetRole(WidgetRole widgetRole)
    {
        WidgetRolePrimaryKey key = new WidgetRolePrimaryKey();
        key.setRoleName(widgetRole.getRoleName());
        key.setWidgetId(widgetRole.getWidgetId());
        WidgetRole existing = getEntityManager().find(WidgetRole.class, key);

        if (existing == null)
        {
            getEntityManager().persist(widgetRole);
            getEntityManager().flush();
            return widgetRole;
        }
        getEntityManager().persist(existing);
        return existing;
    }

    public Widget getWidgetByWidgetName(String widgetName) throws AcmObjectNotFoundException
    {
        TypedQuery<Widget> widgetsByRoles = getEntityManager()
                .createQuery("SELECT widget FROM Widget widget WHERE widget.widgetName=:widgetName", Widget.class);
        widgetsByRoles.setParameter("widgetName", widgetName);
        List<Widget> retval = widgetsByRoles.getResultList();
        if (retval.isEmpty())
        {
            throw new AcmObjectNotFoundException("dashboard", null, "Widgets not found for these roles", null);
        }
        return retval.get(0);
    }

    public List<Widget> getWidgetsByWidgetNames(List<String> widgetNames)
    {
        TypedQuery<Widget> query = entityManager.createQuery("SELECT widget FROM Widget widget WHERE widget.widgetName IN :widgetNames",
                Widget.class);
        query.setParameter("widgetNames", widgetNames);
        return query.getResultList();
    }

    public List<Widget> getAllWidgets()
    {
        String queryText = "SELECT widget FROM Widget widget";
        TypedQuery<Widget> allWidgets = getEntityManager().createQuery(queryText, Widget.class);
        List<Widget> result = allWidgets.getResultList();
        return result;
    }

    public List<RolesGroupByWidgetDto> getRolesGroupByWidget() throws AcmObjectNotFoundException
    {
        String queryText = "SELECT widget.widgetName, wrole.roleName FROM Widget widget LEFT OUTER JOIN WidgetRole wrole "
                + "ON widget.widgetId=wrole.widgetId ORDER BY widget.widgetName";
        Query rolesByWidget = getEntityManager().createQuery(queryText);

        List<Object[]> rolesPerWidget = rolesByWidget.getResultList();

        List<RolesGroupByWidgetDto> result = new ArrayList<>();
        List<WidgetRoleName> roles = new ArrayList<>();
        RolesGroupByWidgetDto rolesPerW = new RolesGroupByWidgetDto();
        String widgetN = null;
        String roleN = null;
        for (Object[] roleWidget : rolesPerWidget)
        {
            widgetN = (String) roleWidget[0];
            roleN = (String) roleWidget[1];
            if (rolesPerW.getWidgetName() == null)
            {
                // only for the first widget in the list
                rolesPerW.setWidgetName(widgetN);
                addRoleNameIfNotNull(roles, roleN);
            }
            else if (widgetN.equals(rolesPerW.getWidgetName()))
            {
                addRoleNameIfNotNull(roles, roleN);
            }
            else
            {
                // all roles for the widget will be added to dto and
                // the dto will be added to the result list.
                rolesPerW.setWidgetAuthorizedRoles(roles);
                result.add(rolesPerW);
                // create a new dto object
                rolesPerW = new RolesGroupByWidgetDto();
                roles = new ArrayList<>();
                rolesPerW.setWidgetName(widgetN);
                addRoleNameIfNotNull(roles, roleN);
            }
        }
        // add the last row into the result collection... only if there were any results.
        if (!rolesPerWidget.isEmpty())
        {
            rolesPerW.setWidgetAuthorizedRoles(roles);
            result.add(rolesPerW);
        }

        if (result.isEmpty())
        {
            throw new AcmObjectNotFoundException("dashboard", null, "Roles not found for all widgets", null);
        }
        return result;
    }

    private void addRoleNameIfNotNull(List<WidgetRoleName> roles, String roleN)
    {
        if (roleN != null)
        {
            roles.add(new WidgetRoleName(roleN));
        }
    }

    public List<Widget> getAllWidgetsByRoles(Set<String> roles) throws AcmObjectNotFoundException
    {
        TypedQuery<Widget> widgetsByRoles = getEntityManager().createQuery("SELECT widget FROM Widget widget, WidgetRole widgetRole "
                + "WHERE widget.widgetId = widgetRole.widgetId AND widgetRole.roleName IN :roleNames ", Widget.class);
        widgetsByRoles.setParameter("roleNames", roles);
        List<Widget> retval = widgetsByRoles.getResultList();
        if (retval.isEmpty())
        {
            throw new AcmObjectNotFoundException("dashboard", null, "Widgets not found for these roles", null);
        }
        return retval;
    }

    @Transactional
    public WidgetRole addWidgetToARole(Widget widget, AcmRole role)
    {
        WidgetRole widgetRole = new WidgetRole();
        widgetRole.setRoleName(role.getRoleName());
        widgetRole.setWidgetId(widget.getWidgetId());
        return saveWidgetRole(widgetRole);
    }

    public void deleteWidgetRole(WidgetRole widgetRole)
    {
        getEntityManager().remove(widgetRole);
    }

    @Transactional
    public void deleteWidget(Widget widget)
    {
        Query deleteWidget = getEntityManager().createQuery("DELETE FROM Widget widget WHERE widget.widgetName=:widgetName");

        deleteWidget.setParameter("widgetName", widget.getWidgetName());
        deleteWidget.executeUpdate();
    }

    @Transactional
    public int deleteAllWidgetRolesByWidgetName(String widgetName)
    {
        Query deleteAllRolesPerWidget = getEntityManager().createQuery("DELETE FROM WidgetRole wrole WHERE wrole.widgetId IN "
                + "(SELECT widget.widgetId FROM Widget widget WHERE widget.widgetName=:widgetName)");

        deleteAllRolesPerWidget.setParameter("widgetName", widgetName);
        int i = deleteAllRolesPerWidget.executeUpdate();
        return i;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    protected Class<Widget> getPersistenceClass()
    {
        return Widget.class;
    }
}
