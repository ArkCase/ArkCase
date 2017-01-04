package com.armedia.acm.pluginmanager.web;

import com.armedia.acm.pluginmanager.model.AcmPluginUrlPrivilege;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

/**
 * Ensure the user has the required privilege to execute a plugin URL. Only URLs in the /plugin namespace are handled by
 * this interceptor.
 */
public class AcmPluginRoleBasedAccessInterceptor extends HandlerInterceptorAdapter
{
    private AcmPluginManager acmPluginManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        String method = request.getMethod();
        String url = request.getServletPath();

        log.debug("Checking user privilege for url: {} {}", method, url);

        HttpSession session = request.getSession(false);

        if (session == null)
        {
            // no session yet - login must not have completed? Anyway, proceed with the next handler.
            return true;
        }

        Map<String, Boolean> userPrivileges = (Map<String, Boolean>) session.getAttribute("acm_privileges");
        if (userPrivileges == null)
        {
            // no user privileges. somehow the user is logged in and is calling a plugin URL, but does not have
            // privileges in the user session. Somehow the login success handler did not run. This is an
            // anomalous situation. Better return HTTP 403.
            response.setStatus(response.SC_FORBIDDEN);
        } else
        {
            List<AcmPluginUrlPrivilege> urlPrivileges = getAcmPluginManager().getUrlPrivileges();
            boolean hasPrivilege = determinePrivilege(method, url, userPrivileges, urlPrivileges);

            if (!hasPrivilege)
            {
                response.setStatus(response.SC_FORBIDDEN);
            }
        }

        return true;
    }

    protected boolean determinePrivilege(String method, String url, Map<String, Boolean> userPrivileges,
            List<AcmPluginUrlPrivilege> urlPrivileges)
    {
        boolean hasPrivilege = false;
        for (AcmPluginUrlPrivilege urlPrivilege : urlPrivileges)
        {
            if (urlPrivilege.matches(url, method))
            {
                String requiredPrivilege = urlPrivilege.getRequiredPrivilege().getPrivilegeName();
                log.debug("Required privilege for {} {}: {}; user has privilege: {}", method, url, requiredPrivilege,
                        userPrivileges.containsKey(requiredPrivilege));
                hasPrivilege = userPrivileges.containsKey(requiredPrivilege) ? userPrivileges.get(requiredPrivilege) : false;
            }
        }
        return hasPrivilege;
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
