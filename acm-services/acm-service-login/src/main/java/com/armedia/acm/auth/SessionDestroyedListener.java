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

import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;

/**
 * Clear authentication tickets once session has ended
 **/
public class SessionDestroyedListener implements ApplicationListener<SessionDestroyedEvent>
{
    private transient final Logger LOG = LogManager.getLogger(getClass());
    private AuthenticationTokenService authenticationTokenService;

    @Override
    public void onApplicationEvent(SessionDestroyedEvent sessionDestroyedEvent)
    {
        String invalidatedSessionId = sessionDestroyedEvent.getId();
        if (invalidatedSessionId != null)
        {
            LOG.debug("Invalidate all acm_tickets for session with id:{}", invalidatedSessionId);
            authenticationTokenService.purgeTokenForAuthenticationPerSession(invalidatedSessionId);
        }
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
