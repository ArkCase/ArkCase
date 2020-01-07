package com.armedia.acm.plugins.dashboard.service;

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
import com.armedia.acm.plugins.dashboard.dao.ModuleDao;
import com.armedia.acm.plugins.dashboard.dao.UserPreferenceDao;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.model.module.Module;
import com.armedia.acm.plugins.dashboard.model.userPreference.PreferredWidgetsDto;
import com.armedia.acm.plugins.dashboard.model.userPreference.UserPreference;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.AcmUserRoleService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by marjan.stefanoski on 18.01.2016.
 */
public class UserPreferenceService
{
    private UserPreferenceDao userPreferenceDao;
    private UserDao userDao;
    private WidgetDao widgetDao;
    private ModuleDao moduleDao;
    private UserPreferenceEventPublisher userPreferenceEventPublisher;
    private DashboardPropertyReader dashboardPropertyReader;
    private AcmUserRoleService userRoleService;
    private Logger log = LogManager.getLogger(getClass());

    public PreferredWidgetsDto updateUserPreferenceWidgets(String userId, PreferredWidgetsDto preferredWidgets, String ipAddress)
            throws AcmObjectNotFoundException
    {
        List<Widget> widgetList;
        if (preferredWidgets.getPreferredWidgets().isEmpty())
        {
            widgetList = Collections.emptyList();
        }
        else
        {
            widgetList = createWidgetList(preferredWidgets.getPreferredWidgets());
        }
        List<UserPreference> upList = null;
        Module module;
        AcmUser user;
        try
        {
            module = getModule(preferredWidgets.getModuleName());
        }
        catch (AcmObjectNotFoundException e)
        {
            log.error("Module with module name [{}]  is not found! Error msg: [{}]", preferredWidgets.getModuleName(), e.getMessage(), e);
            throw e;
        }
        user = userDao.findByUserId(userId);
        try
        {
            upList = getUserPreferenceListByUserAndModule(user, module);
        }
        catch (AcmObjectNotFoundException e)
        {
            log.info("No User Preference found for user: [{}] and module name: [{}]  ", userId, module.getModuleName());
        }

        try
        {
            int i = deleteOldUserPreferenceByUserAndModule(user, module);
            log.info("Deleted " + i + " UserPreference records");
            if (upList != null)
            {
                upList.stream().forEach(userPreference -> {
                    userPreferenceEventPublisher.publishUserPreferenceDeleted(userPreference, ipAddress, true);
                });
            }
        }
        catch (Exception e)
        {
            log.error("Error occurred while deleting UserPreference records. Error msg: [{}] ", e.getMessage(), e);
            if (upList != null)
            {
                upList.stream().forEach(userPreference -> {
                    userPreferenceEventPublisher.publishUserPreferenceDeleted(userPreference, ipAddress, false);
                });
            }
        }

        saveNewUserPreference(user, widgetList, module, ipAddress);

        return preferredWidgets;
    }

    private int deleteOldUserPreferenceByUserAndModule(AcmUser user, Module module)
    {
        return userPreferenceDao.deleteAllUserPreferenceByUserIdAndModuleName(user.getUserId(), module.getModuleName());
    }

    public PreferredWidgetsDto getPreferredWidgetsByUserAndModule(String userId, String moduleName) throws AcmObjectNotFoundException
    {
        if (!dashboardPropertyReader.getModuleNameList().contains(moduleName))
        {
            log.error("Module with name: [{}] is not found!", moduleName);
            throw new AcmObjectNotFoundException("User Preference", null, "Module not found!", null);
        }
        PreferredWidgetsDto preferredWidgetsDto = new PreferredWidgetsDto();
        List<Widget> widgetList;
        List<String> widgetNamesList = new ArrayList<>();

        try
        {
            widgetList = userPreferenceDao.getUserPreferredListOfWidgetsByUserAndModuleName(userId, moduleName);
        }
        catch (AcmObjectNotFoundException e)
        {

            log.info("No preferred widgets for user: [{}] and module: [{}]", userId, moduleName);
            widgetList = getAllAllowedWidgetsForUser(userId);
        }
        widgetList.stream().forEach(widget -> widgetNamesList.add(widget.getWidgetName()));
        preferredWidgetsDto.setModuleName(moduleName);
        preferredWidgetsDto.setPreferredWidgets(widgetNamesList);

        return preferredWidgetsDto;
    }

    private List<Widget> getAllAllowedWidgetsForUser(String userId) throws AcmObjectNotFoundException
    {
        List<Widget> result;
        Set<String> roles = userRoleService.getUserRoles(userId);
        try
        {
            result = onlyUniqueValues(widgetDao.getAllWidgetsByRoles(roles));
            if (!result.isEmpty())
            {
                log.info("All allowed widgets for the user: [{}] will be returned!", userId);
            }
        }
        catch (AcmObjectNotFoundException e)
        {
            log.error("No widgets are allowed for the user: [{}]. Error msg: [{}] ", userId, e.getMessage(), e);
            throw e;
        }
        return result;
    }

    private List<Widget> onlyUniqueValues(List<Widget> widgets)
    {
        Set<Widget> widgetSet = new HashSet<>();
        List<Widget> result = new ArrayList<>();

        widgets.stream().forEach(widget -> widgetSet.add(widget));
        widgetSet.stream().forEach(widget -> result.add(widget));

        return result;
    }

    private List<UserPreference> getUserPreferenceListByUserAndModule(AcmUser user, Module module) throws AcmObjectNotFoundException
    {
        return userPreferenceDao.getUserPreferenceListByUserModuleName(user.getUserId(), module.getModuleName());
    }

    private void saveNewUserPreference(AcmUser user, List<Widget> widgetList, Module module, String ipAddress)
    {
        widgetList.stream().forEach(widget -> {
            UserPreference userPreference = new UserPreference();
            userPreference.setModule(module);
            userPreference.setUser(user);
            userPreference.setWidget(widget);
            try
            {
                userPreference = userPreferenceDao.save(userPreference);
                userPreferenceEventPublisher.publishUserPreferenceCreated(userPreference, ipAddress, true);
            }
            catch (Exception e)
            {
                userPreferenceEventPublisher.publishUserPreferenceCreated(userPreference, ipAddress, false);
            }
        });
    }

    private List<Widget> createWidgetList(List<String> widgetNamesList)
    {
        return widgetDao.getWidgetsByWidgetNames(widgetNamesList);
    }

    public void deleteByWidgetId(Long widgetId)
    {
        userPreferenceDao.deleteByWidgetId(widgetId);
    }

    public List<UserPreference> findByWidgetId(Long widgetId)
    {
        return userPreferenceDao.findByWidgetId(widgetId);
    }

    private Module getModule(String moduleName) throws AcmObjectNotFoundException
    {
        return moduleDao.getModuleByName(moduleName);
    }

    public UserPreferenceDao getUserPreferenceDao()
    {
        return userPreferenceDao;
    }

    public void setUserPreferenceDao(UserPreferenceDao userPreferenceDao)
    {
        this.userPreferenceDao = userPreferenceDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public WidgetDao getWidgetDao()
    {
        return widgetDao;
    }

    public void setWidgetDao(WidgetDao widgetDao)
    {
        this.widgetDao = widgetDao;
    }

    public ModuleDao getModuleDao()
    {
        return moduleDao;
    }

    public void setModuleDao(ModuleDao moduleDao)
    {
        this.moduleDao = moduleDao;
    }

    public UserPreferenceEventPublisher getUserPreferenceEventPublisher()
    {
        return userPreferenceEventPublisher;
    }

    public void setUserPreferenceEventPublisher(UserPreferenceEventPublisher userPreferenceEventPublisher)
    {
        this.userPreferenceEventPublisher = userPreferenceEventPublisher;
    }

    public DashboardPropertyReader getDashboardPropertyReader()
    {
        return dashboardPropertyReader;
    }

    public void setDashboardPropertyReader(DashboardPropertyReader dashboardPropertyReader)
    {
        this.dashboardPropertyReader = dashboardPropertyReader;
    }

    public void setUserRoleService(AcmUserRoleService userRoleService)
    {
        this.userRoleService = userRoleService;
    }
}
