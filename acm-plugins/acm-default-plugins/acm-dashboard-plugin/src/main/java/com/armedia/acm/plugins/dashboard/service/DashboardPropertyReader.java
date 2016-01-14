package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
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

    private String newWidgetsString = (String) dashboardPlugin.getPluginProperties().get("acm.new.widgets");


    public List<String> getModuleNameList()
    {
        String modulesString = (String) dashboardPlugin.getPluginProperties().get("acm.modules");
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

    public List<Widget> readWidgetNamesAndCreateWidgetList()
    {
        String newWidgetsString = (String) dashboardPlugin.getPluginProperties().get("acm.new.widgets");
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
}
