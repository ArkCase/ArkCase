package com.armedia.acm.pluginmanager.web;

import com.armedia.acm.pluginmanager.model.AcmPluginUrlPrivilege;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Ensure the user has the required privilege to execute a plugin URL.  Only URLs in the /plugin namespace are
 * handled by this interceptor.
 */
public class AcmPluginRoleBasedAccessInterceptor extends HandlerInterceptorAdapter
{
    private AcmPluginManager acmPluginManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        String method = request.getMethod();
        String url = request.getServletPath();

        if ( log.isDebugEnabled() )
        {
            log.debug("Checking user privilege for url: " + method + " " + url);
        }

        HttpSession session = request.getSession(false);

        if ( session == null )
        {
            // no session yet - login must not have completed?  Anyway, proceed with the next handler.
            return true;
        }

        Map<String, Boolean> userPrivileges = (Map<String, Boolean>) session.getAttribute("acm_privileges");
        if ( userPrivileges == null )
        {
            // no user privileges.  somehow the user is logged in and is calling a plugin URL, but does not have
            // privileges in the user session.  Somehow the login success handler did not run.  This is an
            // anomalous situation.  Better return HTTP 403.
            String message = "Unknown user privileges; you do not have access to " + request.getServletPath();
            sendErrorResponse(HttpStatus.FORBIDDEN, message, response);

            return false;

        }

        List<AcmPluginUrlPrivilege> urlPrivileges = getAcmPluginManager().getUrlPrivileges();

        for ( AcmPluginUrlPrivilege urlPrivilege : urlPrivileges )
        {
            if ( urlPrivilege.matches(url, method) )
            {
                return true;
            }
        }

        String message = "You do not have privileges for " + method + " " + url;
        sendErrorResponse(HttpStatus.FORBIDDEN, message, response);

        return false;
    }

    public void sendErrorResponse(HttpStatus httpStatus, String message, HttpServletResponse response) throws IOException
    {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        byte[] bytes = message.getBytes();
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
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
