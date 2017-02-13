package com.armedia.acm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The ArkCase external authentication uses this filter class to call the AcmLoginSuccessOperations class after
 * external authentication, to ensure the HTTP session is setup correctly.  It is used from
 * $HOME/.arkcase/acm/spring-security/spring-security-config-external.xml.
 */
public class AcmPreAuthenticatedLoginHandler extends RequestHeaderAuthenticationFilter
{
    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    private AcmLoginSuccessOperations loginSuccessOperations;

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult)
    {
        LOG.debug("Preauthenticated login successful, user {}", authResult.getName());

        super.successfulAuthentication(request, response, authResult);

        getLoginSuccessOperations().onSuccessfulAuthentication(request, authResult);
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
