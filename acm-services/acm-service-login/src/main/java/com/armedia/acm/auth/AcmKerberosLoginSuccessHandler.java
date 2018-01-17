package com.armedia.acm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * When Kerberos is used, the Kerberos authentication results in a second request being sent by the
 * browser, and we don't need the savedRequestAware features of the normal AcmLoginSuccessHandler. So we
 * only use this simple handler, which only sets up the user session, and does not do any request
 * forwarding.
 * <p>
 * Created by dmiller on 6/24/16.
 */
public class AcmKerberosLoginSuccessHandler implements AuthenticationSuccessHandler
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmLoginSuccessOperations loginSuccessOperations;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws ServletException, IOException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Authentication details is of type: "
                    + (authentication.getDetails() == null ? null : authentication.getDetails().getClass().getName()));
        }

        getLoginSuccessOperations().onSuccessfulAuthentication(request, authentication);
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
