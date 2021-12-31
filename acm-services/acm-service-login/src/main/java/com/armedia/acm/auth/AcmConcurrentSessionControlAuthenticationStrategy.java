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

import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.List;

public class AcmConcurrentSessionControlAuthenticationStrategy extends ConcurrentSessionControlAuthenticationStrategy
{
    private final SessionRegistry sessionRegistry;

    private final AuthenticationTokenService authenticationTokenService;

    private static final Logger logger = LogManager.getLogger(AcmConcurrentSessionControlAuthenticationStrategy.class);

    /**
     * @param sessionRegistry
     *            the session registry which should be updated when the authenticated session is changed.
     * @param authenticationTokenService
     */
    public AcmConcurrentSessionControlAuthenticationStrategy(SessionRegistry sessionRegistry,
            AuthenticationTokenService authenticationTokenService)
    {
        super(sessionRegistry);
        this.sessionRegistry = sessionRegistry;
        this.authenticationTokenService = authenticationTokenService;
    }

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response)
    {
        final List<SessionInformation> sessions = sessionRegistry.getAllSessions(authentication.getPrincipal(), false);

        int sessionCount = sessions.size();
        int allowedSessions = getMaximumSessionsForThisUser(authentication);

        if (sessionCount < allowedSessions)
        {
            // They haven't got too many login sessions running at present
            return;
        }

        if (allowedSessions == -1)
        {
            // We permit unlimited login
            return;
        }

        String principal = authentication.getName();

        String acmTicket = request.getParameter("acm_ticket");
        if (acmTicket != null)
        {
            // if token is found, check if it is associated with the authenticated user
            Authentication tokenAuthentication = authenticationTokenService.getAuthenticationForToken(acmTicket);

            if (tokenAuthentication != null)
            {
                String tokenAuthenticationPrincipal = tokenAuthentication.getName();

                if (principal.equals(tokenAuthenticationPrincipal))
                {
                    // This is the same user with external request and new session
                    return;
                }
            }
        }

        String emailTicket = request.getParameter("acm_email_ticket");
        if (emailTicket != null)
        {
            AuthenticationToken token = authenticationTokenService.findByKey(emailTicket);
            if (token.isActive() && token.getCreator().equals(principal))
            {
                // This is the same user with external request and new session
                return;
            }
        }

        if (sessionCount == allowedSessions)
        {
            HttpSession session = request.getSession(false);

            if (session != null)
            {
                // Only permit it though if this request is associated with one of the already registered sessions
                for (SessionInformation si : sessions)
                {
                    if (si.getSessionId().equals(session.getId()))
                    {
                        return;
                    }

                }

            }
            // If the session is null, a new one will be created by the parent class, exceeding the allowed number
        }

        logger.warn("Allowed sessions [{}] for user [{}] exceeded.", allowedSessions, authentication.getName());
        allowableSessionsExceeded(sessions, allowedSessions, sessionRegistry);
    }

}
