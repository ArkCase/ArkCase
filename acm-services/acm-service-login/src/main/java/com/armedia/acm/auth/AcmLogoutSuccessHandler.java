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

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AcmLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;
    private AcmAuthenticationDetailsFactory authenticationDetailsFactory;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            Authentication authentication) throws IOException, ServletException
    {
        // sometimes we can get here even when there is no actual authentication (e.g. someone
        // has invoked the logout url without having actually logged in)
        if (authentication != null)
        {

            AcmAuthenticationDetails details = getAuthenticationDetailsFactory().buildDetails(httpServletRequest);
            AcmAuthentication auth = new AcmAuthentication(null, authentication.getCredentials(),
                    details, authentication.isAuthenticated(), authentication.getName());
            getApplicationEventPublisher().publishEvent(new LogoutEvent(auth));
        }

        super.onLogoutSuccess(httpServletRequest, httpServletResponse, authentication);
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public AcmAuthenticationDetailsFactory getAuthenticationDetailsFactory()
    {
        return authenticationDetailsFactory;
    }

    public void setAuthenticationDetailsFactory(AcmAuthenticationDetailsFactory authenticationDetailsFactory)
    {
        this.authenticationDetailsFactory = authenticationDetailsFactory;
    }
}
