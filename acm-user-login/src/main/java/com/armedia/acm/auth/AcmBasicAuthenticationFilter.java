package com.armedia.acm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by armdev on 6/3/14.
 */
public class AcmBasicAuthenticationFilter extends BasicAuthenticationFilter
{
    private Logger log = LoggerFactory.getLogger(getClass());

    public AcmBasicAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        super(authenticationManager);
    }

    @Override
    public void onSuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authResult) throws IOException
    {
        super.onSuccessfulAuthentication(request, response, authResult);

        if  ( log.isDebugEnabled() )
        {
            log.debug(authResult.getName() + " has logged in via basic authentication.");
        }
    }

}
