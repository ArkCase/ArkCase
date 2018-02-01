package com.armedia.acm.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

/**
 * Filter that clears the {@link Authentication} object from the {@link SecurityContext} and returns 401 Unauthorized
 * error. Only used in
 * the Single Sign-On scenario. This filter is used for REST calls and does not cause a redirect to /samllogin page, but
 * lets
 * {@link org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler} return 401 error.
 * <p>
 * Created by Bojan Milenkoski on 12.4.2016
 */
public class AcmSamlRestAuthenticationCheckFilter extends AcmSamlAuthenticationCheckFilterBase
{

    @Override
    public boolean shouldRedirectToLoginPage()
    {
        // REST services should return 401, not redirect
        return false;
    }
}
