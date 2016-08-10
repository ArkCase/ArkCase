package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.dao.ModuleDao;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
import com.armedia.acm.plugins.dashboard.model.module.Module;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 14.01.2016.
 */


public class DashboardPropertyReader
{
    private AcmPlugin dashboardPlugin;
    private ModuleDao moduleDao;
    private WidgetDao widgetDao;
    private ModuleEventPublisher moduleEventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    private List<String> moduleNameList;
    private List<Widget> widgetList;
    private boolean isNewWidgetForAdding = false;
    private List<Widget> dashboardWidgetsOnly;

    private void init()
    {
        isNewWidgetForAdding = Boolean.valueOf((String) dashboardPlugin.getPluginProperties().
                get(DashboardConstants.IS_NEW_DASHBOARD_WIDGETS_FOR_ADDING));

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

        try
        {
            this.dashboardWidgetsOnly = getDashboardWidgets();
        } catch (AcmDashboardException e)
        {
            log.error("Dashboard Widgets list was not populated, error occurred: [{}] ", e.getMessage(), e);
        }
        if (isNewWidgetForAdding)
        {
            addNewWidgets();
        }

        updateModuleTable();
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
                m.trim();
                moduleList.add(m);
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
        moduleNameList.stream().forEach(module ->
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
        List<Widget> widgetList = getWidgetList();
        widgetList.stream().forEach(widget -> widgetDao.saveWidget(widget));
    }

    private List<Widget> getDashboardWidgetsFromDB(String dashboardWidgets)
    {
        List<Widget> widgetList = new ArrayList<>();
        String[] widgetNames = dashboardWidgets.split(",");
        List<String> widgetNamesList = new ArrayList<>();
        for (String widgetName : widgetNames)
        {
            widgetNamesList.add(widgetName);
        }

        widgetNamesList.stream().forEach(widget ->
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

}
