package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.dao.ModuleDao;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
import com.armedia.acm.plugins.dashboard.model.module.Module;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRole;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by marjan.stefanoski on 14.01.2016.
 */


public class DashboardPropertyReader
{
    private AcmPlugin dashboardPlugin;
    private ModuleDao moduleDao;
    private WidgetDao widgetDao;
    private UserDao userDao;
    private ModuleEventPublisher moduleEventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    private List<String> moduleNameList;
    private List<Widget> widgetList;
    private boolean isNewWidgetForAdding = false;
    private List<Widget> dashboardWidgetsOnly;
    private boolean isWidgetTableEmpty = false;
    private List<Widget> allWidgetsInDB;

    private void init()
    {
        isNewWidgetForAdding = Boolean.valueOf((String) dashboardPlugin.getPluginProperties().
                get(DashboardConstants.IS_NEW_DASHBOARD_WIDGETS_FOR_ADDING));

        allWidgetsInDB = getWidgetDao().getAllWidgets();

        if (allWidgetsInDB.isEmpty())
            isWidgetTableEmpty = true;

        log.info("Initializing - setting moduleNameList and widgetList in the  DashboardPropertyReader bean");
        try
        {
            this.moduleNameList = getModuleNamesAndCreateList();
        } catch (AcmDashboardException e)
        {
            log.error("Module name list was not populated, error occurred: [{}]", e.getMessage(), e);
        }
        try
        {
            this.widgetList = readWidgetNamesAndCreateWidgetList();
        } catch (AcmDashboardException e)
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
            initWidgetRolesTable();
        }
        try
        {
            this.dashboardWidgetsOnly = getDashboardWidgets();
        } catch (AcmDashboardException e)
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

        if (!dashboardPlugin.getPluginProperties().isEmpty())
        {
            Map<String, Object> dashboardPluginPluginProperties = dashboardPlugin.getPluginProperties();
            String jsonRoleWidgetsString = (String) dashboardPluginPluginProperties.get(DashboardConstants.ROLE_WIDGET_LIST);
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
                widgetSet.forEach(widgetName ->
                {
                    try
                    {
                        Widget widget = getWidgetDao().getWidgetByWidgetName(widgetName.trim());
                        addWidgetRoleIntoDB(widget, role);
                    } catch (Exception e)
                    {
                        log.error("Fetching widget with widget name: [{}] failed! Error msg: [{}]", widgetName, e.getMessage(), e);
                    }
                });
                widgetSet.clear();
            }
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
            modulesString = (String) dashboardPlugin.getPluginProperties().get(DashboardConstants.MODULES_STRING);
        } catch (Exception e)
        {
            throw new AcmDashboardException("Error occurred while fetching module names " + e.getMessage(), e);
        }


        String[] modules;
        List<String> moduleList = new ArrayList<>();

        if (!"".equals(modulesString))
        {
            modules = modulesString.split(",");
            for (String m : modules)
            {
                moduleList.add(m.trim());
            }
        }
        return moduleList;
    }

    private List<Widget> readWidgetNamesAndCreateWidgetList() throws AcmDashboardException
    {
        String newWidgetsString;
        try
        {
            newWidgetsString = (String) dashboardPlugin.getPluginProperties().get(DashboardConstants.WIDGETS_STRING);
        } catch (Exception e)
        {
            throw new AcmDashboardException("Error occurred while fetching widget names " + e.getMessage(), e);
        }

        return transformWidgetNamesArrayToWidgetList(newWidgetsString);
    }

    private List<Widget> getDashboardWidgets() throws AcmDashboardException
    {
        String dashboardWidgetsString;
        try
        {
            dashboardWidgetsString = (String) dashboardPlugin.getPluginProperties().get("acm.modules.dashboard.widgets");
        } catch (Exception e)
        {
            throw new AcmDashboardException("Error occurred while fetching dashboard widget names " + e.getMessage(), e);
        }
        return getDashboardWidgetsFromDB(dashboardWidgetsString);
    }

    private List<Widget> transformWidgetNamesArrayToWidgetList(String widgetNamesString)
    {
        List<Widget> widgets = new ArrayList<>();
        String[] widgetsNames;
        if (!"".equals(widgetNamesString))
        {
            widgetsNames = widgetNamesString.split(",");
            for (String widgetName : widgetsNames)
            {
                Widget widget = new Widget();
                widget.setWidgetName(widgetName.trim());
                widgets.add(widget);
            }
        }
        return widgets;
    }

    private void updateModuleTable()
    {
        moduleNameList.forEach(module ->
        {
            try
            {
                moduleDao.getModuleByName(module);
            } catch (AcmObjectNotFoundException e)
            {
                Module m = new Module();
                m.setModuleName(module);
                try
                {
                    moduleDao.save(m);
                    log.info("Module with module name: [{}] is added! ", m.getModuleId());
                    moduleEventPublisher.publishModuleCreated(m, null, null, true);
                } catch (Exception e1)
                {
                    log.error("Persisting new module name failed due to error: [{}]", e1.getMessage(), e1);
                    moduleEventPublisher.publishModuleCreated(m, null, null, false);
                }
            }
        });
    }

    private void addNewWidgets()
    {
        //get all widgets added in the dashboardPlugin.properties config file;
        Set<Widget> setOfAllWidgetsFromPropertyFile = new HashSet<>(getWidgetList());

        Set<Widget> widgetsThatNeedToBeInserted = new HashSet<>();
        Set<Widget> widgetsThatNeedToBeDeleted = new HashSet<>();

        //check if there are already widgets in the DB and if widgets found compare with the list from property file and
        //made appropriate changes in the DB if needed, if widgets are not found insert all of them from the property file
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

            widgetsThatNeedToBeDeleted.forEach(widget ->
            {
                widgetDao.deleteAllWidgetRolesByWidgetName(widget.getWidgetName());
                widgetDao.deleteWidget(widget);
            });

            widgetsThatNeedToBeInserted.forEach(widget -> widgetDao.saveWidget(widget));
        } else
        {
            setOfAllWidgetsFromPropertyFile.forEach(widget -> widgetDao.saveWidget(widget));
        }
    }

    private List<Widget> getDashboardWidgetsFromDB(String dashboardWidgets)
    {
        List<Widget> widgetList = new ArrayList<>();
        String[] widgetNames = dashboardWidgets.split(",");
        List<String> widgetNamesList = Arrays.asList(widgetNames);

        widgetNamesList.forEach(widget ->
        {
            try
            {
                widgetList.add(widgetDao.getWidgetByWidgetName(widget));
            } catch (AcmObjectNotFoundException e)
            {
                log.error("Fetching widget with widget name: [{}] failed! Error msg: [{}]", widget, e.getMessage(), e);
            }
        });
        return widgetList;
    }

    public AcmPlugin getDashboardPlugin()
    {
        return dashboardPlugin;
    }

    public void setDashboardPlugin(AcmPlugin dashboardPlugin)
    {
        this.dashboardPlugin = dashboardPlugin;
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
}
