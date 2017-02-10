package com.armedia.acm.auth;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.web.api.MDCConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    public void onSuccessfulAuthentication(HttpServletRequest request, Authentication authentication)
    {
        String internalUserId = addAcmUserToSession(request, authentication);

        addUserIdToSession(request, internalUserId);

        addAlfrescoUserIdToSession(request);

        addPrivilegesToSession(request, authentication);

        addIpAddressToSession(request, authentication);

        addAcmApplicationToSession(request);

        recordAuditPropertyUser(internalUserId);
    }

    private void recordAuditPropertyUser(String userId)
    {
        getAuditPropertyEntityAdapter().setUserId(userId);
    }

    protected void addUserIdToSession(HttpServletRequest request, String userId)
    {
        HttpSession session = request.getSession(true);
        session.setAttribute("acm_username", userId);

        if ( log.isDebugEnabled() )
        {
            log.debug("Session 'acm_username' set to '" + userId + "'");
        }

        // after successful login set the MDC variable (needed for API calls)
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, userId);
    }

    private void addAlfrescoUserIdToSession(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        AcmUser acmUser = (AcmUser) session.getAttribute("acm_user");

        String alfrescoUserId = getAlfrescoUserIdLdapAttributeValue(acmUser);
        session.setAttribute("acm_alfresco_username", alfrescoUserId);

        log.debug("Session 'acm_alfresco_username' set to '{}'", alfrescoUserId);

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, alfrescoUserId);
    }

    private String getAlfrescoUserIdLdapAttributeValue(AcmUser acmUser)
    {
        switch (getAcmApplication().getAlfrescoUserIdLdapAttribute().toLowerCase())
        {
            case "samaccountname":
                return acmUser.getsAMAccountName();
            case "userprincipalname":
                return acmUser.getUserPrincipalName();
            case "uid":
                return acmUser.getUid();
            case "dn":
            case "distinguishedname":
                return acmUser.getDistinguishedName();
            default:
                return acmUser.getsAMAccountName();
        }
    }

    protected void addIpAddressToSession(HttpServletRequest request, Authentication authentication)
    {
        String ipAddress = "";

        HttpSession session = request.getSession(true);

        if ( authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails )
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

        // we have to put a map in the session because of how JSTL works. It's easier to check for
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

        String json;
        ObjectMapper om = new ObjectMapper();
        try
        {
            json = om.writeValueAsString(getAcmApplication().getObjectTypes());
            json = json == null || "null".equals(json) ? "[]" : json;
            session.setAttribute("acm_object_types", json);
        } catch (IOException e)
        {
            log.error(e.getMessage());
            session.setAttribute("acm_object_types", "[]");
        }

        if ( log.isDebugEnabled() )
        {
            log.debug("Added ACM application named '" + getAcmApplication().getApplicationName() + "' to user session.");
        }

    }

    protected String addAcmUserToSession(HttpServletRequest request, Authentication authentication)
    {
        String userId = authentication.getName();

        AcmUser user = getUserDao().findByUserId(userId);

        HttpSession session = request.getSession(true);

        session.setAttribute("acm_user", user);

        return user.getUserId();

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

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
