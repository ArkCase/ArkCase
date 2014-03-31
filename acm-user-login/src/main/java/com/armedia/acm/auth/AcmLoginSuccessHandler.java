package com.armedia.acm.auth;


import com.armedia.acm.pluginmanager.AcmPlugin;
import com.armedia.acm.pluginmanager.AcmPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

public class AcmLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmPluginManager acmPluginManager;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws ServletException, IOException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Authentication details is of type: " + (authentication.getDetails() == null ? null :
                    authentication.getDetails().getClass().getName()) );
        }
        addUserIdToSession(request, authentication);
        addNavigatorPluginsToSession(request, authentication);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    protected void addUserIdToSession(HttpServletRequest request, Authentication authentication)
    {
        String userId = authentication.getName();

        HttpSession session = request.getSession(true);
        session.setAttribute("acm_username", userId);

        if ( log.isDebugEnabled() )
        {
            log.debug("Session 'acm_username' set to '" + userId + "'");
        }
    }

    protected void addNavigatorPluginsToSession(HttpServletRequest request, Authentication authentication)
    {
        Collection<AcmPlugin> plugins = getAcmPluginManager().getEnabledNavigatorPlugins();

        if ( log.isDebugEnabled() )
        {
            log.debug("Adding " + plugins.size() + " plugins to user session.");
        }

        HttpSession session = request.getSession(true);

        session.setAttribute("acm_navigator_plugins", plugins);
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
