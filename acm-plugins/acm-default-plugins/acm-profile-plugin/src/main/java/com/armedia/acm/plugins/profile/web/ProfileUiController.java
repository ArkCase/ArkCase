package com.armedia.acm.plugins.profile.web;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.armedia.acm.web.AcmPageDescriptor;

@RequestMapping("/plugin/profile")
public class ProfileUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPageDescriptor pageDescriptor;
    private AcmPlugin plugin;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showHomePage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("profile");
        String dashboardFile = null;
        //plugin.getPluginProperties()
        retval.addObject("pageDescriptor", getPageDescriptor());
        return retval;
    }

    public AcmPageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }

    public void setPageDescriptor(AcmPageDescriptor pageDescriptor) {
        this.pageDescriptor = pageDescriptor;
    }

    public AcmPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(AcmPlugin plugin) {
        this.plugin = plugin;
    }
}
