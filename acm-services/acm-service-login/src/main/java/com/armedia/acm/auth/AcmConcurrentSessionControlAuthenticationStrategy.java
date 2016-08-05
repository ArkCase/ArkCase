package com.armedia.acm.auth;

import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
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

    /**
     * @param sessionRegistry            the session registry which should be updated when the authenticated session is changed.
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
            // We permit unlimited logins
            return;
        }

        String token = request.getParameter("acm_ticket");
        if (token != null)
        {
            //if token is found, check if it is associated with the authenticated user
            Authentication tokenAuthentication = authenticationTokenService.getAuthenticationForToken(token);

            if (tokenAuthentication != null)
            {
                String tokenAuthenticationPrincipal = tokenAuthentication.getName();
                String principal = authentication.getName();

                    if (tokenAuthenticationPrincipal.equals(principal))
                    {
                        //This is the same user with external request and new session
                        return;
                    }
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

        allowableSessionsExceeded(sessions, allowedSessions, sessionRegistry);
    }
}
