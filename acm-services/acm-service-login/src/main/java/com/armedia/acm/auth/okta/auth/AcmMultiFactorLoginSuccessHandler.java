package com.armedia.acm.auth.okta.auth;

import com.armedia.acm.auth.AcmLoginSuccessOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger LOGGER = LoggerFactory.getLogger(AcmMultiFactorLoginSuccessHandler.class);
    private AcmMultiFactorConfig acmMultiFactorConfig;
    private SessionRegistry sessionRegistry;
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private AcmLoginSuccessOperations loginSuccessOperations;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException
    {
        LOGGER.debug("Authentication details is of type: {}",
                authentication.getDetails() == null ? null : authentication.getDetails().getClass().getName());
        getLoginSuccessOperations().onSuccessfulAuthentication(request, authentication);
        super.onAuthenticationSuccess(request, response, authentication);
        sessionRegistry.registerNewSession(request.getSession().getId(), authentication.getPrincipal());
        sessionAuthenticationStrategy.onAuthentication(authentication, request, response);
    }

    public AcmMultiFactorConfig getAcmMultiFactorConfig()
    {
        return acmMultiFactorConfig;
    }

    public void setAcmMultiFactorConfig(AcmMultiFactorConfig acmMultiFactorConfig)
    {
        this.acmMultiFactorConfig = acmMultiFactorConfig;
    }

    @Override
    public void setAlwaysUseDefaultTargetUrl(boolean alwaysUseDefaultTargetUrl)
    {
        super.setAlwaysUseDefaultTargetUrl(getAcmMultiFactorConfig().isAlwaysUseDefaultUrl());
    }

    @Override
    public void setDefaultTargetUrl(String defaultTargetUrl)
    {
        super.setDefaultTargetUrl(getAcmMultiFactorConfig().getDefaultLoginTargetUrl());
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
