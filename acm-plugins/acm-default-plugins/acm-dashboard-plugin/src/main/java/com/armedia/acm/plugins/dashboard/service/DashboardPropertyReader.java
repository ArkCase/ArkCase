package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
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
    private Logger log = LoggerFactory.getLogger(getClass());

    private List<String> moduleNameList;
    private List<Widget> widgetList;

    private void init()
    {
        if (log.isInfoEnabled())
        {
            log.info("Initializing - setting moduleNameList and widgetList in the  DashboardPropertyReader bean");
        }
        try
        {
            this.moduleNameList = getModuleNamesAndCreateList();
        } catch (AcmDashboardException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Module name list was not populated, error occurred: " + e.getMessage(), e);
            }
        }
        try
        {
            this.widgetList = readWidgetNamesAndCreateWidgetList();
        } catch (AcmDashboardException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Widgets list was not populated, error occurred: " + e.getMessage(), e);
            }
        }
    }


    private List<String> getModuleNamesAndCreateList() throws AcmDashboardException
    {
        String modulesString;
        try
        {
            if (log.isInfoEnabled())
            {
                log.info("Fetching all module names from the property file");
            }
            modulesString = (String) dashboardPlugin.getPluginProperties().get("acm.modules");
        } catch (Exception e)
        {
            throw new AcmDashboardException("Error occurred while fetching module names " + e.getMessage(), e);
        }


        String[] modules;
        List<String> moduleList = new ArrayList<>();

        if (!"".equals(modulesString) && modulesString.contains(","))
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
            newWidgetsString = (String) dashboardPlugin.getPluginProperties().get("acm.new.widgets");
        } catch (Exception e)
        {
            throw new AcmDashboardException("Error occured while fetching widget names " + e.getMessage(), e);
        }


        String[] newWidgetsNames;
        List<Widget> widgetList = new ArrayList<>();
        if (!"".equals(newWidgetsString))
        {
            newWidgetsNames = newWidgetsString.split(",");
            for (String widgetName : newWidgetsNames)
            {
                Widget widget = new Widget();
                widget.setWidgetName(widgetName.trim());
                widgetList.add(widget);

            }
        }
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
}
