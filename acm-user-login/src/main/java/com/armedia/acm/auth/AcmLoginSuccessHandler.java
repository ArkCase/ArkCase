package com.armedia.acm.auth;


import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcmLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmPluginManager acmPluginManager;
    private AcmApplication acmApplication;

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

        // need to add the privileges first, since we need to know the user's privileges to tell what navigator
        // tabs he/she can access.
        addPrivilegesToSession(request, authentication);

        Map<String, Boolean> privileges = (Map<String, Boolean>) request.getSession(true).getAttribute("acm_privileges");
        addNavigatorPluginsToSession(request, privileges);


        addIpAddressToSession(request, authentication);

        addAcmApplicationToSession(request);

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

    protected void addIpAddressToSession(HttpServletRequest request, Authentication authentication)
    {
        String ipAddress = "";

        HttpSession session = request.getSession(true);

        if ( authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }

        session.setAttribute("acm_ip_address", ipAddress);

        if ( log.isDebugEnabled() )
        {
            log.debug("Session 'acm_ip_address' set to '" + ipAddress + "'");
        }
    }

    protected void addNavigatorPluginsToSession(HttpServletRequest request, Map<String, Boolean> userPrivileges)
    {
        Collection<AcmPlugin> plugins = getAcmPluginManager().findAccessiblePlugins(userPrivileges);

        if ( log.isDebugEnabled() )
        {
            log.debug("Adding " + plugins.size() + " plugins to user session.");
        }

        HttpSession session = request.getSession(true);

        session.setAttribute("acm_navigator_plugins", plugins);
    }

    public void addPrivilegesToSession(HttpServletRequest request, Authentication authentication)
    {
        List<String> allPrivileges = new ArrayList<>();

        if ( authentication.getAuthorities() != null )
        {
            for ( GrantedAuthority authority : authentication.getAuthorities() )
            {
                List<String> privileges = getAcmPluginManager().getPrivilegesForRole(authority.getAuthority());
                allPrivileges.addAll(privileges);
            }
        }

        // we have to put a map in the session because of how JSTL works.  It's easier to check for
        // a map entry than to see if an element exists in a list.
        Map<String, Boolean> privilegeMap = new HashMap<>();
        for ( String privilege : allPrivileges )
        {
            privilegeMap.put(privilege, Boolean.TRUE);
        }

        HttpSession session = request.getSession(true);

        session.setAttribute("acm_privileges", privilegeMap);

        if ( log.isDebugEnabled() )
        {
            log.debug("Added " + privilegeMap.size() + " privileges to user session.");
        }

    }

    public void addAcmApplicationToSession(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);

        session.setAttribute("acm_application", getAcmApplication());

        if ( log.isTraceEnabled() )
        {
            log.trace("Added ACM application named '" + getAcmApplication().getApplicationName() + "' to user session.");
        }

    }
    public AcmPluginManager getAcmPluginManager()
    {
        return acmPluginManager;
    }

    public void setAcmPluginManager(AcmPluginManager acmPluginManager)
    {
        this.acmPluginManager = acmPluginManager;
    }


    public void setAcmApplication(AcmApplication acmApplication)
    {
        this.acmApplication = acmApplication;
    }

    public AcmApplication getAcmApplication()
    {
        return acmApplication;
    }


}
