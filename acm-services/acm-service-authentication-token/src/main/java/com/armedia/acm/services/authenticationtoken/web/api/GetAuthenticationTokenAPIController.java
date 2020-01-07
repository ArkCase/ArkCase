package com.armedia.acm.services.authenticationtoken.web.api;

/*-
 * #%L
 * ACM Service: Authentication Tokens
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

import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private final Logger log = LogManager.getLogger(getClass());
    private AuthenticationTokenService authenticationTokenService;

    /**
     * REST service to retrieve a token for an authorized user; i.e. the HTTP client should provide a Basic
     * Authentication when calling this URL. The token is returned in the response body.
     *
     * @param authentication
     *            Provided automatically by Spring MVC.
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
