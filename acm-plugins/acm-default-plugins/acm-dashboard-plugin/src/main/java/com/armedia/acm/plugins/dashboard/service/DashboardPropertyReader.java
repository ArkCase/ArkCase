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
import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.dao.ModuleDao;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardConfig;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
import com.armedia.acm.plugins.dashboard.model.module.Module;
import com.armedia.acm.plugins.dashboard.model.userPreference.UserPreference;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRole;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 14.01.2016.
 */

public class DashboardPropertyReader
{
    private DashboardConfig dashboardConfig;
    private ModuleDao moduleDao;
    private WidgetDao widgetDao;
    private UserDao userDao;
    private DashboardDao dashboardDao;
    private UserPreferenceService userPreferenceService;
    private ModuleEventPublisher moduleEventPublisher;
    private Logger log = LogManager.getLogger(getClass());
    private List<String> moduleNameList;
    private List<Widget> widgetList;
    private List<Widget> dashboardWidgetsOnly;
    private boolean isWidgetTableEmpty = false;
    private List<Widget> allWidgetsInDB;

    private void init()
    {
        boolean isNewWidgetForAdding = dashboardConfig.getAddWidget();

        allWidgetsInDB = getWidgetDao().getAllWidgets();

        if (allWidgetsInDB.isEmpty())
            isWidgetTableEmpty = true;

        log.info("Initializing - setting moduleNameList and widgetList in the  DashboardPropertyReader bean");
        try
        {
            this.moduleNameList = getModuleNamesAndCreateList();
        }
        catch (AcmDashboardException e)
        {
            log.error("Module name list was not populated, error occurred: [{}]", e.getMessage(), e);
        }
        try
        {
            this.widgetList = readWidgetNamesAndCreateWidgetList();
        }
        catch (AcmDashboardException e)
        {
            log.error("Widgets list was not populated, error occurred: [{}]", e.getMessage(), e);
        }

        if (isNewWidgetForAdding)
        {
            addNewWidgets();
        }

        if (isWidgetTableEmpty)
        {
            initWidgetTable();
        }
        initWidgetRolesTable();
        try
        {
            this.dashboardWidgetsOnly = getDashboardWidgets();
        }
        catch (AcmDashboardException e)
        {
            log.error("Dashboard Widgets list was not populated, error occurred: [{}] ", e.getMessage(), e);
        }

        updateModuleTable();
    }

    private void initWidgetTable()
    {
        addNewWidgets();
    }

    private void initWidgetRolesTable()
    {
        List<AcmRole> allRoles = getUserDao().findAllRoles();
        Set<String> widgetSet = new HashSet<>();
        String retVal = null;
        boolean isRoleFound = false;

        String jsonRoleWidgetsString = dashboardConfig.getRoleWidgets();
        JSONArray jsonArray = new JSONArray(jsonRoleWidgetsString);
        for (AcmRole role : allRoles)
        {
            for (int i = 0; i < jsonArray.length(); i++)
            {
                if (role.getRoleName().equals(jsonArray.getJSONObject(i).getString(DashboardConstants.ROLE)))
                {
                    retVal = jsonArray.getJSONObject(i).getString(DashboardConstants.WIDGET_LIST);
                    isRoleFound = true;
                    break;
                }
                isRoleFound = false;
            }

            if (!isRoleFound)
            {
                continue;
            }

            widgetSet.addAll(Arrays.asList(retVal.split(DashboardConstants.COMMA_SPLITTER)));
            widgetSet.forEach(widgetName -> {
                try
                {
                    Widget widget = getWidgetDao().getWidgetByWidgetName(widgetName.trim());
                    addWidgetRoleIntoDB(widget, role);
                }
                catch (Exception e)
                {
                    log.error("Fetching widget with widget name: [{}] failed! Error msg: [{}]", widgetName, e.getMessage(), e);
                }
            });
            widgetSet.clear();
        }
    }

    private WidgetRole addWidgetRoleIntoDB(Widget widget, AcmRole role)
    {
        WidgetRole widgetRole = new WidgetRole();
        widgetRole.setWidgetId(widget.getWidgetId());
        widgetRole.setRoleName(role.getRoleName());
        return getWidgetDao().saveWidgetRole(widgetRole);
    }

    private List<String> getModuleNamesAndCreateList() throws AcmDashboardException
    {
        String modulesString;
        try
        {
            log.info("Fetching all module names from the property file");
            modulesString = dashboardConfig.getModules();
        }
        catch (Exception e)
        {
            throw new AcmDashboardException("Error occurred while fetching module names " + e.getMessage(), e);
        }

        if (StringUtils.isNotBlank(modulesString))
        {
            String[] modules = modulesString.split(",");
            return Arrays.stream(modules)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private List<Widget> readWidgetNamesAndCreateWidgetList() throws AcmDashboardException
    {
        String newWidgetsString;
        try
        {
            newWidgetsString = dashboardConfig.getNewWidgets();
        }
        catch (Exception e)
        {
            throw new AcmDashboardException("Error occurred while fetching widget names " + e.getMessage(), e);
        }

        return transformWidgetNamesArrayToWidgetList(newWidgetsString);
    }

    private List<Widget> getDashboardWidgets() throws AcmDashboardException
    {
        try
        {
            String dashboardWidgetsString = dashboardConfig.getModuleDashboardWidgets();
            return getDashboardWidgetsFromDB(dashboardWidgetsString);
        }
        catch (Exception e)
        {
            throw new AcmDashboardException("Error occurred while fetching dashboard widget names " + e.getMessage(), e);
        }
    }

    private List<Widget> transformWidgetNamesArrayToWidgetList(String widgetNamesString)
    {
        if (StringUtils.isNotBlank(widgetNamesString))
        {
            String[] widgetsNames = widgetNamesString.split(",");
            return Arrays.stream(widgetsNames)
                    .map(it -> {
                        Widget widget = new Widget();
                        widget.setWidgetName(it.trim());
                        return widget;
                    })
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private void updateModuleTable()
    {
        moduleNameList.forEach(module -> {
            try
            {
                moduleDao.getModuleByName(module);
            }
            catch (AcmObjectNotFoundException e)
            {
                Module m = new Module();
                m.setModuleName(module);
                try
                {
                    moduleDao.save(m);
                    log.info("Module with module name: [{}] is added! ", m.getModuleId());
                    moduleEventPublisher.publishModuleCreated(m, null, null, true);
                }
                catch (Exception e1)
                {
                    log.error("Persisting new module name failed due to error: [{}]", e1.getMessage(), e1);
                    moduleEventPublisher.publishModuleCreated(m, null, null, false);
                }
            }
        });
    }

    private void addNewWidgets()
    {
        // get all widgets added in the dashboardPlugin.properties config file;
        Set<Widget> setOfAllWidgetsFromPropertyFile = new HashSet<>(getWidgetList());

        Set<Widget> widgetsThatNeedToBeInserted = new HashSet<>();
        Set<Widget> widgetsThatNeedToBeDeleted = new HashSet<>();

        // check if there are already widgets in the DB and if widgets found compare with the list from property file
        // and
        // made appropriate changes in the DB if needed, if widgets are not found insert all of them from the property
        // file
        if (allWidgetsInDB != null)
        {

            Set<Widget> setOfAllWidgetsInDb = new HashSet<>(allWidgetsInDB);

            // A difference between set of widgets from the DB and set of widgets from the
            // property file is calculated. the result will be all widgets that are in DB but
            // not in the property file, all widgets that need to be removed from the DB!
            widgetsThatNeedToBeDeleted.addAll(setOfAllWidgetsInDb);
            widgetsThatNeedToBeDeleted.removeAll(setOfAllWidgetsFromPropertyFile);

            // A difference between the set of widgets from the property file and set of widgets from the
            // DB is calculated. the result will be all widgets that are in the property file but
            // not in the DB, all widgets that need to be inserted into the DB!
            // The intersection between these two sets will remain intact in the DB.
            widgetsThatNeedToBeInserted.addAll(setOfAllWidgetsFromPropertyFile);
            widgetsThatNeedToBeInserted.removeAll(setOfAllWidgetsInDb);

            widgetsThatNeedToBeDeleted.forEach(widget -> {
                String widgetName = widget.getWidgetName();
                Long widgetId = widget.getWidgetId();

                widgetDao.deleteAllWidgetRolesByWidgetName(widgetName);

                deleteUserWidgetPreferences(widgetId, widgetName);

                widgetDao.deleteWidget(widget);
            });

            widgetsThatNeedToBeInserted.forEach(widget -> widgetDao.saveWidget(widget));
        }
        else
        {
            setOfAllWidgetsFromPropertyFile.forEach(widget -> widgetDao.saveWidget(widget));
        }
    }

    private List<Widget> getDashboardWidgetsFromDB(String dashboardWidgets)
    {
        List<Widget> widgetList = new ArrayList<>();
        String[] widgetNames = dashboardWidgets.split(",");
        List<String> widgetNamesList = Arrays.asList(widgetNames);

        widgetNamesList.forEach(widget -> {
            try
            {
                widgetList.add(widgetDao.getWidgetByWidgetName(widget));
            }
            catch (AcmObjectNotFoundException e)
            {
                log.error("Fetching widget with widget name: [{}] failed! Error msg: [{}]", widget, e.getMessage(), e);
            }
        });
        return widgetList;
    }

    private void deleteUserWidgetPreferences(Long widgetId, String widgetName)
    {
        List<UserPreference> userPreferences = userPreferenceService.findByWidgetId(widgetId);
        if (CollectionUtils.isNotEmpty(userPreferences))
        {
            userPreferences.forEach(userPreference -> {
                String moduleName = userPreference.getModule().getModuleName();
                try
                {
                    // also update preferred widget in JSON configuration
                    Dashboard dashboard = dashboardDao.getDashboardConfigForUserAndModuleName(userPreference.getUser(),
                            moduleName);
                    String configFiltered = removeWidgetFromDashboardConfig(dashboard.getDashboardConfig(), widgetName);
                    dashboard.setDashboardConfig(configFiltered);
                    dashboardDao.save(dashboard);
                }
                catch (AcmObjectNotFoundException e)
                {
                    log.debug("No dashboard configuration for widget {} and module {}. {}", widgetName, moduleName,
                            e.getMessage());
                }
            });

            userPreferenceService.deleteByWidgetId(widgetId);
        }
    }

    public String removeWidgetFromDashboardConfig(String config, String widgetName)
    {
        try
        {
            JSONObject jsonConfig = new JSONObject(config);
            return DashboardService.removeWidgetsFromJson(jsonConfig, new HashSet<>(Arrays.asList(widgetName)));
        }
        catch (JSONException e)
        {
            log.warn("Not valid JSON format. {}", e.getMessage());
        }
        return config;

    }

    public List<String> getModuleNameList()
    {
        return moduleNameList;
    }

    public void setModuleNameList(List<String> moduleNameList)
    {
        this.moduleNameList = moduleNameList;
    }

    public List<Widget> getWidgetList()
    {
        return widgetList;
    }

    public void setWidgetList(List<Widget> widgetList)
    {
        this.widgetList = widgetList;
    }

    public List<Widget> getDashboardWidgetsOnly()
    {
        return dashboardWidgetsOnly;
    }

    public void setDashboardWidgetsOnly(List<Widget> dashboardWidgetsOnly)
    {
        this.dashboardWidgetsOnly = dashboardWidgetsOnly;
    }

    public ModuleDao getModuleDao()
    {
        return moduleDao;
    }

    public void setModuleDao(ModuleDao moduleDao)
    {
        this.moduleDao = moduleDao;
    }

    public WidgetDao getWidgetDao()
    {
        return widgetDao;
    }

    public void setWidgetDao(WidgetDao widgetDao)
    {
        this.widgetDao = widgetDao;
    }

    public ModuleEventPublisher getModuleEventPublisher()
    {
        return moduleEventPublisher;
    }

    public void setModuleEventPublisher(ModuleEventPublisher moduleEventPublisher)
    {
        this.moduleEventPublisher = moduleEventPublisher;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public UserPreferenceService getUserPreferenceService()
    {
        return userPreferenceService;
    }

    public void setUserPreferenceService(UserPreferenceService userPreferenceService)
    {
        this.userPreferenceService = userPreferenceService;
    }

    public DashboardDao getDashboardDao()
    {
        return dashboardDao;
    }

    public void setDashboardDao(DashboardDao dashboardDao)
    {
        this.dashboardDao = dashboardDao;
    }

    public DashboardConfig getDashboardConfig()
    {
        return dashboardConfig;
    }

    public void setDashboardConfig(DashboardConfig dashboardConfig)
    {
        this.dashboardConfig = dashboardConfig;
    }
}
