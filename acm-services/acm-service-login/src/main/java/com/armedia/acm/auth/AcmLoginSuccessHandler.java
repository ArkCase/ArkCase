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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class AcmLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
{
    private Logger log = LogManager.getLogger(getClass());

    private AcmLoginSuccessOperations loginSuccessOperations;
    private SessionRegistry sessionRegistry;
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private List<String> ignoreSavedUrls;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException
    {
        log.debug("Authentication details is of type: {}",
                authentication.getDetails() == null ? null : authentication.getDetails().getClass().getName());

        getLoginSuccessOperations().onSuccessfulAuthentication(request, authentication);
        SavedRequest savedRequest = (SavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (savedRequest != null)
        {
            String redirectUrl = savedRequest.getRedirectUrl();
            if (redirectUrl != null)
            {
                String contextPath = request.getContextPath();
                if (ignoreSavedUrls.stream().anyMatch(url -> redirectUrl.contains(contextPath + url)))
                {
                    request.getSession().removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
                }
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
        sessionRegistry.registerNewSession(request.getSession().getId(), authentication.getPrincipal());
        sessionAuthenticationStrategy.onAuthentication(authentication, request, response);
    }

    public AcmLoginSuccessOperations getLoginSuccessOperations()
    {
        return loginSuccessOperations;
    }

    public void setLoginSuccessOperations(AcmLoginSuccessOperations loginSuccessOperations)
    {
        this.loginSuccessOperations = loginSuccessOperations;
    }

    public void setSessionRegistry(SessionRegistry sessionRegistry)
    {
        this.sessionRegistry = sessionRegistry;
    }

    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionAuthenticationStrategy)
    {
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
    }

    public void setIgnoreSavedUrls(List<String> ignoreSavedUrls)
    {
        this.ignoreSavedUrls = ignoreSavedUrls;
    }
}
