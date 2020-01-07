package com.armedia.acm.auth.okta.auth;

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

import com.armedia.acm.auth.AcmLoginSuccessOperations;
import com.armedia.acm.auth.okta.model.OktaConfig;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AcmMultiFactorLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
{
    private Logger LOGGER = LogManager.getLogger(AcmMultiFactorLoginSuccessHandler.class);
    private SessionRegistry sessionRegistry;
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private AcmLoginSuccessOperations loginSuccessOperations;
    private OktaConfig oktaConfig;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException
    {
        LOGGER.debug("Authentication details is of type: {}",
                authentication.getDetails() == null ? null : authentication.getDetails().getClass().getName());
        getLoginSuccessOperations().onSuccessfulAuthentication(request, authentication);
        super.onAuthenticationSuccess(request, response, authentication);
        sessionRegistry.registerNewSession(request.getSession().getId(), authentication.getPrincipal());
        sessionAuthenticationStrategy.onAuthentication(authentication, request, response);
    }

    public OktaConfig getOktaConfig()
    {
        return oktaConfig;
    }

    public void setOktaConfig(OktaConfig oktaConfig)
    {
        this.oktaConfig = oktaConfig;
    }

    @Override
    public void setAlwaysUseDefaultTargetUrl(boolean alwaysUseDefaultTargetUrl)
    {
        super.setAlwaysUseDefaultTargetUrl(oktaConfig.getAlwaysUseDefaultUrl());
    }

    @Override
    public void setDefaultTargetUrl(String defaultTargetUrl)
    {
        super.setDefaultTargetUrl(oktaConfig.getDefaultLoginTargetUrl());
    }

    public SessionRegistry getSessionRegistry()
    {
        return sessionRegistry;
    }

    public void setSessionRegistry(SessionRegistry sessionRegistry)
    {
        this.sessionRegistry = sessionRegistry;
    }

    public SessionAuthenticationStrategy getSessionAuthenticationStrategy()
    {
        return sessionAuthenticationStrategy;
    }

    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionAuthenticationStrategy)
    {
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
    }

    public AcmLoginSuccessOperations getLoginSuccessOperations()
    {
        return loginSuccessOperations;
    }

    public void setLoginSuccessOperations(AcmLoginSuccessOperations loginSuccessOperations)
    {
        this.loginSuccessOperations = loginSuccessOperations;
    }
}
