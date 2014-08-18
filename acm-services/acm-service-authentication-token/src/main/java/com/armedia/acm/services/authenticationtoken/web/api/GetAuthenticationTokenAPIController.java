package com.armedia.acm.services.authenticationtoken.web.api;

import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = { "/api/v1/authenticationtoken", "/api/latest/authenticationtoken" })
public class GetAuthenticationTokenAPIController
{
    private AuthenticationTokenService authenticationTokenService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * REST service to retrieve a token for an authorized user; i.e. the HTTP client should provide a Basic
     * Authentication when calling this URL.  The token is returned in the response body.
     *
     * @param authentication Provided automatically by Spring MVC.
     * @return A token that can be used to represent the Authentication.
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String authenticationtoken(Authentication authentication)
    {
        log.debug("Storing authentication token for user: '" + authentication.getName() + "'");

        String token = getAuthenticationTokenService().getTokenForAuthentication(authentication);
        return token;
    }

    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }
}
