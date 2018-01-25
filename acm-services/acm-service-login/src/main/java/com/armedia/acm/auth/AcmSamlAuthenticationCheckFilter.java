package com.armedia.acm.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

/**
 * Filter that clears the {@link Authentication} object from the {@link SecurityContext} and causes authentication check
 * of the user against
 * ADFS server. Only used in the Single Sign-On scenario. This filter is used for non REST calls and cause redirect to
 * the /samllogin page
 * where the 'redirectURL' is set to the Angular state.
 * <p>
 * Created by Bojan Milenkoski on 14.3.2016
 */
public class AcmSamlAuthenticationCheckFilter extends AcmSamlAuthenticationCheckFilterBase
{
    @Override
    public boolean shouldRedirectToLoginPage()
    {
        // non REST resources redirect to the /samllogin page
        return true;
    }
}
