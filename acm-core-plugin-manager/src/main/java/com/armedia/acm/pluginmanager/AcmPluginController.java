package com.armedia.acm.pluginmanager;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
@RequestMapping("/service/plugins")
public class AcmPluginController
{
    private AcmPluginManager acmPluginManager;

    @RequestMapping(value="/navigatorPlugins", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Collection<AcmPlugin> enabledNavigatorPlugins(Authentication authentication)
    {
        Collection<AcmPlugin> enabledNavigatorPlugins = getAcmPluginManager().getEnabledNavigatorPlugins();

        return enabledNavigatorPlugins;
    }

    public AcmPluginManager getAcmPluginManager()
    {
        return acmPluginManager;
    }

    public void setAcmPluginManager(AcmPluginManager acmPluginManager)
    {
        this.acmPluginManager = acmPluginManager;
    }
}
