package com.armedia.acm.plugins.dashboard.web;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.armedia.acm.web.AcmPageDescriptor;

@RequestMapping({"/plugin/dashboard", "/", "/home"})
public class DashboardUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPageDescriptor pageDescriptor;
    private AcmPlugin dashboardPlugin;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showHomePage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("home");
        String dashboardFile = null;
        if(!dashboardPlugin.getPluginProperties().isEmpty() && dashboardPlugin.getPluginProperties().containsKey("acm.dashboardFile")) {
            dashboardFile = (String) dashboardPlugin.getPluginProperties().get("acm.dashboardFile");
        } else {
            if(log.isErrorEnabled()) {
                log.error("dashboardPlugin.properties or property value is missing, users will not have dashboard");
            }
        }
        retval.addObject("dashboardFileName", dashboardFile);
        retval.addObject("pageDescriptor", getPageDescriptor());
        return retval;
    }

    public AcmPageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }

    public void setPageDescriptor(AcmPageDescriptor pageDescriptor) {
        this.pageDescriptor = pageDescriptor;
    }

    public AcmPlugin getDashboardPlugin() {
        return dashboardPlugin;
    }

    public void setDashboardPlugin(AcmPlugin dashboardPlugin) {
        this.dashboardPlugin = dashboardPlugin;
    }
}
