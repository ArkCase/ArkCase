package com.armedia.acm.auth;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 6/3/14.
 */
public class AcmLoginSuccessOperations
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmPluginManager acmPluginManager;
    private AcmApplication acmApplication;
    private UserDao userDao;

    public void onSuccessfulAuthentication(HttpServletRequest request,
                                           Authentication authentication)
    {
        addUserIdToSession(request, authentication);

        addPrivilegesToSession(request, authentication);

        addIpAddressToSession(request, authentication);

        addAcmApplicationToSession(request);

        addAcmUserToSession(request, authentication);
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

    protected void addPrivilegesToSession(HttpServletRequest request, Authentication authentication)
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

    protected void addAcmApplicationToSession(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);

        session.setAttribute("acm_application", getAcmApplication());

        if ( log.isDebugEnabled() )
        {
            log.debug("Added ACM application named '" + getAcmApplication().getApplicationName() + "' to user session.");
        }

    }

    protected void addAcmUserToSession(HttpServletRequest request, Authentication authentication)
    {
        String userId = authentication.getName();

        AcmUser user = getUserDao().findByUserId(userId);

        HttpSession session = request.getSession(true);

        session.setAttribute("acm_user", user);

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

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }
}
