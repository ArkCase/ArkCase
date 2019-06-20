package com.armedia.acm.auth;

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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The ArkCase external authentication uses this filter class to call the AcmLoginSuccessOperations class after
 * external authentication, to ensure the HTTP session is setup correctly. It is used from
 * $HOME/.arkcase/acm/spring-security/spring-security-config-external.xml.
 */
public class AcmPreAuthenticatedLoginHandler extends RequestHeaderAuthenticationFilter
{
    private transient final Logger LOG = LogManager.getLogger(getClass());

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
