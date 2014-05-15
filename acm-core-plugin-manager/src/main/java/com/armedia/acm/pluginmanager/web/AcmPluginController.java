package com.armedia.acm.pluginmanager.web;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Map;

@Controller
@RequestMapping("/service/plugins")
public class AcmPluginController
{
    private AcmPluginManager acmPluginManager;
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * REST service to get the list of accessible plugins.  It is NOT used by the ACM webapp (the webapp puts the
     * accessible plugins in the HTTP session at login time).  This service is to ensure the plugin list is
     * available via REST.
     */
    @RequestMapping(value="/navigatorPlugins", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Collection<AcmPlugin> enabledNavigatorPlugins(HttpSession userSession)
    {
        Map<String, Boolean> userPrivileges = (Map<String, Boolean>) userSession.getAttribute("acm_privileges");
        Collection<AcmPlugin> enabledNavigatorPlugins = getAcmPluginManager().findAccessiblePlugins(userPrivileges);

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
