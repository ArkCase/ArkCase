package com.armedia.acm.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Adds authentication details to SAML authentication token.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 26.07.2016.
 */
public class AcmSamlProcessingFilter extends SAMLProcessingFilter
{
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException
    {
        Authentication authentication = super.attemptAuthentication(request, response);
        if (authentication instanceof AcmAuthentication)
        {
            AcmAuthentication acmAuthentication = (AcmAuthentication) authentication;
            acmAuthentication.setDetails(authenticationDetailsSource.buildDetails(request));
            return acmAuthentication;
        }
        return authentication;
    }
}
