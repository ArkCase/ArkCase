package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static java.time.temporal.ChronoUnit.DAYS;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ApplicationRolesToPrivilegesConfig;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 6/3/14.
 */
public class AcmLoginSuccessOperations
{
    private static final int DAYS_TO_PASSWORD_EXPIRATION = 10;
    private Logger log = LogManager.getLogger(getClass());
    private AcmApplication acmApplication;
    private UserDao userDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private ObjectConverter objectConverter;
    private ApplicationConfig applicationConfig;
    private ExternalAuthenticationUtils externalAuthenticationUtils;
    private ApplicationRolesToPrivilegesConfig rolesToPrivilegesConfig;

    public void onSuccessfulAuthentication(HttpServletRequest request, Authentication authentication)
    {
        String internalUserId = addAcmUserToSession(request, authentication);

        addUserIdToSession(request, internalUserId);

        addAlfrescoUserIdToSession(request);

        addAlfrescoUserIdToAuthenticationDetails(authentication);

        addPrivilegesToSession(request, authentication);

        addIpAddressToSession(request, authentication);

        addAcmApplicationToSession(request);

        recordAuditPropertyUser(internalUserId);

        setPasswordExpirationSessionAttribute(request);
    }

    private void addAlfrescoUserIdToAuthenticationDetails(Authentication authentication)
    {
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            String cmisUserId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY);
            ((AcmAuthenticationDetails) authentication.getDetails()).setCmisUserId(cmisUserId);

            log.debug("Set authentication details CMIS user id to {}", cmisUserId);
        }
    }

    protected void setPasswordExpirationSessionAttribute(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        AcmUser acmUser = (AcmUser) session.getAttribute("acm_user");

        LocalDate passwordExpirationDate = acmUser.getPasswordExpirationDate();
        LocalDate today = LocalDate.now();
        if (passwordExpirationDate != null)
        {
            long daysBetween = DAYS.between(today, passwordExpirationDate);
            if (daysBetween <= DAYS_TO_PASSWORD_EXPIRATION)
            {
                String daysToExpiration = daysBetween == 0 ? "today" : String.format("in %d day(s)", daysBetween);
                session.setAttribute("acm_user_message",
                        "Your password expires " + daysToExpiration + ", please change it before expiration date.");
            }
        }
    }

    private void recordAuditPropertyUser(String userId)
    {
        getAuditPropertyEntityAdapter().setUserId(userId);
    }

    protected void addUserIdToSession(HttpServletRequest request, String userId)
    {
        HttpSession session = request.getSession(true);
        session.setAttribute("acm_username", userId);

        log.debug("Session 'acm_username' set to '{}'", userId);

        // after successful login set the MDC variable (needed for API calls)
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, userId);
    }

    private void addAlfrescoUserIdToSession(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        AcmUser acmUser = (AcmUser) session.getAttribute("acm_user");

        String alfrescoUserId = getExternalAuthenticationUtils().getEcmServiceUserId(acmUser);
        session.setAttribute("acm_alfresco_username", alfrescoUserId);

        log.debug("Session 'acm_alfresco_username' set to '{}'", alfrescoUserId);

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, alfrescoUserId);
    }

    protected void addIpAddressToSession(HttpServletRequest request, Authentication authentication)
    {
        String ipAddress = "";

        HttpSession session = request.getSession(true);

        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }

        session.setAttribute("acm_ip_address", ipAddress);

        log.debug("Session 'acm_ip_address' set to '{}'", ipAddress);
    }

    protected void addPrivilegesToSession(HttpServletRequest request, Authentication authentication)
    {
        List<Object> allPrivileges = new ArrayList<>();

        if (authentication.getAuthorities() != null)
        {
            for (GrantedAuthority authority : authentication.getAuthorities())
            {
                List<Object> privileges = getPrivilegesForRole(authority.getAuthority());
                if (privileges != null)
                {
                    allPrivileges.addAll(privileges);
                }
            }
        }

        // we have to put a map in the session because of how JSTL works. It's easier to check for
        // a map entry than to see if an element exists in a list.
        Map<String, Boolean> privilegeMap = new HashMap<>();
        for (Object privilege : allPrivileges)
        {
            privilegeMap.put((String) privilege, Boolean.TRUE);
        }

        HttpSession session = request.getSession(true);

        session.setAttribute("acm_privileges", privilegeMap);

        log.debug("Added {} privileges to user session.", privilegeMap.size());

    }

    protected void addAcmApplicationToSession(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);

        session.setAttribute("acm_application", getAcmApplication());
        session.setAttribute("issue_collector_flag", applicationConfig.getIssueCollectorFlag());
        String json = getObjectConverter().getJsonMarshaller().marshal(getAcmApplication().getObjectTypes());
        json = json == null || "null".equals(json) ? "[]" : json;
        session.setAttribute("acm_object_types", json);

        log.debug("Added ACM application named '{}' to user session.", applicationConfig.getApplicationName());

    }

    protected String addAcmUserToSession(HttpServletRequest request, Authentication authentication)
    {
        String userId = authentication.getName();

        AcmUser user = getUserDao().findByUserId(userId);

        HttpSession session = request.getSession(true);

        session.setAttribute("acm_user", user);

        return user.getUserId();

    }

    public List<Object> getPrivilegesForRole(String role)
    {
        if (rolesToPrivilegesConfig.getRolesToPrivileges().containsKey(role))
        {
            return Collections.unmodifiableList(rolesToPrivilegesConfig.getRolesToPrivileges().get(role));
        }
        else
        {
            String wildCardRole = StringUtils.substringBeforeLast(role, "@") + "@*";
            if (rolesToPrivilegesConfig.getRolesToPrivileges().containsKey(wildCardRole))
            {
                return Collections.unmodifiableList(rolesToPrivilegesConfig.getRolesToPrivileges().get(wildCardRole));
            }
            else
            {
                return Collections.emptyList();
            }
        }
    }

    public AcmApplication getAcmApplication()
    {
        return acmApplication;
    }

    public void setAcmApplication(AcmApplication acmApplication)
    {
        this.acmApplication = acmApplication;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig)
    {
        this.applicationConfig = applicationConfig;
    }

    public ExternalAuthenticationUtils getExternalAuthenticationUtils()
    {
        return externalAuthenticationUtils;
    }

    public void setExternalAuthenticationUtils(ExternalAuthenticationUtils externalAuthenticationUtils)
    {
        this.externalAuthenticationUtils = externalAuthenticationUtils;
    }

    public void setRolesToPrivilegesConfig(ApplicationRolesToPrivilegesConfig rolesToPrivilegesConfig)
    {
        this.rolesToPrivilegesConfig = rolesToPrivilegesConfig;
    }
}
